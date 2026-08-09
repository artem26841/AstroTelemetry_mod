package com.science.astrotelemetry;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = AstroTelemetry.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class NetworkManager {

    public static void sendTelemetryPacket() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || ZoneManager.getZones().isEmpty()) return;

        TelemetryZone zone = ZoneManager.getZones().get(0);
        
        String packet = "» [AstroNet] Синхронизация частот... #AstroData:" + 
                        zone.getX() + ":" + zone.getY() + ":" + zone.getZ() + ":" + 
                        zone.getRadius() + ":" + zone.getMaxHeight();
        
        mc.player.connection.sendChat(packet);
    }

    @SubscribeEvent
    public static void onChatReceived(net.minecraftforge.client.event.ClientChatReceivedEvent event) {
        String message = event.getMessage().getString();

        if (message.contains("#AstroData") && message.contains("artem26841")) {
            try {
                String dataPart = message.substring(message.indexOf("#AstroData:") + 11);
                String[] tokens = dataPart.split(":");
                
                double x = Double.parseDouble(tokens[0]);
                double y = Double.parseDouble(tokens[1]);
                double z = Double.parseDouble(tokens[2]);
                double radius = Double.parseDouble(tokens[3]);
                double maxH = Double.parseDouble(tokens[4]);

                if (!ZoneManager.getZones().isEmpty()) {
                    TelemetryZone zone = ZoneManager.getZones().get(0);
                    zone.setX(x);
                    zone.setY(y);
                    zone.setZ(z);
                    zone.setRadius(radius);
                    zone.setMaxHeight(maxH);
                    ZoneManager.saveZones();
                }
            } catch (Exception e) {
                AstroTelemetry.LOGGER.error("Ошибка чтения сетевого пакета телеметрии");
            }
        }
    }
}
