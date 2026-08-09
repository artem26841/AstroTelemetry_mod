package com.science.astrotelemetry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AstroTelemetry.MODID)
public class AstroTelemetry {
    public static final String MODID = "astrotelemetry";
    public static final Logger LOGGER = LogManager.getLogger();

    public AstroTelemetry() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
            
            modEventBus.addListener(KeyBindings::registerKeys);
            modEventBus.addListener(this::doClientSetup);

            MinecraftForge.EVENT_BUS.addListener(TelemetryHud::onRenderGui);
            MinecraftForge.EVENT_BUS.addListener(KeyBindings::onClientTick);
        }
        LOGGER.info("!!! AstroTelemetry УСПЕШНО ЗАПУЩЕН !!!");
    }

    private void doClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ZoneManager::init);
        LOGGER.info("!!! AstroTelemetry: БАЗА ДАННЫХ ИНИЦИАЛИЗИРОВАНА !!!");
    }
}
