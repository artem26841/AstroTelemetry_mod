package com.science.astrotelemetry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TelemetryConfigScreen extends Screen {
    private final Screen parentScreen; // Переменная для запоминания предыдущего экрана хаба
    private EditBox xField;
    private EditBox yField;
    private EditBox zField;
    private EditBox maxHeightField;
    private EditBox radiusField;

      private final int zoneIndex;
    public TelemetryConfigScreen(Screen parentScreen, int zoneIndex) {
        super(Component.literal("Управление Исследовательской Станцией"));
        this.parentScreen = parentScreen;
        this.zoneIndex = zoneIndex;
    }
    // В методах init() и render() замените .get(0) на .get(this.zoneIndex)
    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        boolean isOwner = this.minecraft.player.getGameProfile().getName().equals("artem26841");

        int pX = this.width / 2;
        int pY = this.height / 2;

        TelemetryZone mainZone = ZoneManager.getZones().get(0);

        this.xField = new EditBox(this.font, pX - 105, pY - 50, 65, 20, Component.literal("X"));
        this.xField.setValue(String.valueOf((int)mainZone.getX()));
        this.xField.setEditable(isOwner);
        this.addWidget(this.xField);

        this.yField = new EditBox(this.font, pX - 35, pY - 50, 65, 20, Component.literal("Y"));
        this.yField.setValue(String.valueOf((int)mainZone.getY()));
        this.yField.setEditable(isOwner);
        this.addWidget(this.yField);

        this.zField = new EditBox(this.font, pX + 35, pY - 50, 65, 20, Component.literal("Z"));
        this.zField.setValue(String.valueOf((int)mainZone.getZ()));
        this.zField.setEditable(isOwner);
        this.addWidget(this.zField);

        this.maxHeightField = new EditBox(this.font, pX - 105, pY - 10, 100, 20, Component.literal("Макс. Высота"));
        this.maxHeightField.setValue(String.valueOf((int)mainZone.getMaxHeight()));
        this.maxHeightField.setEditable(isOwner);
        this.addWidget(this.maxHeightField);

        this.radiusField = new EditBox(this.font, pX + 5, pY - 10, 100, 20, Component.literal("Радиус области"));
        this.radiusField.setValue(String.valueOf((int)mainZone.getRadius()));
        this.radiusField.setEditable(isOwner);
        this.addWidget(this.radiusField);

        Button gpsButton = Button.builder(Component.literal("Привязать к моему GPS"), (button) -> {
            this.xField.setValue(String.valueOf(net.minecraft.util.Mth.floor(this.minecraft.player.getX())));
            this.yField.setValue(String.valueOf(net.minecraft.util.Mth.floor(this.minecraft.player.getY())));
            this.zField.setValue(String.valueOf(net.minecraft.util.Mth.floor(this.minecraft.player.getZ())));
        }).bounds(pX - 105, pY + 20, 210, 20).build();

        Button saveButton = Button.builder(Component.literal("Применить координаты"), (button) -> {
            try {
                mainZone.setX(Double.parseDouble(this.xField.getValue()));
                mainZone.setY(Double.parseDouble(this.yField.getValue()));
                mainZone.setZ(Double.parseDouble(this.zField.getValue()));
                mainZone.setMaxHeight(Double.parseDouble(this.maxHeightField.getValue()));
                mainZone.setRadius(Double.parseDouble(this.radiusField.getValue()));
                
                ZoneManager.saveZones();
                this.minecraft.setScreen(this.parentScreen); // Возвращаемся в хаб после сохранения
            } catch (NumberFormatException e) {
                this.xField.setValue("ОШИБКА!");
            }
        }).bounds(pX - 105, pY + 45, 210, 20).build();

        // ОБЯЗАТЕЛЬНАЯ КНОПКА НАЗАД ДЛЯ ВОЗВРАТА В ХАБ ПУЛЬТА УПРАВЛЕНИЯ
        Button backButton = Button.builder(Component.literal("Назад"), (button) -> {
            this.minecraft.setScreen(this.parentScreen); // Просто возвращаемся в хаб без сохранения изменений
        }).bounds(pX - 105, pY + 70, 210, 20).build();

        gpsButton.active = isOwner;
        saveButton.active = isOwner;

        this.addRenderableWidget(gpsButton);
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(backButton);
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

        String titleText = "НАСТРОЙКИ СТАНЦИИ: \"ГЛ КОМП\"";
        graphics.drawString(this.font, titleText, pX - (this.font.width(titleText) / 2), pY - 75, 0xFFFFFF, false);
        
        graphics.drawString(this.font, "Центр (X / Y / Z):", pX - 105, pY - 62, 0xAAAAAA, false);
        graphics.drawString(this.font, "Макс. высота:", pX - 105, pY - 22, 0xAAAAAA, false);
        graphics.drawString(this.font, "Радиус обл.:", pX + 5, pY - 22, 0xAAAAAA, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
