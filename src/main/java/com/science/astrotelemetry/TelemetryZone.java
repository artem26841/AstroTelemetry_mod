package com.science.astrotelemetry;

public class TelemetryZone {
    private String name;
    private double x;
    private double z;
    private double radius;
    private double frequency; // Частота в МГц
    private String satelliteType; // Например, "Радиотелескоп", "Спутник связи"

    public TelemetryZone(String name, double x, double z, double radius, double frequency, String satelliteType) {
        this.name = name;
        this.x = x;
        this.z = z;
        this.radius = radius;
        this.frequency = frequency;
        this.satelliteType = satelliteType;
    }

    // Проверка: находится ли игрок внутри зоны по формуле расстояния
    public boolean isPlayerInside(double playerX, double playerZ) {
        double dx = playerX - this.x;
        double dz = playerZ - this.z;
        return (dx * dx + dz * dz) <= (radius * radius);
    }

    // Геттеры для отображения в HUD и GUI
    public String getName() { return name; }
    public double getX() { return x; }
    public double getZ() { return z; }
    public double getRadius() { return radius; }
    public double getFrequency() { return frequency; }
    public String getSatelliteType() { return satelliteType; }
}
