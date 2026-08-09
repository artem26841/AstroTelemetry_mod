package com.science.astrotelemetry;

import net.minecraft.util.Mth;

public class TelemetryZone {
    private String name;
    private double x;
    private double y; 
    private double z;
    private double radius;
    private double maxHeight; 

    public TelemetryZone(String name, double x, double y, double z, double radius, double maxHeight) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.maxHeight = maxHeight;
    }

    // ИСПРАВЛЕНО: Центр зоны тоже округляется через Mth.floor, убирая сдвиг на 1 блок!
    public boolean isPlayerInside(double playerX, double playerY, double playerZ) {
        int pY = Mth.floor(playerY);
        if (pY < Mth.floor(this.y) || pY > Mth.floor(this.maxHeight)) return false; 
        
        // Округляем координаты игрока до конкретного целого блока
        int pX = Mth.floor(playerX);
        int pZ = Mth.floor(playerZ);

        // Округляем координаты центра зоны по той же логике, чтобы убрать перекос осей
        int centerX = Mth.floor(this.x);
        int centerZ = Mth.floor(this.z);

        // Считаем абсолютно точное расстояние между блоками
        int distanceX = Math.abs(pX - centerX);
        int distanceZ = Math.abs(pZ - centerZ);
        
        return distanceX <= (int)this.radius && distanceZ <= (int)this.radius;
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setRadius(double radius) { this.radius = radius; }
    public void setMaxHeight(double maxHeight) { this.maxHeight = maxHeight; }

    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getRadius() { return radius; }
    public double getMaxHeight() { return maxHeight; }
}
