package com.science.astrotelemetry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TelemetryConfigScreen extends Screen {
    private final Screen parentScreen;
    private final int zoneIndex; 
    private EditBox xField;
    private EditBox yField;
    private EditBox zField;
    private EditBox maxHeightField;
    private EditBox radiusField;
    private EditBox soundRangeField; 
    private EditBox colorField; 
    private double currentVolumeValue;

    public TelemetryConfigScreen(Screen parentScreen, int zoneIndex) {
        super(Component.literal("Управление Исследовательской Станцией"));
        this.parentScreen = parentScreen;
        this.zoneIndex = zoneIndex;
    }

    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        // Проверка: вы хозяин или обычный гость?
        boolean isOwner = this.minecraft.player.getGameProfile().getName().equals("artem26841");

        int pX = this.width / 2;
        int pY = this.height / 2;

        TelemetryZone currentZone = ZoneManager.getZones().get(this.zoneIndex);
        ZoneManager.PlayerPreset preset = ZoneManager.getPreset(this.zoneIndex);
        
        this.currentVolumeValue = preset.soundVolume / 5.0;
        if (this.currentVolumeValue > 1.0) this.currentVolumeValue = 1.0;
        if (this.currentVolumeValue < 0.0) this.currentVolumeValue = 0.0;

        // Координаты структуры (Управляет ТОЛЬКО artem26841)
        this.xField = new EditBox(this.font, pX - 105, pY - 55, 65, 20, Component.literal("X"));
        this.xField.setValue(String.valueOf((int)currentZone.getX()));
        this.xField.setEditable(isOwner);
        this.addWidget(this.xField);

        this.yField = new EditBox(this.font, pX - 35, pY - 55, 65, 20, Component.literal("Y"));
        this.yField.setValue(String.valueOf((int)currentZone.getY()));
        this.yField.setEditable(isOwner);
        this.addWidget(this.yField);

        this.zField = new EditBox(this.font, pX + 35, pY - 55, 65, 20, Component.literal("Z"));
        this.zField.setValue(String.valueOf((int)currentZone.getZ()));
        this.zField.setEditable(isOwner);
        this.addWidget(this.zField);

        this.maxHeightField = new EditBox(this.font, pX - 105, pY - 15, 100, 20, Component.literal("Макс. Высота"));
        this.maxHeightField.setValue(String.valueOf((int)currentZone.getMaxHeight()));
        this.maxHeightField.setEditable(isOwner);
        this.addWidget(this.maxHeightField);

        this.radiusField = new EditBox(this.font, pX + 5, pY - 15, 100, 20, Component.literal("Радиус области"));
        this.radiusField.setValue(String.valueOf((int)currentZone.getRadius()));
        this.radiusField.setEditable(isOwner);
        this.addWidget(this.radiusField);

        this.colorField = new EditBox(this.font, pX - 105, pY + 65, 210, 20, Component.literal("Цвет текста HEX"));
        this.colorField.setValue(preset.textColor);
        this.colorField.setEditable(true); // Открыто для всех отдельно в каждой зоне!
        this.addWidget(this.colorField);

        if (this.zoneIndex == 0) {
            AbstractSliderButton volumeSlider = new AbstractSliderButton(pX + 5, pY + 25, 100, 20, 
                    Component.literal("Громкость: " + (int)(this.currentVolumeValue * 100) + "%"), this.currentVolumeValue) {
                @Override
                protected void updateMessage() {
                    this.setMessage(Component.literal("Громкость: " + (int)(this.value * 100) + "%"));
                }
                @Override
                protected void applyValue() {
                    // Ползунком может двигать любой игрок для себя
                    TelemetryConfigScreen.this.currentVolumeValue = this.value;
                }
            };
            this.addRenderableWidget(volumeSlider);
        }

        // Кнопка GPS (Работает только у вас)
        Button gpsButton = Button.builder(Component.literal("Привязать к моему GPS"), (button) -> {
            this.xField.setValue(String.valueOf(net.minecraft.util.Mth.floor(this.minecraft.player.getX())));
            this.yField.setValue(String.valueOf(net.minecraft.util.Mth.floor(this.minecraft.player.getY())));
            this.zField.setValue(String.valueOf(net.minecraft.util.Mth.floor(this.minecraft.player.getZ())));
        }).bounds(pX - 105, pY + 95, 210, 20).build();
        gpsButton.active = isOwner;
        this.addRenderableWidget(gpsButton);

        // Кнопка применить (Работает у всех, но сохраняет разные файлы!)
        Button saveButton = Button.builder(Component.literal("Применить настройки"), (button) -> {
            try {
                // Если зашел админ — обновляем глобальные координаты
                if (isOwner) {
                    currentZone.setX(Double.parseDouble(this.xField.getValue()));
                    currentZone.setY(Double.parseDouble(this.yField.getValue()));
                    currentZone.setZ(Double.parseDouble(this.zField.getValue()));
                    currentZone.setMaxHeight(Double.parseDouble(this.maxHeightField.getValue()));
                    currentZone.setRadius(Double.parseDouble(this.radiusField.getValue()));
                    ZoneManager.saveZones();
                }
                
                // Сохраняем ЛИЧНЫЕ настройки цвета и звука текущего игрока в его пресет
                if (this.colorField != null) {
                    String hexInput = this.colorField.getValue().trim();
                    if (!hexInput.startsWith("#")) hexInput = "#" + hexInput;
                    preset.textColor = hexInput;
                }
                
                if (this.zoneIndex == 0) {
                    preset.soundVolume = this.currentVolumeValue * 5.0;
                }

                ZoneManager.savePlayerPresets(); // Пишем в личный json игрока
                this.minecraft.setScreen(this.parentScreen); 
            } catch (NumberFormatException e) {
                this.xField.setValue("ОШИБКА!");
            }
        }).bounds(pX - 105, pY + 120, 210, 20).build();
        this.addRenderableWidget(saveButton);

        this.addRenderableWidget(Button.builder(Component.literal("Назад"), (button) -> {
            this.minecraft.setScreen(this.parentScreen); 
        }).bounds(pX - 105, pY + 145, 210, 20).build());
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
        if (this.colorField != null) this.colorField.renderWidget(graphics, mouseX, mouseY, partialTick);

        String zoneName = this.zoneIndex == 0 ? "ЦВМ ОУС-1 \"ГЛ КОМП\"" : "КОМПЛЕКС \"УПОИР v-1.2.1\"";
        String titleText = "НАСТРОЙКИ СТАНЦИИ: " + zoneName;
        graphics.drawString(this.font, titleText, pX - (this.font.width(titleText) / 2), pY - 80, 0xFFFFFF, false);
        
        graphics.drawString(this.font, "Центр структуры (Только для artem26841):", pX - 105, pY - 67, 0xAAAAAA, false);
        graphics.drawString(this.font, "Макс. высота:", pX - 105, pY - 27, 0xAAAAAA, false);
        graphics.drawString(this.font, "Радиус обл.:", pX + 5, pY - 27, 0xAAAAAA, false);
        graphics.drawString(this.font, "ЛИЧНЫЙ HEX цвет этой зоны (например, #FF1B18):", pX - 105, pY + 53, 0xAAAAAA, false);

        if (this.zoneIndex == 0) {
            graphics.drawString(this.font, "Мощность звука:", pX + 5, pY + 13, 0xAAAAAA, false);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
