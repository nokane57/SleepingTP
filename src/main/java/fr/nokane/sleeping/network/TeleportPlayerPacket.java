package fr.nokane.sleeping.network;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.event.PvPBlockPlaceEventHandler;
import fr.nokane.sleeping.gui.TimerScreen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

import static fr.nokane.sleeping.event.PvPBlockPlaceEventHandler.combatTimer;

public class TeleportPlayerPacket {

    private BlockPos targetPosition;

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

            // Vérifier si le joueur est valide, s'il a le droit de téléportation et s'il est en combat
            if (player != null && canPlayerTeleport(player) && !isPlayerInCombat()) {
                // Téléporter le joueur à la position cible
                player.teleportTo(packet.targetPosition.getX(), packet.targetPosition.getY(), packet.targetPosition.getZ());
                player.closeContainer();

                // Mettre à jour le temps de dernière téléportation
                long currentTime = System.currentTimeMillis();
                Config.setLastTeleportTime(currentTime);
            } else {
                // Envoyer un message au joueur en fonction de la raison pour laquelle la téléportation est annulée
                if (isPlayerInCombat()) {
                    player.sendMessage(new StringTextComponent("Vous ne pouvez pas vous téléporter en étant en combat."), player.getUUID());
                } else {
                    long currentTime = System.currentTimeMillis();
                    long lastTeleportTime = Config.getLastTeleportTime();
                    int teleportCooldown = Config.getTeleportCooldown() * 1000; // Convertir le cooldown en millisecondes
                    int remainingTime = (int) ((lastTeleportTime + teleportCooldown - currentTime) / 1000); // Temps restant en secondes

                    player.sendMessage(new StringTextComponent("Vous devez attendre encore " + remainingTime + " secondes avant de pouvoir vous téléporter."), player.getUUID());
                }
            }
        });
        context.setPacketHandled(true);
    }

    private static boolean isPlayerInCombat() {
        // Vérifier si le joueur est en combat (utilisez votre logique spécifique pour déterminer si le joueur est en combat)
        return PvPBlockPlaceEventHandler.combatTimer > 0;
    }

    private static boolean canPlayerTeleport(ServerPlayerEntity player) {
        long currentTime = System.currentTimeMillis();
        long lastTeleportTime = Config.getLastTeleportTime();
        int teleportCooldown = Config.getTeleportCooldown() * 1000; // Convert cooldown to milliseconds

        // Vérifier si le joueur a attendu suffisamment longtemps depuis sa dernière téléportation
        return currentTime - lastTeleportTime >= teleportCooldown;
    }
}