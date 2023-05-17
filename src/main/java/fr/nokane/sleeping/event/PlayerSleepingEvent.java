package fr.nokane.sleeping.event;

import fr.nokane.sleeping.SleepingTP;
import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.gui.GuiButton;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayerSleepingEvent {

    private static int getRemainingCooldown(PlayerEntity player) {
        long currentTime = System.currentTimeMillis();
        long lastTeleportTime = Config.getLastTeleportTime();
        int teleportCooldown = Config.getTeleportCooldown();

        int remainingCooldown = (int) Math.max(0, (lastTeleportTime + teleportCooldown * 1000 - currentTime) / 1000);
        return remainingCooldown;
    }

    @SubscribeEvent
    public static void onPlayerSleeping(PlayerSleepInBedEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.isCrouching()) {
            if (!Config.isTeleportEnabled()) {
                player.sendMessage(new StringTextComponent("La téléportation est désactivée."), player.getUUID());
                return; // Bloquer l'action si la téléportation n'est pas activée dans la configuration
            }

            int remainingCooldown = getRemainingCooldown(player);
            if (remainingCooldown > 0) {
                String message = String.format("Veuillez patienter %d secondes avant de vous téléporter à nouveau.", remainingCooldown);
                player.sendMessage(new StringTextComponent(message), Util.NIL_UUID);
                return; // Bloquer l'action si le cooldown n'est pas écoulé
            }

            World world = event.getEntity().getCommandSenderWorld();
            BlockPos pos = event.getPos();
            BlockState state = world.getBlockState(pos);

            ForgeConfigSpec.ConfigValue<List<? extends String>> bedBlockNamesConfig = Config.bedBlockNames;
            List<String> bedBlockNamesList = (List<String>) bedBlockNamesConfig.get();
            List<Block> bedBlocks = new ArrayList<>();
            for (String blockName : bedBlockNamesList) {
                ResourceLocation blockLocation = new ResourceLocation(blockName);
                Block block = ForgeRegistries.BLOCKS.getValue(blockLocation);
                if (block != null) {
                    bedBlocks.add(block);
                } else {
                    SleepingTP.getLogger().error("Invalid bed block name: " + blockName);
                }
            }
            if (bedBlocks.contains(state.getBlock())) {
                Minecraft.getInstance().setScreen(new GuiButton(new TranslationTextComponent("gui.sleeping.title")));
            }

            if (!player.isSleeping()) {
                // Get the player's current position
                // Teleport the player to the specified zone coordinates
                teleportToZone(String.format("Sleeping Position:%d,%d,%d", pos.getX(), pos.getY(), pos.getZ()));
                // Mark the event as successful to prevent the player from sleeping in bed
                event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
            }
        }
    }

    public static void teleportToZone(String zoneEntry) {
        // Extract the coordinates from the zoneEntry string
        String[] parts = zoneEntry.split(":");
        String[] coordinates = parts[1].split(",");
        double x = Double.parseDouble(coordinates[0]);
        double y = Double.parseDouble(coordinates[1]);
        double z = Double.parseDouble(coordinates[2]);

        // Get the player entity
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            // Teleport the player to the specified zone coordinates
            player.setPos(x, y, z);
            player.stopSleepInBed(true, true);
            Config.setLastTeleportTime(System.currentTimeMillis()); // Mettre à jour le temps de la dernière téléportation dans la configuration
        } else {
            Optional<BlockPos> bedPos = player.getSleepingPos();
            bedPos.ifPresent(pos -> {
                player.startSleeping(pos);
                Minecraft.getInstance().player.closeContainer();
                Config.setLastTeleportTime(System.currentTimeMillis()); // Mettre à jour le temps de la dernière téléportation dans la configuration
            });
            System.out.println("Le joueur dort");
        }
    }
}