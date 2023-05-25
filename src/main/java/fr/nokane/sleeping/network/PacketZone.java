package fr.nokane.sleeping.network;

import fr.nokane.sleeping.config.Config;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketZone {

    private List<String> zoneList;

    public PacketZone(List<String> zoneList) {
        this.zoneList = getZoneListFromConfig();
    }

    public static void encode(PacketZone message, PacketBuffer buffer) {
        buffer.writeInt(message.zoneList.size());
        for (String zoneEntry : message.zoneList) {
            buffer.writeUtf(zoneEntry);
        }
    }

    public static PacketZone decode(PacketBuffer buffer) {
        int size = buffer.readInt();
        List<String> zoneList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String zoneEntry = buffer.readUtf();
            zoneList.add(zoneEntry);
        }
        return new PacketZone(zoneList);
    }

    public static void handle(PacketZone message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            List<String> zoneList = message.zoneList;
            for (String zoneEntry : zoneList) {
                String[] parts = zoneEntry.split(":");
                if (parts.length == 2) {
                    String zoneName = parts[0];
                    String zoneCoordinates = parts[1];
                    // Faire quelque chose avec chaque entrée de zone
                    // ...
                }
            }
        });
        context.setPacketHandled(true);
    }

    public static List<String> getZoneListFromConfig() {
        return new ArrayList<>(Config.zones.get());
    }
}