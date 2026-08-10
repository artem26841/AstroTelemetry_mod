package com.science.astrotelemetry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

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

    public static void initForCurrentServerOrWorld() {
        Minecraft mc = Minecraft.getInstance();
        File configDir = new File(mc.gameDirectory, "config/astrotelemetry");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        String fileName;
        if (mc.getSingleplayerServer() != null) {
            fileName = "singleplayer_" + mc.getSingleplayerServer().getWorldData().getLevelName() + ".json";
        } else {
            ServerData serverData = mc.getCurrentServer();
            if (serverData != null) {
                fileName = "server_" + serverData.ip.replace(":", "_").replace(".", "_") + ".json";
            } else {
                fileName = "local_world.json";
            }
        }

        configFile = new File(configDir, fileName);
        loadZones();
    }

    public static List<TelemetryZone> getZones() {
        return zones;
    }

    public static void saveZones() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(zones, writer);
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Ошибка сохранения зон!", e);
        }
    }

    public static void loadZones() {
        zones.clear();
        if (!configFile.exists()) {
            // Создаем ДВЕ зоны по умолчанию (ГСОИ на 0 43 0 и УПОИР на 50 43 50)
            zones.add(new TelemetryZone("Гл Комп", 0.0, 43.0, 0.0, 6.0, 256.0));
            zones.add(new TelemetryZone("Упоир v-1.2.1", 50.0, 43.0, 50.0, 6.0, 256.0));
            saveZones();
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            Type listType = new TypeToken<ArrayList<TelemetryZone>>() {}.getType();
            List<TelemetryZone> loaded = GSON.fromJson(reader, listType);
            if (loaded != null) {
                zones.addAll(loaded);
            }
            // Защита: если файл пуст или поврежден
            if (zones.size() < 2) {
                zones.clear();
                zones.add(new TelemetryZone("Гл Комп", 0.0, 43.0, 0.0, 6.0, 256.0));
                zones.add(new TelemetryZone("Упоир v-1.2.1", 50.0, 43.0, 50.0, 6.0, 256.0));
                saveZones();
            }
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Ошибка загрузки зон!", e);
        }
    }
}
