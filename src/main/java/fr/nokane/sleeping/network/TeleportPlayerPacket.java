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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
            ServerPlayerEntity player = context.getSender();
            if (player != null) {
                UUID playerUUID = player.getUUID();

                if (canPlayerTeleport(playerUUID) && !isPlayerInCombat()) {
                    player.teleportTo(packet.targetPosition.getX(), packet.targetPosition.getY(), packet.targetPosition.getZ());
                    player.closeContainer();

                    // Mettre à jour le temps de dernière téléportation spécifique au joueur
                    updateLastTeleportTime(playerUUID);

                    // Envoyer un message au joueur avec les informations du timer
                    int remainingTime = getRemainingTeleportCooldown(playerUUID);
                    player.sendMessage(new StringTextComponent("Vous devez attendre encore " + remainingTime + " secondes avant de pouvoir vous téléporter."), player.getUUID());
                } else {
                    // Envoyer un message au joueur en fonction de la raison pour laquelle la téléportation est annulée
                    if (isPlayerInCombat()) {
                        player.sendMessage(new StringTextComponent("Vous ne pouvez pas vous téléporter en étant en combat."), player.getUUID());
                    } else {
                        int remainingTime = getRemainingTeleportCooldown(playerUUID);
                        player.sendMessage(new StringTextComponent("Vous devez attendre encore " + remainingTime + " secondes avant de pouvoir vous téléporter."), player.getUUID());
                    }
                }
            }
        });

        context.setPacketHandled(true);
    }

    private static Map<UUID, Long> lastTeleportTimes = new HashMap<>();
    private static int teleportCooldownSeconds = 60;

    private static boolean isPlayerInCombat() {
        // Vérifier si le joueur est en combat (utilisez votre logique spécifique pour déterminer si le joueur est en combat)
        return PvPBlockPlaceEventHandler.combatTimer > 0;
    }

    private static boolean canPlayerTeleport(UUID playerUUID) {
        long currentTime = System.currentTimeMillis();
        long lastTeleportTime = lastTeleportTimes.getOrDefault(playerUUID, 0L);
        long teleportCooldownMillis = teleportCooldownSeconds * 1000;

        // Vérifier si le joueur a attendu suffisamment longtemps depuis sa dernière téléportation
        return currentTime - lastTeleportTime >= teleportCooldownMillis;
    }

    private static void updateLastTeleportTime(UUID playerUUID) {
        long currentTime = System.currentTimeMillis();
        lastTeleportTimes.put(playerUUID, currentTime);
    }

    private static int getRemainingTeleportCooldown(UUID playerUUID) {
        long currentTime = System.currentTimeMillis();
        long lastTeleportTime = lastTeleportTimes.getOrDefault(playerUUID, 0L);
        long teleportCooldownMillis = teleportCooldownSeconds * 1000;
        long remainingTimeMillis = lastTeleportTime + teleportCooldownMillis - currentTime;

        // Convertir le temps restant en secondes
        int remainingTimeSeconds = (int) (remainingTimeMillis / 1000);
        return remainingTimeSeconds > 0 ? remainingTimeSeconds : 0;
    }
}