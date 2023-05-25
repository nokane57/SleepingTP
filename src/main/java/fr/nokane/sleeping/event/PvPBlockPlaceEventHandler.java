package fr.nokane.sleeping.event;

import fr.nokane.sleeping.config.Config;

import fr.nokane.sleeping.utils.Reference;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class PvPBlockPlaceEventHandler {
    public static int combatTimer = 0;
    private static final int TICKS_PER_SECOND = 20; // Nombre de ticks par seconde
    private static final Map<PlayerEntity, ItemStack> playerPlacedBlocks = new HashMap<>();

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof PlayerEntity && event.getSource().getEntity() != null) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            // Réinitialiser le timer de combat avec la durée configurée en secondes
            combatTimer = Config.getPvPCombatTimer() * TICKS_PER_SECOND;
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();

            // Vérifier si le joueur est en mode PVP
            if (player.isPushable()) {
                BlockState placedBlockState = event.getPlacedBlock();

                // Vérifier si le bloc placé fait partie de la liste bedBlockNames
                if (Config.bedBlockNames.get().contains(placedBlockState.getBlock().getRegistryName().toString())) {
                    // Vérifier si le joueur est en phase de combat
                    if (combatTimer > 0) {
                        int remainingTimeSeconds = combatTimer / TICKS_PER_SECOND;
                        player.sendMessage(new StringTextComponent("Vous ne pouvez pas poser ce bloc en mode PVP. Temps restant : " + remainingTimeSeconds + " secondes."), Util.NIL_UUID);

                        // Annuler l'événement pour empêcher le placement du bloc
                        event.setCanceled(true);

                        // Vérifier si le joueur a déjà placé ce bloc
                        if (!playerPlacedBlocks.containsKey(player)) {
                            // Conserver une référence de l'élément avant l'annulation de l'événement
                            ItemStack itemStack = placedBlockState.getBlock().asItem().getDefaultInstance();
                            playerPlacedBlocks.put(player, itemStack);
                        }
                    } else {
                        player.sendMessage(new StringTextComponent("Vous n'êtes plus en phase de combat."), Util.NIL_UUID);
                        // Le joueur n'est plus en phase de combat, autoriser la pose du bloc
                        // Vous pouvez ajouter d'autres actions ici si nécessaire
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            // Décrémenter le timer de combat
            if (combatTimer > 0) {
                combatTimer--;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            PlayerEntity player = event.player;

            // Vérifier si le joueur a placé un bloc et si le timer de combat est terminé
            if (playerPlacedBlocks.containsKey(player) && combatTimer == 0) {
                // Récupérer l'élément précédemment placé par le joueur
                ItemStack itemStack = playerPlacedBlocks.get(player);

                // Réinsérer l'élément dans l'inventaire du joueur
                if (itemStack != null && !player.inventory.contains(itemStack)) {
                    player.inventory.add(itemStack);
                }

                // Retirer l'élément de la liste des éléments placés par le joueur
                playerPlacedBlocks.remove(player);
            }
        }
    }
}