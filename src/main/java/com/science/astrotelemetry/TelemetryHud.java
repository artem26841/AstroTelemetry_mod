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
        int textColor = 0xFF00FF00; // Яркий зеленый матричный цвет

        // 1. Позиция по вертикали: над инвентарем
        int y = screenHeight - 55; 

        // 2. ОТРИСОВКА СТАТИЧНОЙ СТРОКИ 1 (Название терминала)
        String titleText = "[ ЦВМ БОРТ-КОМПЬЮТЕР V1.2 ]";
        int titleX = screenWidth / 2 - font.width(titleText) / 2;
        graphics.drawString(font, titleText, titleX, y, textColor, false);

        // 3. СБОР ДАННЫХ ДЛЯ СТРОКИ 2 (Бегущая строка)
        double noise = (gameTime % 20 == 0) ? Math.random() * 0.02 : 0.005;
        int rainPercent = (int)(player.level().getRainLevel(1.0F) * 100);
        
        String dataText = " »»» [СТАТУС]: СЕТЬ ПОДКЛЮЧЕНА -> [ОБЪЕКТ]: " + zone.getName().toUpperCase() + 
                          " -> [ВЫСОТА]: " + String.format("%.1f", player.getY()) + "м" +
                          " -> [ЧАСТОТА]: " + String.format("%.4f", 1420.4 + noise) + " MHz" +
                          " -> [ПОГОДА]: ИСКАЖЕНИЕ " + rainPercent + "%" +
                          " -> [ТЕЛЕМЕТРИЯ]: ДАННЫЕ СТАБИЛЬНЫ »»»     ";

        // 4. МАТЕМАТИКА БЕГУЩЕЙ СТРОКИ
        int textWidth = font.width(dataText);
        
        // Ограничиваем зону видимости строки, чтобы она бегала в рамка центра экрана
        int maxDisplayWidth = 260; 
        int startX = screenWidth / 2 - maxDisplayWidth / 2;

        // Рассчитываем смещение на основе тиков игры (скорость движения)
        // Меняйте число 2, чтобы ускорить или замедлить строку (чем больше, тем медленнее)
        int speed = 2; 
        int shift = (int) ((gameTime * speed) % textWidth);

        // Включаем scissor (ножницы графического движка), чтобы текст не вылезал за границы центра экрана
        graphics.enableScissor(startX, y + 12, startX + maxDisplayWidth, y + 25);

        // Рисуем основной текст со смещением влево
        graphics.drawString(font, dataText, startX - shift, y + 12, textColor, false);
        
        // Рисуем дубликат следом, чтобы строка шла бесконечным бесшовным потоком
        graphics.drawString(font, dataText, startX - shift + textWidth, y + 12, textColor, false);

        // Выключаем ограничения графики
        graphics.disableScissor();
    }
}
