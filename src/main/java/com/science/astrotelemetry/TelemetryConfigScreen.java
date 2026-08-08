package com.science.astrotelemetry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TelemetryConfigScreen extends Screen {
    private EditBox xField;
    private EditBox zField;
    private EditBox heightField;
    private Button radiusButton;
    private double currentRadius;

    public TelemetryConfigScreen() {
        super(Component.literal("Управление Исследовательской Станцией"));
    }

    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int pX = this.width / 2;
        int pY = this.height / 2;

        // Получаем нашу главную рабочую зону
        TelemetryZone mainZone = ZoneManager.getZones().get(0);
        this.currentRadius = mainZone.getRadius();

        // 1. Поля координат X и Z (опускаем чуть ниже, чтобы разнести с заголовком)
        this.xField = new EditBox(this.font, pX - 100, pY - 45, 95, 20, Component.literal("X"));
        this.xField.setValue(String.valueOf((int)mainZone.getX()));
        this.addWidget(this.xField);

        this.zField = new EditBox(this.font, pX + 5, pY - 45, 95, 20, Component.literal("Z"));
        this.zField.setValue(String.valueOf((int)mainZone.getZ()));
        this.addWidget(this.zField);

        // 2. Поле минимальной высоты Y (сдвигаем ниже, убирая наложение)
        this.heightField = new EditBox(this.font, pX - 100, pY - 5, 200, 20, Component.literal("Мин. Высота"));
        this.heightField.setValue(String.valueOf((int)mainZone.getMinY()));
        this.addWidget(this.heightField);

        // 3. НОВОЕ: Кнопка переключения радиуса (области действия)
        this.radiusButton = Button.builder(Component.literal("Область приема: " + (int)this.currentRadius + " бл."), (button) -> {
            if (this.currentRadius == 6) this.currentRadius = 12;
            else if (this.currentRadius == 12) this.currentRadius = 24;
            else if (this.currentRadius == 24) this.currentRadius = 50;
            else this.currentRadius = 6;
            
            button.setMessage(Component.literal("Область приема: " + (int)this.currentRadius + " бл."));
        }).bounds(pX - 100, pY + 20, 200, 20).build();
        this.addRenderableWidget(this.radiusButton);

        // 4. Кнопка GPS привязки
        Button gpsButton = Button.builder(Component.literal("Привязать к моему GPS"), (button) -> {
            this.xField.setValue(String.valueOf((int)this.minecraft.player.getX()));
            this.zField.setValue(String.valueOf((int)this.minecraft.player.getZ()));
            this.heightField.setValue(String.valueOf((int)this.minecraft.player.getY()));
        }).bounds(pX - 100, pY + 50, 200, 20).build();

        // 5. Кнопка сохранения данных
        Button saveButton = Button.builder(Component.literal("Применить координаты"), (button) -> {
            try {
                double x = Double.parseDouble(this.xField.getValue());
                double z = Double.parseDouble(this.zField.getValue());
                double minY = Double.parseDouble(this.heightField.getValue());

                // Перезаписываем все параметры в конфиг
                mainZone.setX(x);
                mainZone.setZ(z);
                mainZone.setMinY(minY);
                mainZone.setRadius(this.currentRadius); // Сохраняем выбранную область покрытия
                ZoneManager.saveZones();
                
                this.onClose(); 
            } catch (NumberFormatException e) {
                this.heightField.setValue("ОШИБКА ДАННЫХ!");
            }
        }).bounds(pX - 100, pY + 75, 200, 20).build();

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
        if (this.zField != null) this.zField.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (this.heightField != null) this.heightField.renderWidget(graphics, mouseX, mouseY, partialTick);

        // Корректируем позиции текстов-подсказок (разносим их по высоте)
        String titleText = "КООРДИНАТОР ОУС-1 \"ОБСЕРВАТОРИЯ\"";
        graphics.drawString(this.font, titleText, pX - (this.font.width(titleText) / 2), pY - 75, 0xFFFFFF, false);
        
        String label1 = "Координаты центра структуры (X / Z):";
        graphics.drawString(this.font, label1, pX - 100, pY - 58, 0xAAAAAA, false);
        
        String label2 = "Минимальная высота приема сигнала (Y):";
        graphics.drawString(this.font, label2, pX - 100, pY - 18, 0xAAAAAA, false);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; 
    }
}
