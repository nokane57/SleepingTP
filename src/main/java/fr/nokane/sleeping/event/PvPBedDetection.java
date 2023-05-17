package fr.nokane.sleeping.event;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PvPBedDetection {

    private static int pvpTimer = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (player != null && player.isAlive()) {
            if (pvpTimer > 0) {
                pvpTimer--;
                if (pvpTimer == 0) {
                    Minecraft.getInstance().gui.setOverlayMessage(new StringTextComponent("You are no longer in PvP mode."), false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSleeping(PlayerSleepInBedEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.isCrouching()) {
            if (pvpTimer > 0) {
                player.sendMessage(new StringTextComponent("You cannot use beds while in combat."), player.getUUID());
                event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
            }
        }
    }

    public static void startPvPTimer() {
        pvpTimer = Config.getPvpDetectionTimer();
        Minecraft.getInstance().gui.setOverlayMessage(new StringTextComponent("You are in PvP mode. Beds cannot be used."), false);
    }

    public static boolean isPvPEnabled() {
        return pvpTimer <= 0;
    }
}
