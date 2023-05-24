package fr.nokane.sleeping.network;

import fr.nokane.sleeping.utils.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

    private static SimpleChannel INSTANCE;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessaged() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Reference.MOD_ID, "sleepingtp"),
                () -> "1.0",
                s -> true,
                s -> true);
        INSTANCE.messageBuilder(PacketOpenGui.class, nextID())
                .encoder(((packetOpenGui, packetBuffer) -> {}))
                .decoder(packetBuffer -> new PacketOpenGui())
                .consumer(PacketOpenGui::handle)
                .add();
        INSTANCE.messageBuilder(PacketZone.class, nextID())
                .encoder(PacketZone::encode)
                .decoder(PacketZone::decode)
                .consumer(PacketZone::handle)
                .add();
        INSTANCE.messageBuilder(PacketTimer.class, nextID())
                .encoder(PacketTimer::encode)
                .decoder(PacketTimer::decode)
                .consumer(PacketTimer::handle)
                .add();
        INSTANCE.messageBuilder(TeleportPlayerPacket.class, nextID())
                .encoder(TeleportPlayerPacket::encode)
                .decoder(TeleportPlayerPacket::decode)
                .consumer(TeleportPlayerPacket::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        if (INSTANCE != null && player != null && player.connection != null && player.connection.connection != null) {
            INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void senToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}