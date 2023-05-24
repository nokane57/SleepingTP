package fr.nokane.sleeping.network;

import fr.nokane.sleeping.config.Config;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
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
            ServerPlayerEntity player = context.getSender();
            if (player != null) {
                // Récupérer l'UUID du joueur qui a initié l'action
                UUID initiatingPlayerUUID = player.getUUID();

                // Vérifier si le joueur actuel est le même que celui qui a initié l'action
                if (!player.getUUID().equals(initiatingPlayerUUID)) {
                    // Mettre à jour les valeurs du timer dans la configuration
                    Config.setLastTeleportTime(System.currentTimeMillis());
                    Config.setTeleportCooldown(message.teleportCooldown);
                    // Envoyer un message au joueur avec les informations du timer
                    player.sendMessage(new StringTextComponent("Teleport Cooldown: " + message.teleportCooldown), player.getUUID());
                }
            }
        });
        context.setPacketHandled(true);
    }
}