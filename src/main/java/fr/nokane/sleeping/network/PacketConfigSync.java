package fr.nokane.sleeping.network;

import fr.nokane.sleeping.config.Config;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PacketConfigSync {

    private int teleportCooldown;
    private int pvpCombatTimer;
    private List<String> zones;

    public PacketConfigSync(int teleportCooldown, int pvpCombatTimer, List<String> zones) {
        this.teleportCooldown = teleportCooldown;
        this.pvpCombatTimer = pvpCombatTimer;
        this.zones = zones;
    }

    public static void encode(PacketConfigSync message, PacketBuffer buffer) {
        buffer.writeInt(message.teleportCooldown);
        buffer.writeInt(message.pvpCombatTimer);
        buffer.writeInt(message.zones.size());
        for (String zone : message.zones) {
            buffer.writeUtf(zone);
        }
    }

    public static PacketConfigSync decode(PacketBuffer buffer) {
        int teleportCooldown = buffer.readInt();
        int pvpCombatTimer = buffer.readInt();
        int numZones = buffer.readInt();
        List<String> zones = new ArrayList<>();
        for (int i = 0; i < numZones; i++) {
            zones.add(buffer.readUtf());
        }
        return new PacketConfigSync(teleportCooldown, pvpCombatTimer, zones);
    }

    public static void handle(PacketConfigSync message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Mettez à jour les valeurs de configuration sur le client avec les données synchronisées
            Config.setTeleportCooldown(message.teleportCooldown);
            Config.setPvPCombatTimer(message.pvpCombatTimer);
            Config.setZones(message.zones);

            // Rafraîchir l'écran de téléportation (s'il est actuellement ouvert) pour refléter les nouvelles valeurs
        });
        context.setPacketHandled(true);
    }
}