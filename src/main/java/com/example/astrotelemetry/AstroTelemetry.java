package com.science.astrotelemetry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AstroTelemetry.MODID)
public class AstroTelemetry {
    public static final String MODID = "astrotelemetry";
    public static final Logger LOGGER = LogManager.getLogger();

    public AstroTelemetry() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Регистрируем клиентскую инициализацию (Шаг 3)
        modEventBus.addListener(this::doClientSetup);

        // Регистрируем главный автобус событий Forge
        MinecraftForge.EVENT_BUS.register(this);
        
        LOGGER.info("AstroTelemetry успешно инициализирован на стороне клиента!");
    }

    private void doClientSetup(final FMLClientSetupEvent event) {
        // Запускаем наш менеджер зон, чтобы он создал или прочитал JSON файл
        event.enqueueWork(ZoneManager::init);
        LOGGER.info("AstroTelemetry: База данных научных зон загружена.");
    }
}
