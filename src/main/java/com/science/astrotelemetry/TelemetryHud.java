package com.science.astrotelemetry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderGuiEvent;

public class TelemetryHud {
    private static int cachedNoise = 0;
    private static long lastNoiseTick = -1;
    private static int lastFrameIndex = -1;

    // Переменные контроля звука
    private static SimpleSoundInstance currentAmbientSound = null;
    private static long lastAmbientSoundTick = 0;
    private static boolean wasInsideGsoi = false;

    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.level != null && mc.player != null && mc.font != null && mc.screen == null && event.getGuiGraphics() != null) {
            Player player = mc.player;
            boolean isInsideGsoiNow = false;

            if (ZoneManager.getZones() != null && !ZoneManager.getZones().isEmpty()) {
                
                TelemetryZone gsoiZone = ZoneManager.getZones().get(0);
                if (gsoiZone != null && gsoiZone.isPlayerInside(player.getX(), player.getY(), player.getZ())) {
                    isInsideGsoiNow = true;
                    long gameTime = mc.level.getGameTime();

                    // Бесконечный цикл на 16 секунд (320 тиков)
                    if (!wasInsideGsoi || (gameTime - lastAmbientSoundTick >= 320) || gameTime < lastAmbientSoundTick) {
                        
                        if (currentAmbientSound != null) {
                            mc.getSoundManager().stop(currentAmbientSound);
                        }

                        ZoneManager.PlayerPreset gsoiPreset = ZoneManager.getPreset(0);
                        float baseVolume = gsoiPreset != null ? (float) gsoiPreset.soundVolume : 1.0F;
                        float finalVolume = 0.7F * baseVolume;

                        Vec3 playerPos = player.getEyePosition(1.0F);
                        Vec3 zonePos = new Vec3(gsoiZone.getX() + 0.5, gsoiZone.getY() + 0.5, gsoiZone.getZ() + 0.5);
                        BlockHitResult rayTrace = mc.level.clip(new ClipContext(playerPos, zonePos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                        if (rayTrace.getType() == HitResult.Type.BLOCK) {
                            finalVolume *= 0.2F;
                        }

                        // ИСПРАВЛЕНО: Безопасный конструктор 3D звука строго для маппингов Forge 47.4.22
                        currentAmbientSound = new SimpleSoundInstance(
                            AstroSounds.ZONE_ENTER.get().getLocation(),
                            SoundSource.BLOCKS,
                            finalVolume,
                            1.0F,
                            false,
                            0,
                            SimpleSoundInstance.SoundInstancePosition.LINEAR,
                            gsoiZone.getX() + 0.5,
                            gsoiZone.getY() + 0.5,
                            gsoiZone.getZ() + 0.5
                        );

                        mc.getSoundManager().play(currentAmbientSound);
                        lastAmbientSoundTick = gameTime;
                    }
                }

                if (!isInsideGsoiNow && wasInsideGsoi && currentAmbientSound != null) {
                    mc.getSoundManager().stop(currentAmbientSound);
                    currentAmbientSound = null;
                }
                wasInsideGsoi = isInsideGsoiNow;

                // Вывод текста
                for (int i = 0; i < ZoneManager.getZones().size(); i++) {
                    TelemetryZone zone = ZoneManager.getZones().get(i);
                    if (zone != null && zone.isPlayerInside(player.getX(), player.getY(), player.getZ())) {
                        ZoneManager.PlayerPreset currentPreset = ZoneManager.getPreset(i);
                        int personalColor = currentPreset != null ? currentPreset.getHexColor() : 0xFF00FF00;

                        if (i == 0) {
                            renderGSOIZone(event.getGuiGraphics(), mc.font, zone, player, personalColor);
                        } else if (i == 1) {
                            renderUPOIRZone(event.getGuiGraphics(), mc.font, zone, player, personalColor);
                        }
                        break;
                    }
                }
            }
        }
    }

    private static void renderGSOIZone(GuiGraphics graphics, Font font, TelemetryZone zone, Player player, int textColor) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        long gameTime = player.level().getGameTime();
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

        renderScrollingText(graphics, font, dataText, screenWidth, y + 12, gameTime, textColor);
    }

    private static void renderUPOIRZone(GuiGraphics graphics, Font font, TelemetryZone zone, Player player, int textColor) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        long gameTime = player.level().getGameTime();
        int y = screenHeight - 75; 

        String titleText = "[ ===УПОИР v-1.2.1=== ]";
        graphics.drawString(font, titleText, screenWidth / 2 - font.width(titleText) / 2, y, textColor, false);

        int frame = (int) ((gameTime / 100) % 4);
        String frameText = "[СКАНИРОВАНИЕ НЕБА...]";
        if (frame == 1) frameText = "[ОБЪЕКТЫ]: В НЕБЕ НЕ ОБНАРУЖЕНО";
        else if (frame == 2) frameText = "[АНОМАЛИИ]: НЕ ОБНАРУЖЕНО";
        else if (frame == 3) frameText = "[УРОВЕНЬ ОПАСНОСТИ]: БЕЛЫЙ";

        graphics.drawString(font, frameText, screenWidth / 2 - font.width(frameText) / 2, y + 12, textColor, false);

        double noise = (gameTime % 20 == 0) ? Math.random() * 0.05 : 0.01;
        String dataText = " »»» Статус = Сеть Подключена -> Частота = " + String.format("%.3f", 1665.2 + noise) + " MHz -> " +
                          "Эффективность = 97.4% -> Искажения = 2% »»»             ";

        renderScrollingText(graphics, font, dataText, screenWidth, y + 24, gameTime, textColor);
    }

    private static void renderScrollingText(GuiGraphics graphics, Font font, String text, int screenWidth, int y, long gameTime, int color) {
        int textWidth = font.width(text);
        if (textWidth <= 0) return;

        int maxDisplayWidth = 230;
        int startX = screenWidth / 2 - maxDisplayWidth / 2;
        int shift = (int) ((gameTime * 2) % textWidth);

        graphics.enableScissor(startX, y, startX + maxDisplayWidth, y + 12);
        graphics.drawString(font, text, startX - shift, y, color, false);
        graphics.drawString(font, text, startX - shift + textWidth, y, color, false);
        graphics.disableScissor();
    }
}
