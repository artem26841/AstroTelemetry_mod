package com.science.astrotelemetry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = AstroTelemetry.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyBindings {
    public static KeyMapping configKey;

    // Регистрируем клавишу "H" через шину мода
    @Mod.EventBusSubscriber(modid = AstroTelemetry.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            configKey = new KeyMapping(
                "key.astrotelemetry.open_menu", 
                InputConstants.Type.KEYSYM, 
                GLFW.GLFW_KEY_H, // Буква H
                "category.astrotelemetry.mod"
            );
            event.register(configKey);
        }
    }

    // Каждую секунду проверяем, нажал ли игрок кнопку в игре
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (configKey != null && configKey.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null && mc.screen == null) {
                    // Открываем наше окно настройки компьютера
                    mc.setScreen(new TelemetryConfigScreen());
                }
            }
        }
    }
}
