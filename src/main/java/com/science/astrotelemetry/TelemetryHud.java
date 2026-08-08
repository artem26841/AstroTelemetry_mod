package com.science.astrotelemetry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiEvent;

public class TelemetryHud {

    // ИСПРАВЛЕНО: Используем глобальное событие отрисовки интерфейса RenderGuiEvent.Post
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        
        // Рисуем только если игрок в игре, мир загружен и меню паузы закрыто
        if (mc != null && mc.level != null && mc.player != null && mc.font != null && mc.screen == null && event.getGuiGraphics() != null) {
            Player player = mc.player;
            
            if (ZoneManager.getZones() != null && !ZoneManager.getZones().isEmpty()) {
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

        // Позиция по вертикали строго над инвентарем (65 пикселей от низа экрана)
        int y = screenHeight - 65; 

        // СТРОКА 1: Статичное название главного компьютера structures
        String titleText = "[ ЦВМ БОРТ-КОМПЬЮТЕР V1.2 ]";
        int titleX = screenWidth / 2 - font.width(titleText) / 2;
        graphics.drawString(font, titleText, titleX, y, textColor, false);

        // СТРОКА 2: Сбор всех данных в одну новостную ленту в ряд через стрелочки ->
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
        int maxDisplayWidth = 220; 
        int startX = screenWidth / 2 - maxDisplayWidth / 2;

        // Математика сдвига строки (скорость движения)
        int shift = (int) ((gameTime * 2) % textWidth);

        // Включаем безопасную обрезку краев под Forge 47.4.22
        graphics.enableScissor(startX, y + 12, startX + maxDisplayWidth, y + 24);

        // Рисуем основной плывущий текст
        graphics.drawString(font, dataText, startX - shift, y + 12, textColor, false);
        
        // Рисуем дубликат следом, чтобы получилась бесконечная бесшовная лента новостей
        graphics.drawString(font, dataText, startX - shift + textWidth, y + 12, textColor, false);

        graphics.disableScissor();
    }
}
