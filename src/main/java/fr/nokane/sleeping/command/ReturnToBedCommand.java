package fr.nokane.sleeping.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.nokane.sleeping.config.Config;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

public class ReturnToBedCommand {
    private static long lastTeleportTime = 0;
    private static final long TELEPORT_COOLDOWN = Config.commandTeleportCooldownTimer.get() * 60 * 1000; // Convertir les minutes en millisecondes


    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("returnToBed")
                        .requires(source -> source.hasPermission(0))
                .executes(context -> returnToBed(context.getSource())));
    }

    private static int returnToBed(CommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrException();
        BlockPos bedLocation = player.getRespawnPosition();


        if (bedLocation == null) {
            source.sendFailure(new StringTextComponent("Vous n'avez pas de lit enregistré."));
            return 0;
        }

        // Vérifier si le lit est accessible (non obstrué)
        if (!player.level.getBlockState(bedLocation).isBed(player.level, bedLocation, player)) {
            source.sendFailure(new StringTextComponent("Votre lit est obstrué."));
            return 0;
        }

        // Vérifier si le lit est suffisamment éloigné des dangers (comme les monstres)
        if (!player.level.noCollision(player, player.getBoundingBox().deflate(0.2))) {
            source.sendFailure(new StringTextComponent("Votre lit est dangereux."));
            return 0;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime < TELEPORT_COOLDOWN) {
            long remainingTime = (TELEPORT_COOLDOWN - (currentTime - lastTeleportTime)) / 1000;
            source.sendFailure(new StringTextComponent("Vous devez attendre encore " + remainingTime + " secondes avant de pouvoir vous téléporter à nouveau."));
            return 0;
        }

        // Téléporter le joueur à son lit
        player.teleportTo(bedLocation.getX() + 1.5, bedLocation.getY() + 1.5, bedLocation.getZ() + 1.5);
        lastTeleportTime = currentTime;

        source.sendSuccess(new StringTextComponent("Vous êtes retourné à votre lit."), true);
        return 1;
    }
}
