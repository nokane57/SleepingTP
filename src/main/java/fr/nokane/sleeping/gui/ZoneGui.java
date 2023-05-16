package fr.nokane.sleeping.gui;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.event.PlayerSleepingEvent;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ZoneGui extends Screen {

    public ZoneGui() {
        super(new StringTextComponent("Zone GUI"));
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 200;
        int buttonHeight = 20;
        int buttonSpacing = 25;

        int x = (this.width - buttonWidth) / 2;
        int y = 50;

        // Create a button for each zone
        for (int i = 0; i < Config.zones.get().size(); i++) {
            String zoneEntry = Config.zones.get().get(i);
            String[] parts = zoneEntry.split(":");
            String name = parts[0];
            ITextComponent buttonText = new StringTextComponent(name);

            addButton(new Button(x, y, buttonWidth, buttonHeight, buttonText, button -> {
                PlayerEntity player = Minecraft.getInstance().player;
                Optional<BlockPos> bedPos = player.getSleepingPos();
                bedPos.ifPresent(player::startSleeping);
                Minecraft.getInstance().player.closeContainer();
                System.out.println("Le joueur dort");
                teleportToZone(zoneEntry);
            }));

            y += buttonSpacing;
        }
    }

    @SubscribeEvent
    public static void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        // Get the player entity
        PlayerEntity player = event.getPlayer();
        // Check if the player is awake
        if (!player.isSleeping()) {
            // Check if the player has pressed the "Dormir" button
            if (player.getPersistentData().getBoolean("SleepButtonPressed")) {
                // The "Dormir" button was pressed
                // Perform the sleeping action
                Optional<BlockPos> bedPos = player.getSleepingPos();
                bedPos.ifPresent(player::startSleeping);
                Minecraft.getInstance().player.closeContainer();
                System.out.println("Le joueur dort");
                // Reset the button press flag
                player.getPersistentData().remove("SleepButtonPressed");
            } else {
                // The "Dormir" button was not pressed
                // Get the player's current position
                BlockPos pos = player.blockPosition();
                // Teleport the player to the specified zone coordinates
                teleportToZone(String.format("Sleeping Position:%d,%d,%d", pos.getX(), pos.getY(), pos.getZ()));
                // Mark the event as successful to prevent the player from sleeping in bed
                event.setResult(PlayerEntity.SleepResult.TOO_FAR_AWAY);
            }
        }
    }

    // ...

    private static void teleportToZone(String zoneEntry) {
        // Extract the coordinates from the zoneEntry string
        String[] parts = zoneEntry.split(":");
        String[] coordinates = parts[1].split(",");
        double x = Double.parseDouble(coordinates[0]);
        double y = Double.parseDouble(coordinates[1]);
        double z = Double.parseDouble(coordinates[2]);

        // Get the player entity
        PlayerEntity player = Minecraft.getInstance().player;
        // Teleport the player to the specified zone coordinates
        if (player != null) {
            player.setPos(x, y, z);
            player.stopSleepInBed(true, true);
        }
    }
}
