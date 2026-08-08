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

    // Инициализация файла конфигурации
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

    // Добавление новой зоны и автоматическое сохранение
    public static void addZone(TelemetryZone zone) {
        zones.add(zone);
        saveZones();
    }

    // Сохранение списка зон в JSON
    public static void saveZones() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(zones, writer);
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Не удалось сохранить зоны AstroTelemetry!", e);
        }
    }

    // Загрузка списка зон из JSON
    public static void loadZones() {
        zones.clear();
        if (!configFile.exists()) {
            // ИСПРАВЛЕНО: имя, X, Z, радиус (6), минимальная высота Y (по умолчанию 60)
            addZone(new TelemetryZone("Центральная Лаборатория", 0, 0, 6, 60.0));
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
