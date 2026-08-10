package com.science.astrotelemetry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;

public class TelemetryHud {
    private static int cachedNoise = 0;
    private static long lastNoiseTick = -1;
    private static int lastFrameIndex = -1;

    // Таймер для бесконечного цикличного повторения звука
    private static long lastAmbientSoundTick = 0;

    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.level != null && mc.player != null && mc.font != null && mc.screen == null && event.getGuiGraphics() != null) {
            Player player = mc.player;

            if (ZoneManager.getZones() != null && !ZoneManager.getZones().isEmpty()) {
                
                // РАБОТАЕМ СВЕРХУ С ЗОНОЙ 0 (ГСОИ) ДЛЯ ЦИКЛИЧНОГО 3D ЗВУКА
                TelemetryZone gsoiZone = ZoneManager.getZones().get(0);
                if (gsoiZone != null) {
                    double dx = player.getX() - gsoiZone.getX();
                    double dz = player.getZ() - gsoiZone.getZ();
                    double distanceSq = dx * dx + dz * dz;

                    double allowedRange = gsoiZone.getSoundRange();
                    if (distanceSq <= allowedRange * allowedRange) {
                        long gameTime = mc.level.getGameTime();
                        
                        // ИСПРАВЛЕНО: Бесконечный цикл. Когда звук заканчивается (через 4 сек / 80 тиков), он играет заново
                        if (gameTime - lastAmbientSoundTick >= 80 || gameTime < lastAmbientSoundTick) {
                            
                            // НОВОЕ: Проверка на наличие стен/пола между игроком и центром зоны
                            float volume = 0.7F; // Базовая громкость на открытом пространстве
                            
                            Vec3 playerPos = player.getEyePosition(1.0F);
                            Vec3 zonePos = new Vec3(gsoiZone.getX() + 0.5, gsoiZone.getY() + 0.5, gsoiZone.getZ() + 0.5);
                            
                            // Пускаем невидимый луч от глаз игрока к центру зоны
                            BlockHitResult rayTrace = mc.level.clip(new ClipContext(
                                playerPos, zonePos, 
                                ClipContext.Block.COLLIDER, 
                                ClipContext.Fluid.NONE, 
                                player
                            ));

                            // Если луч упёрся в блок (стену или пол), глушим звук до 0.15F (в 4.5 раза тише!)
                            if (rayTrace.getType() == HitResult.Type.BLOCK) {
                                volume = 0.15F; 
                            }

                            // Воспроизводим строго ИЗ КООРДИНАТ ЦЕНТРА ЗОНЫ
                            mc.level.playSound(player, gsoiZone.getX(), gsoiZone.getY(), gsoiZone.getZ(), 
                                AstroSounds.ZONE_ENTER.get(), SoundSource.BLOCKS, volume, 1.0F);
                            
                            lastAmbientSoundTick = gameTime;
                        }
                    }
                }

                // Стандартная логика вывода бегущих строк на экран
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

    private static void renderGSOIZone(GuiGraphics graphics, Font font, TelemetryZone zone, Player player) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        long gameTime = player.level().getGameTime();
        int textColor = 0xFF00FF00; 
        int y = screenHeight - 65;

        String titleText = "[ ГСОИ БОРТ-КОМПЬЮТЕР v-26.84.1 ]";
        graphics.drawString(font, titleText, screenWidth / 2 - font.width(titleText) / 2, y, textColor, false);

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

        if (gameTime % 20 == 0 || lastNoiseTick == -1 || gameTime < lastNoiseTick) {
            cachedNoise = minNoise + (int)(Math.random() * ((maxNoise - minNoise) + 1));
            lastNoiseTick = gameTime;
        }

        double freqNoise = (gameTime % 20 == 0) ? Math.random() * 0.001 : 0.003;

        String dataText = " »»» [СТАТУС] = Сеть подключена -> [GPS] = Опсерватория Ass -> " +
                          "[Частота] = " + String.format("%.4f", 1420.405 + freqNoise) + " MHz -> [Погода] = " + weatherStr + " -> " +
                          "[Помехи] = " + cachedNoise + "% -> [Стабильность сети тарелок] = 100% -> " +
                          "[Активность тарелок] = 12/12 »»»             ";

        renderScrollingText(graphics, font, dataText, screenWidth, y + 12, gameTime);
    }

    private static void renderUPOIRZone(GuiGraphics graphics, Font font, TelemetryZone zone, Player player) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        long gameTime = player.level().getGameTime();
        int textColor = 0xFF00FF00;
        int y = screenHeight - 75; 

        String titleText = "[ ===УПОИР v-1.2.1=== ]";
        graphics.drawString(font, titleText, screenWidth / 2 - font.width(titleText) / 2, y, textColor, false);

        int frame = (int) ((gameTime / 100) % 4);
        if (frame != lastFrameIndex) {
            lastFrameIndex = frame;
        }

        String frameText = "[СКАНИРОВАНИЕ НЕБА...]";
        if (frame == 1) frameText = "[ОБЪЕКТЫ]: В НЕБЕ НЕ ОБНАРУЖЕНО";
        else if (frame == 2) frameText = "[АНОМАЛИИ]: НЕ ОБНАРУЖЕНО";
        else if (frame == 3) frameText = "[УРОВЕНЬ ОПАСНОСТИ]: БЕЛЫЙ";

        graphics.drawString(font, frameText, screenWidth / 2 - font.width(frameText) / 2, y + 12, textColor, false);

        double noise = (gameTime % 20 == 0) ? Math.random() * 0.05 : 0.01;
        String dataText = " »»» Статус = Сеть Подключена -> Частота = " + String.format("%.3f", 1665.2 + noise) + " MHz -> " +
                          "Эффективность = 97.4% -> Искажения = 2% »»»             ";

        renderScrollingText(graphics, font, dataText, screenWidth, y + 24, gameTime);
    }

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
