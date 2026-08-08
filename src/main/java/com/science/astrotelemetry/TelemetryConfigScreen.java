package com.science.astrotelemetry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TelemetryConfigScreen extends Screen {
    private EditBox nameField;
    private EditBox xField;
    private EditBox zField;
    private EditBox radiusField;

    public TelemetryConfigScreen() {
        super(Component.literal("Настройка Бортового Компьютера"));
    }

    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int pX = this.width / 2;
        int pY = this.height / 2;

        this.nameField = new EditBox(this.font, pX - 100, pY - 60, 200, 20, Component.literal("Имя"));
        this.nameField.setValue("Новый Сектор");
        this.addWidget(this.nameField);

        this.xField = new EditBox(this.font, pX - 100, pY - 35, 95, 20, Component.literal("X"));
        this.xField.setValue(String.valueOf((int)this.minecraft.player.getX()));
        this.addWidget(this.xField);

        this.zField = new EditBox(this.font, pX + 5, pY - 35, 95, 20, Component.literal("Z"));
        this.zField.setValue(String.valueOf((int)this.minecraft.player.getZ()));
        this.addWidget(this.zField);

        this.radiusField = new EditBox(this.font, pX - 100, pY - 10, 200, 20, Component.literal("Радиус"));
        this.radiusField.setValue("6"); 
        this.addWidget(this.radiusField);

        Button saveButton = Button.builder(Component.literal("Добавить Зону"), (button) -> {
            try {
                String name = this.nameField.getValue();
                double x = Double.parseDouble(this.xField.getValue());
                double z = Double.parseDouble(this.zField.getValue());
                double radius = Double.parseDouble(this.radiusField.getValue());

                ZoneManager.addZone(new TelemetryZone(name, x, z, radius, 1420.4, "Спутник"));
                this.onClose(); 
            } catch (NumberFormatException e) {
                this.nameField.setValue("ОШИБКА ВВОДА!");
            }
        }).bounds(pX - 100, pY + 20, 200, 20).build();

        this.addRenderableWidget(saveButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft != null && this.minecraft.level != null) {
            graphics.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xD0101010);
        }
        
        int pX = this.width / 2;
        int pY = this.height / 2;

        // ИСПРАВЛЕНО: Безопасный рендеринг виджетов ввода данных
        if (this.nameField != null) this.nameField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.xField != null) this.xField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.zField != null) this.zField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.radiusField != null) this.radiusField.renderWidget(graphics, mouseX, mouseY, partialTick);

        // ИСПРАВЛЕНО: Безопасный метод отрисовки текста без конфликтов с маппингами Forge 47.4.22
        String titleText = "НАСТРОЙКА СПУТНИКОВОЙ СЕТИ";
        int textWidth = this.font.width(titleText);
        graphics.drawString(this.font, titleText, pX - (textWidth / 2), pY - 80, 0xFFFFFF, false);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}
