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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoneManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<TelemetryZone> zones = new ArrayList<>();
    private static File configFile;
    
    // Локальное хранилище личных настроек игрока (Индекс зоны -> Параметры)
    private static final Map<Integer, PlayerPreset> playerPresets = new HashMap<>();
    private static File presetsFile;

    public static void initForCurrentServerOrWorld() {
        Minecraft mc = Minecraft.getInstance();
        File configDir = new File(mc.gameDirectory, "config/astrotelemetry");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        String prefix = mc.getSingleplayerServer() != null ? "singleplayer_" + mc.getSingleplayerServer().getWorldData().getLevelName() : 
                        (mc.getCurrentServer() != null ? "server_" + mc.getCurrentServer().ip.replace(":", "_").replace(".", "_") : "local_world");

        configFile = new File(configDir, prefix + ".json");
        presetsFile = new File(configDir, prefix + "_player_presets.json");
        
        loadZones();
        loadPlayerPresets();
    }

    public static List<TelemetryZone> getZones() { return zones; }

    public static void saveZones() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(zones, writer);
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Ошибка сохранения координат зон!", e);
        }
    }

    public static void loadZones() {
        zones.clear();
        if (!configFile.exists()) {
            zones.add(new TelemetryZone("Гл Комп", 0.0, 43.0, 0.0, 6.0, 256.0));
            zones.add(new TelemetryZone("Упоир v-1.2.1", 50.0, 43.0, 50.0, 6.0, 256.0));
            saveZones();
            return;
        }
        try (FileReader reader = new FileReader(configFile)) {
            Type listType = new TypeToken<ArrayList<TelemetryZone>>() {}.getType();
            List<TelemetryZone> loaded = GSON.fromJson(reader, listType);
            if (loaded != null) zones.addAll(loaded);
            if (zones.size() < 2) {
                zones.clear();
                zones.add(new TelemetryZone("Гл Комп", 0.0, 43.0, 0.0, 6.0, 256.0));
                zones.add(new TelemetryZone("Упоир v-1.2.1", 50.0, 43.0, 50.0, 6.0, 256.0));
                saveZones();
            }
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Ошибка загрузки координат зон!", e);
        }
    }

    // ЛОКАЛЬНЫЕ НАСТРОЙКИ ИГРОКА (ЦВЕТ И ГРОМКОСТЬ)
    public static PlayerPreset getPreset(int index) {
        return playerPresets.computeIfAbsent(index, k -> new PlayerPreset(index == 0 ? 16.0 : 0.0, 1.0, "#00FF00"));
    }

    public static void savePlayerPresets() {
        if (presetsFile == null) return;
        try (FileWriter writer = new FileWriter(presetsFile)) {
            GSON.toJson(playerPresets, writer);
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Ошибка сохранения личных настроек!", e);
        }
    }

    public static void loadPlayerPresets() {
        playerPresets.clear();
        if (!presetsFile.exists()) return;
        try (FileReader reader = new FileReader(presetsFile)) {
            Type type = new TypeToken<HashMap<Integer, PlayerPreset>>() {}.getType();
            Map<Integer, PlayerPreset> loaded = GSON.fromJson(reader, type);
            if (loaded != null) playerPresets.putAll(loaded);
        } catch (Exception e) {
            AstroTelemetry.LOGGER.error("Ошибка загрузки личных настроек!", e);
        }
    }

    // Вспомогательный мини-класс для хранения личных данных игрока
    public static class PlayerPreset {
        public double soundRange;
        public double soundVolume;
        public String textColor;

        public PlayerPreset(double range, double vol, String color) {
            this.soundRange = range;
            this.soundVolume = vol;
            this.textColor = color;
        }
        
        public int getHexColor() {
            try {
                if (this.textColor != null && this.textColor.startsWith("#")) {
                    return Integer.parseInt(this.textColor.substring(1), 16);
                }
                return Integer.decode(this.textColor);
            } catch (Exception e) { return 0xFF00FF00; }
        }
    }
}
