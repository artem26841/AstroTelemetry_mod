package com.science.astrotelemetry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RenderGuiEvent;

public class TelemetryHud {

    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.level != null && mc.player != null && mc.font != null && mc.screen == null && event.getGuiGraphics() != null) {
            Player player = mc.player;
            if (ZoneManager.getZones() != null && !ZoneManager.getZones().isEmpty()) {
                // Перебираем обе зоны и смотрим, в какой именно находится игрок
                for (int i = 0; i < ZoneManager.getZones().size(); i++) {
                    TelemetryZone zone = ZoneManager.getZones().get(i);
                    if (zone != null && zone.isPlayerInside(player.getX(), player.getY(), player.getZ())) {
                        if (i == 0) {
                            renderGSOIZone(event.getGuiGraphics(), mc.font, zone, player);
                        } else if (i == 1) {
                            renderUPOIRZone(event.getGuiGraphics(), mc.font, zone, player);
                        }
                        break;
                    }
                }
            }
        }
    }

    // ==================== ЗОНА 1: ГСОИ (2 СТРОКИ) ====================
    private static void renderGSOIZone(GuiGraphics graphics, Font font, TelemetryZone zone, Player player) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        long gameTime = player.level().getGameTime();
        int textColor = 0xFF00FF00; // Зеленый матричный
        int y = screenHeight - 65;

        // Строка 1: Статичное название из Блокнота
        String titleText = "[ ГСОИ БОРТ-КОМПЬЮТЕР v-26.84.1 ]";
        graphics.drawString(font, titleText, screenWidth / 2 - font.width(titleText) / 2, y, textColor, false);

        // Расчет погоды мира игры
        Level level = player.level();
        String weatherStr = "ЯСНО";
        int minNoise = 0, maxNoise = 5;

        if (level.isThundering()) {
            weatherStr = "ГРОЗА";
            minNoise = 50; maxNoise = 80;
        } else if (level.isRaining()) {
            weatherStr = "ДОЖДЬ";
            minNoise = 10; maxNoise = 50;
        }

        // Рандомный скачок помех в зависимости от погоды
        int randomNoise = minNoise + (int)(Math.random() * ((maxNoise - minNoise) + 1));
        double freqNoise = (gameTime % 20 == 0) ? Math.random() * 0.01 : 0.003;

        // Строка 2: Бегущая строка со всеми данными в ряд через стрелочки ->
        String dataText = " »»» [СТАТУС] = Сеть подключена -> [GPS] = Опсерватория Ass -> " +
                          "[Частота] = " + String.format("%.4f", 1420.405 + freqNoise) + " MHz -> [Погода] = " + weatherStr + " -> " +
                          "[Помехи] = " + randomNoise + "% -> [Стабильность сети тарелок] = 100% -> " +
                          "[Активность тарелок] = 12/12 »»»             ";

        renderScrollingText(graphics, font, dataText, screenWidth, y + 12, gameTime);
    }

    // ==================== ЗОНА 2: УПОИР (3 СТРОКИ) ====================
    private static void renderUPOIRZone(GuiGraphics graphics, Font font, TelemetryZone zone, Player player) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        long gameTime = player.level().getGameTime();
        int textColor = 0xFF00FF00;
        int y = screenHeight - 75; // Поднимаем чуть выше, так как тут 3 строки

        // Строка 1: Статичное название
        String titleText = "[ ===УПОИР v-1.2.1=== ]";
        graphics.drawString(font, titleText, screenWidth / 2 - font.width(titleText) / 2, y, textColor, false);

        // Строка 2: Карусель из 4-х перелистывающихся кадров (каждые 100 тиков = 5 секунд)
        int frame = (int) ((gameTime / 100) % 4);
        String frameText = "[СКАНИРОВАНИЕ НЕБА...]";
        if (frame == 1) frameText = "[ОБЪЕКТЫ]: В НЕБЕ НЕ ОБНАРУЖЕНО";
        else if (frame == 2) frameText = "[АНОМАЛИИ]: НЕ ОБНАРУЖЕНО";
        else if (frame == 3) frameText = "[УРОВЕНЬ ОПАСНОСТИ]: БЕЛЫЙ";

        graphics.drawString(font, frameText, screenWidth / 2 - font.width(frameText) / 2, y + 12, textColor, false);

        // Строка 3: Бесконечная бегущая лента новостей
        double noise = (gameTime % 20 == 0) ? Math.random() * 0.05 : 0.01;
        String dataText = " »»» Статус = Сеть Подключена -> Частота = " + String.format("%.3f", 1665.2 + noise) + " MHz -> " +
                          "Эффективность = 97.4% -> Искажения = 2% »»»             ";

        renderScrollingText(graphics, font, dataText, screenWidth, y + 24, gameTime);
    }

    // Вспомогательный метод плавной отрисовки и бесшовной обрезки бегущей ленты
    private static void renderScrollingText(GuiGraphics graphics, Font font, String text, int screenWidth, int y, long gameTime) {
        int textWidth = font.width(text);
        if (textWidth <= 0) return;

        int maxDisplayWidth = 230;
        int startX = screenWidth / 2 - maxDisplayWidth / 2;
        int shift = (int) ((gameTime * 2) % textWidth);

        graphics.enableScissor(startX, y, startX + maxDisplayWidth, y + 12);
        graphics.drawString(font, text, startX - shift, y, 0xFF00FF00, false);
        graphics.drawString(font, text, startX - shift + textWidth, y, 0xFF00FF00, false);
        graphics.disableScissor();
    }
}
