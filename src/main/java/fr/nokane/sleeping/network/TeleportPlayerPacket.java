package fr.nokane.sleeping.network;

import fr.nokane.sleeping.config.Config;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class TeleportPlayerPacket {

    private static BlockPos targetPosition;

    public TeleportPlayerPacket(BlockPos targetPosition) {
        this.targetPosition = targetPosition;
    }

    public static void encode(TeleportPlayerPacket packet, PacketBuffer buffer) {
        buffer.writeBlockPos(packet.targetPosition);
    }

    public static TeleportPlayerPacket decode(PacketBuffer buffer) {
        BlockPos targetPosition = buffer.readBlockPos();
        return new TeleportPlayerPacket(targetPosition);
    }

    public static void handle(TeleportPlayerPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Récupérer l'instance du joueur côté serveur
            ServerPlayerEntity player = context.getSender();

            // Vérifier si le joueur est valide et s'il a le droit de téléportation
            if (player != null && canPlayerTeleport(player)) {
                // Téléporter le joueur à la position cible
                player.teleportTo(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
                player.closeContainer();
                // Mettre à jour le temps de dernière téléportation
                long currentTime = System.currentTimeMillis();
                Config.setLastTeleportTime(currentTime);
            }
        });
        context.setPacketHandled(true);
    }

    private static boolean canPlayerTeleport(ServerPlayerEntity player) {
        long currentTime = System.currentTimeMillis();
        long lastTeleportTime = Config.getLastTeleportTime();
        int teleportCooldown = Config.getTeleportCooldown() * 1000; // Convert cooldown to milliseconds

        // Vérifier si le joueur a attendu suffisamment longtemps depuis sa dernière téléportation
        return currentTime - lastTeleportTime >= teleportCooldown;
    }
}