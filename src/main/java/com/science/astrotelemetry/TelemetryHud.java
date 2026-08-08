package com.science.astrotelemetry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AstroTelemetry.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TelemetryHud {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (event == null || event.getOverlay() == null || event.getOverlay().id() == null) return;

        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.level == null || mc.player == null || mc.font == null || event.getGuiGraphics() == null) return;

            Player player = mc.player;
            double pX = player.getX();
            double pZ = player.getZ();

            if (ZoneManager.getZones() != null) {
                for (TelemetryZone zone : ZoneManager.getZones()) {
                    if (zone != null && zone.isPlayerInside(pX, pZ)) {
                        renderComputerData(event.getGuiGraphics(), mc.font, zone, player);
                        break;
                    }
                }
            }
        }
    }

    private static void renderComputerData(GuiGraphics graphics, Font font, TelemetryZone zone, Player player) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        int x = screenWidth / 2 + 95;
        int y = screenHeight - 45; 
        int textColor = 0xFF00FF00; 

        double noise = (player.level().getGameTime() % 20 == 0) ? Math.random() * 0.05 : 0.01;
        double currentFreq = zone.getFrequency() + noise;

        // ИСПРАВЛЕНО: Использование строго совместимого метода drawString для Forge 47.4.22
        graphics.drawString(font, "== BORT-COMPUTER V1.0 ==", x, y, textColor, false);
        graphics.drawString(font, "ZONE: " + zone.getName().toUpperCase(), x, y + 10, textColor, false);
        graphics.drawString(font, "FREQ: " + String.format("%.4f", currentFreq) + " MHz", x, y + 20, textColor, false);
        graphics.drawString(font, "TYPE: " + zone.getSatelliteType(), x, y + 30, textColor, false);
        graphics.drawString(font, "SIGNAL: CONNECTED...", x, y + 40, textColor, false);
    }
}
