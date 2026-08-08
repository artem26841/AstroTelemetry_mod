package com.science.astrotelemetry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;

public class TelemetryHud {

    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event == null || event.getOverlay() == null || event.getOverlay().id() == null) return;

        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.level == null || mc.player == null || mc.font == null || event.getGuiGraphics() == null) return;

            Player player = mc.player;
            if (ZoneManager.getZones() != null) {
                for (TelemetryZone zone : ZoneManager.getZones()) {
                    if (zone != null && zone.isPlayerInside(player.getX(), player.getY(), player.getZ())) {
                        renderTickerData(event.getGuiGraphics(), mc.font, zone, player);
                        break;
                    }
                }
            }
        }
    }

    private static void renderTickerData(GuiGraphics graphics, Font font, TelemetryZone zone, Player player) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        long gameTime = player.level().getGameTime();
        int textColor = 0xFF00FF00; // Зеленый матричный цвет

        // Позиция по вертикали над инвентарем
        int y = screenHeight - 55; 

        // СТРОКА 1: Статичное название главного компьютера
        String titleText = "[ ЦВМ БОРТ-КОМПЬЮТЕР V1.2 ]";
        int titleX = screenWidth / 2 - font.width(titleText) / 2;
        graphics.drawString(font, titleText, titleX, y, textColor, false);

        // СТРОКА 2: Сбор всех данных в одну новостную ленту в ряд
        double noise = (gameTime % 20 == 0) ? Math.random() * 0.02 : 0.005;
        int rainPercent = (int)(player.level().getRainLevel(1.0F) * 100);
        
        String dataText = " »»» [СТАТУС]: СЕТЬ ПОДКЛЮЧЕНА -> [ОБЪЕКТ]: " + zone.getName().toUpperCase() + 
                          " -> [ВЫСОТА]: " + String.format("%.1f", player.getY()) + "м" +
                          " -> [ЧАСТОТА]: " + String.format("%.4f", 1420.4 + noise) + " MHz" +
                          " -> [ПОГОДА]: ИСКАЖЕНИЕ " + rainPercent + "%" +
                          " -> [ТЕЛЕМЕТРИЯ]: ДАННЫЕ СТАБИЛЬНЫ »»»             ";

        int textWidth = font.width(dataText);
        if (textWidth <= 0) return;

        // Настройка ширины окошка бегущей строки по центру экрана
        int maxDisplayWidth = 200; 
        int startX = screenWidth / 2 - maxDisplayWidth / 2;

        // Математика сдвига строки: скорость регулируется числом 2 (меньше — быстрее)
        int shift = (int) ((gameTime * 2) % textWidth);

        // Безопасный метод обрезки под Forge 47.4.22
        graphics.enableScissor(startX, y + 12, startX + maxDisplayWidth, y + 24);

        // Рисуем основной плывущий текст
        graphics.drawString(font, dataText, startX - shift, y + 12, textColor, false);
        
        // Рисуем дубликат следом, чтобы получилась бесконечная бесшовная лента новостей
        graphics.drawString(font, dataText, startX - shift + textWidth, y + 12, textColor, false);

        graphics.disableScissor();
    }
}
