package com.science.astrotelemetry;

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

    public boolean isPlayerInside(double playerX, double playerY, double playerZ) {
        if (playerY < this.y || playerY > this.maxHeight) return false; 
        
        double dx = playerX - this.x;
        double dz = playerZ - this.z;
        return (dx * dx + dz * dz) <= (radius * radius);
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
