package fr.nokane.sleeping.event;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.network.Networking;
import fr.nokane.sleeping.network.PacketOpenGui;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SleepingCrouchEvent {

    @SubscribeEvent
    public static void onPlayerStartCrouching(PlayerSleepInBedEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity && event.getEntity().isCrouching()) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

            // Vérifier si le joueur a été téléporté récemment
            if (Config.hasTeleported()) {
                player.sendMessage(new StringTextComponent("Vous ne pouvez pas dormir après avoir été téléporté."), Util.NIL_UUID);
                event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
                return;
            }

            // Le joueur n'a pas été téléporté récemment, continuer avec le comportement par défaut
            Networking.sendToClient(new PacketOpenGui(), player);
        }
    }
}
