package com.science.astrotelemetry;

public class TelemetryZone {
    private String name;
    private double x;
    private double z;
    private double radius;
    private double minY; // Минимальная высота для работы компьютера

    public TelemetryZone(String name, double x, double z, double radius, double minY) {
        this.name = name;
        this.x = x;
        this.z = z;
        this.radius = radius;
        this.minY = minY;
    }

    // ИСПРАВЛЕНО: Проверяем координаты X, Z И ВЫСОТУ Y
    public boolean isPlayerInside(double playerX, double playerY, double playerZ) {
        if (playerY < this.minY) return false; // Если игрок спустился ниже лаборатории — сигнал пропадает
        double dx = playerX - this.x;
        double dz = playerZ - this.z;
        return (dx * dx + dz * dz) <= (radius * radius);
    }

    // ИСПРАВЛЕНО: Методы для изменения координат (перетаскивания) через меню
    public void setX(double x) { this.x = x; }
    public void setZ(double z) { this.z = z; }
    public void setMinY(double minY) { this.minY = minY; }

    public String getName() { return name; }
    public double getX() { return x; }
    public double getZ() { return z; }
    public double getRadius() { return radius; }
    public double getMinY() { return minY; }
}
