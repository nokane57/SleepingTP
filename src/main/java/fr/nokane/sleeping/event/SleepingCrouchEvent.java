package fr.nokane.sleeping.event;

import fr.nokane.sleeping.network.Networking;
import fr.nokane.sleeping.network.PacketOpenGui;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SleepingCrouchEvent {

    @SubscribeEvent
    public static void onPlayerStartCrouching(PlayerSleepInBedEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity && event.getEntity().isCrouching()) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            Networking.sendToClient(new PacketOpenGui(), player);
        }
    }
}
