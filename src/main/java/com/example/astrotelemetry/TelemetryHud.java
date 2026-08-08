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
        // Отрисовываем только поверх стандартного HUD элементов игрока (хотбара)
        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;

            // Если игрок в игре и мир загружен
            if (player != null) {
                double pX = player.getX();
                double pZ = player.getZ();

                // Проверяем, находится ли игрок в какой-либо научной зоне
                for (TelemetryZone zone : ZoneManager.getZones()) {
                    if (zone.isPlayerInside(pX, pZ)) {
                        renderComputerData(event.getGuiGraphics(), mc.font, zone, player);
                        break; // Выводим данные только одной зоны за раз
                    }
                }
            }
        }
    }

    private static void renderComputerData(GuiGraphics graphics, Font font, TelemetryZone zone, Player player) {
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        // Позиция: Справа от панели инвентаря (Hotbar)
        // Центр экрана по горизонтали + 95 пикселей (инвентарь заканчивается примерно на +91)
        int x = screenWidth / 2 + 95;
        // Позиция по вертикали: чуть выше самого низа экрана
        int y = screenHeight - 45; 

        // Зеленый "матричный" цвет для текста компьютера (в формате ARGB)
        int textColor = 0xFF00FF00; 

        // Имитируем шумы спутника, зависящие от времени мира (чтобы цифры немного "прыгали")
        double noise = (player.level().getGameTime() % 20 == 0) ? Math.random() * 0.05 : 0.01;
        double currentFreq = zone.getFrequency() + noise;

        // Рисуем строки данных бортового компьютера
        graphics.drawString(font, "== BORT-COMPUTER V1.0 ==", x, y, textColor, false);
        graphics.drawString(font, "ZONE: " + zone.getName().toUpperCase(), x, y + 10, textColor, false);
        graphics.drawString(font, "FREQ: " + String.format("%.4f", currentFreq) + " MHz", x, y + 20, textColor, false);
        graphics.drawString(font, "TYPE: " + zone.getSatelliteType(), x, y + 30, textColor, false);
        graphics.drawString(font, "SIGNAL: CONNECTED...", x, y + 40, textColor, false);
    }
}
