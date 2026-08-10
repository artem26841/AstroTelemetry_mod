package com.science.astrotelemetry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AstroTelemetry.MODID)
public class AstroTelemetry {
    public static final String MODID = "astrotelemetry";
    public static final Logger LOGGER = LogManager.getLogger();

    public AstroTelemetry() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // ВКЛЮЧАЕМ НАШИ КАСТОМНЫЕ ЗВУКИ В СИСТЕМУ СБОРКИ FORGE
        AstroSounds.register(modEventBus);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(KeyBindings::registerKeys);
            modEventBus.addListener(this::doClientSetup);

            MinecraftForge.EVENT_BUS.addListener(TelemetryHud::onRenderGui);
            MinecraftForge.EVENT_BUS.addListener(KeyBindings::onClientTick);
            MinecraftForge.EVENT_BUS.register(this);
        }
        LOGGER.info("!!! AstroTelemetry УСПЕШНО ЗАПУЩЕН !!!");
    }

    private void doClientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("!!! AstroTelemetry: Клиент настроен !!!");
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() && event.getEntity() == net.minecraft.client.Minecraft.getInstance().player) {
            ZoneManager.initForCurrentServerOrWorld();
        }
    }
}
