package com.science.astrotelemetry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TelemetryConfigScreen extends Screen {
    private EditBox xField;
    private EditBox yField;
    private EditBox zField;
    private EditBox maxHeightField;
    private EditBox radiusField;

    public TelemetryConfigScreen() {
        super(Component.literal("Управление Исследовательской Станцией"));
    }

    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int pX = this.width / 2;
        int pY = this.height / 2;

        TelemetryZone mainZone = ZoneManager.getZones().get(0);

        this.xField = new EditBox(this.font, pX - 105, pY - 50, 65, 20, Component.literal("X"));
        this.xField.setValue(String.valueOf((int)mainZone.getX()));
        this.addWidget(this.xField);

        this.yField = new EditBox(this.font, pX - 35, pY - 50, 65, 20, Component.literal("Y"));
        this.yField.setValue(String.valueOf((int)mainZone.getY()));
        this.addWidget(this.yField);

        this.zField = new EditBox(this.font, pX + 35, pY - 50, 65, 20, Component.literal("Z"));
        this.zField.setValue(String.valueOf((int)mainZone.getZ()));
        this.addWidget(this.zField);

        this.maxHeightField = new EditBox(this.font, pX - 105, pY - 10, 100, 20, Component.literal("Макс. Высота"));
        this.maxHeightField.setValue(String.valueOf((int)mainZone.getMaxHeight()));
        this.addWidget(this.maxHeightField);

        this.radiusField = new EditBox(this.font, pX + 5, pY - 10, 100, 20, Component.literal("Радиус области"));
        this.radiusField.setValue(String.valueOf((int)mainZone.getRadius()));
        this.addWidget(this.radiusField);

        Button gpsButton = Button.builder(Component.literal("Привязать к моему GPS"), (button) -> {
            this.xField.setValue(String.valueOf((int)this.minecraft.player.getX()));
            this.yField.setValue(String.valueOf((int)this.minecraft.player.getY()));
            this.zField.setValue(String.valueOf((int)this.minecraft.player.getZ()));
        }).bounds(pX - 105, pY + 25, 210, 20).build();

        Button saveButton = Button.builder(Component.literal("Применить координаты"), (button) -> {
            try {
                double x = Double.parseDouble(this.xField.getValue());
                double y = Double.parseDouble(this.yField.getValue());
                double z = Double.parseDouble(this.zField.getValue());
                double maxH = Double.parseDouble(this.maxHeightField.getValue());
                double rad = Double.parseDouble(this.radiusField.getValue());

                mainZone.setX(x);
                mainZone.setY(y);
                mainZone.setZ(z);
                mainZone.setMaxHeight(maxH);
                mainZone.setRadius(rad);
                
                ZoneManager.saveZones();
                this.onClose(); 
            } catch (NumberFormatException e) {
                this.xField.setValue("ОШИБКА!");
            }
        }).bounds(pX - 105, pY + 50, 210, 20).build();

        this.addRenderableWidget(gpsButton);
        this.addRenderableWidget(saveButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft != null && this.minecraft.level != null) {
            graphics.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xD0101010);
        }
        
        int pX = this.width / 2;
        int pY = this.height / 2;

        if (this.xField != null) this.xField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.yField != null) this.yField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.zField != null) this.zField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.maxHeightField != null) this.maxHeightField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.radiusField != null) this.radiusField.renderWidget(graphics, mouseX, mouseY, partialTick);

        String titleText = "КООРДИНАТОР ОУС-1 \"ОБСЕРВАТОРИЯ\"";
        graphics.drawString(this.font, titleText, pX - (this.font.width(titleText) / 2), pY - 75, 0xFFFFFF, false);
        
        String label1 = "Ввод координат центра (X / Y / Z):";
        graphics.drawString(this.font, label1, pX - 105, pY - 62, 0xAAAAAA, false);
        
        String label2 = "Макс. высота:";
        graphics.drawString(this.font, label2, pX - 105, pY - 22, 0xAAAAAA, false);

        String label3 = "Радиус обл.:";
        graphics.drawString(this.font, label3, pX + 5, pY - 22, 0xAAAAAA, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}
