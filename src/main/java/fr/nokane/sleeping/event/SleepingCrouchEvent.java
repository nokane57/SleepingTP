package fr.nokane.sleeping.event;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.network.Networking;
import fr.nokane.sleeping.network.PacketOpenGui;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SleepingCrouchEvent {

    @SubscribeEvent
    public static void onPlayerStartCrouching(PlayerSleepInBedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayerEntity))
            return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();

        // Vérifier si le joueur a été téléporté récemment
        if (Config.hasTeleported()) {
            player.sendMessage(new StringTextComponent("Vous ne pouvez pas dormir après avoir été téléporté."), Util.NIL_UUID);
            event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
            return;
        }

        // Vérifier si le bloc de lit utilisé est valide
        World world = event.getEntity().getCommandSenderWorld();
        BlockPos bedPos = event.getPos();
        BlockState bedState = world.getBlockState(bedPos);
        ResourceLocation bedBlockLocation = bedState.getBlock().getRegistryName();
        List<String> allowedBedBlockNames = (List<String>) Config.bedBlockNames.get();

        if (!allowedBedBlockNames.contains(bedBlockLocation.toString())) {
            // Le bloc de lit utilisé n'est pas autorisé, arrêter l'événement
            return;
        }

        // Le lit est autorisé, le joueur n'a pas été téléporté récemment et le bloc de lit est valide, continuer avec le comportement par défaut
        Networking.sendToClient(new PacketOpenGui(), player);
    }
}
