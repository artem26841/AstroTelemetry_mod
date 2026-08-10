package com.science.astrotelemetry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AstroSounds {
    // Создаем реестр звуков для нашего мода
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AstroTelemetry.MODID);

    // Регистрируем кодовое имя "zone_enter", которое мы написали в sounds.json
    public static final RegistryObject<SoundEvent> ZONE_ENTER = SOUND_EVENTS.register("zone_enter",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(AstroTelemetry.MODID, "zone_enter")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
