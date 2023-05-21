package fr.nokane.sleeping.network;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.gui.TimerScreen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;


public class PacketTimer {
    private int teleportCooldown;

    public PacketTimer(int teleportCooldown) {
        this.teleportCooldown = teleportCooldown;
    }

    public static void encode(PacketTimer message, PacketBuffer buffer) {
        buffer.writeInt(message.teleportCooldown);
    }

    public static PacketTimer decode(PacketBuffer buffer) {
        int teleportCooldown = buffer.readInt();
        return new PacketTimer(teleportCooldown);
    }

    public static void handle(PacketTimer message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Mettre à jour les valeurs du timer dans la configuration
            Config.setLastTeleportTime(System.currentTimeMillis());
            Config.setTeleportCooldown(message.teleportCooldown);

            // Afficher l'écran TimerScreen côté client
            TimerScreen.open();
        });
        context.setPacketHandled(true);
    }
}
