package com.science.astrotelemetry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TelemetryHubScreen extends Screen {

    public TelemetryHubScreen() {
        super(Component.literal("Главный Хаб Обсерватории"));
    }

    @Override
    protected void init() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        boolean isOwner = this.minecraft.player.getGameProfile().getName().equals("artem26841");

        int pX = this.width / 2;
        int pY = this.height / 2;

        // Кнопка 1: ГЛ КОМП (Зона 0)
        this.addRenderableWidget(Button.builder(Component.literal("ЦВМ ОУС-1 \"ГЛ КОМП\""), (button) -> {
            this.minecraft.setScreen(new TelemetryConfigScreen(this, 0));
        }).bounds(pX - 100, pY - 35, 200, 20).build());

        // Кнопка 2: УПОИР (Зона 1) -> ТЕПЕРЬ АКТИВНА!
        this.addRenderableWidget(Button.builder(Component.literal("КОМПЛЕКС \"УПОИР v-1.2.1\""), (button) -> {
            this.minecraft.setScreen(new TelemetryConfigScreen(this, 1));
        }).bounds(pX - 100, pY - 5, 200, 20).build());

        // Кнопка 3: ОРТ-32 Квазар (Пока заблокирована)
        Button empty2 = Button.builder(Component.literal("ОРТ-32 \"КВАЗАР\" [НЕТ СИГНАЛА]"), (button) -> {}).bounds(pX - 100, pY + 25, 200, 20).build();
        empty2.active = false;
        this.addRenderableWidget(empty2);

        // Кнопка ТРАНСЛИРОВ
        Button transButton = Button.builder(Component.literal("ТРАНСЛИРОВ"), (button) -> {
            NetworkManager.sendTelemetryPacket();
            this.onClose();
        }).bounds(15, this.height - 35, 90, 20).build();
        
        transButton.active = isOwner;
        this.addRenderableWidget(transButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft != null && this.minecraft.level != null) {
            graphics.fillGradient(0, 0, this.width, this.height, 0xD0101010, 0xD0101010);
        }
        int pX = this.width / 2;
        int pY = this.height / 2;

        String titleText = "НАСТРОЙКА ЗОН АПСЕРВОТОРИИ";
        graphics.drawString(this.font, titleText, pX - (this.font.width(titleText) / 2), pY - 60, 0xFFFFFF, false);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
