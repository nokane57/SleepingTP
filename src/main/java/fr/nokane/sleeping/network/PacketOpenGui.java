package fr.nokane.sleeping.network;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.gui.TeleporteScreen;
import fr.nokane.sleeping.gui.TimerScreen;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenGui {

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (isTimerFinished()) {
                TeleporteScreen.open();
            } else {
                TimerScreen.open();
            }
        });
        return true;
    }

    private boolean isTimerFinished() {
        long currentTime = System.currentTimeMillis();
        long lastTeleportTime = Config.getLastTeleportTime();
        int teleportCooldown = Config.getTeleportCooldown() * 1000; // Convert cooldown to milliseconds

        return currentTime - lastTeleportTime >= teleportCooldown;
    }
}
