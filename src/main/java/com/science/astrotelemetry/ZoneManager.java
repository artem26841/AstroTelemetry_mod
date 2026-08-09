package com.science.astrotelemetry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ZoneManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<TelemetryZone> zones = new ArrayList<>();
    private static File configFile;

    public static void init() {
        File configDir = new File(Minecraft.getInstance().gameDirectory, "config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        configFile = new File(configDir, "astro_zones.json");
        loadZones();
    }

    public static List<TelemetryZone> getZones() {
        return zones;
    }

    public static void addZone(TelemetryZone zone) {
        zones.add(zone);
        saveZones();
    }

    public static void saveZones() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(zones, writer);
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Не удалось сохранить зоны AstroTelemetry!", e);
        }
    }

    public static void loadZones() {
        zones.clear();
        if (!configFile.exists()) {
            addZone(new TelemetryZone("Главный Институт", 0.0, 43.0, 0.0, 50.0, 256.0));
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            Type listType = new TypeToken<ArrayList<TelemetryZone>>() {}.getType();
            List<TelemetryZone> loaded = GSON.fromJson(reader, listType);
            if (loaded != null) {
                zones.addAll(loaded);
            }
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Не удалось загрузить зоны AstroTelemetry!", e);
        }
    }
}
