package com.science.astrotelemetry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static KeyMapping configKey;

    public static void registerKeys(RegisterKeyMappingsEvent event) {
        configKey = new KeyMapping(
            "key.astrotelemetry.open_menu", 
            InputConstants.Type.KEYSYM, 
            GLFW.GLFW_KEY_H, 
            "category.astrotelemetry.mod"
        );
        event.register(configKey);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (configKey != null && configKey.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null && mc.screen == null) {
                    mc.setScreen(new TelemetryConfigScreen());
                }
            }
        }
    }
}
