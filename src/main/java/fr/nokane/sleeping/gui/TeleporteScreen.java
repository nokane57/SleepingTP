package fr.nokane.sleeping.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.network.Networking;
import fr.nokane.sleeping.network.TeleportPlayerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;


public class TeleporteScreen extends Screen {

    protected TeleporteScreen() {
        super(new TranslationTextComponent("screen.sleepingtp.teleport"));
    }

    @Override
    protected void init() {
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
            final BlockPos coordinates = parseCoordinates(parts[1]); // Store coordinates as BlockPos

            ITextComponent buttonText = new StringTextComponent(name);

            addButton(new Button(x, y, buttonWidth, buttonHeight, buttonText, button -> {
                if (!isTimerRunning()) {
                    TeleportPlayerPacket packet = new TeleportPlayerPacket(coordinates);
                    Networking.senToServer(packet);
                }
            }));
            y += buttonSpacing;
        }
    }

    private BlockPos parseCoordinates(String coordinates) {
        String[] parts = coordinates.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        int z = Integer.parseInt(parts[2]);
        return new BlockPos(x, y, z);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TeleporteScreen());
    }

    private boolean isTimerRunning() {
        long currentTime = System.currentTimeMillis();
        long lastTeleportTime = Config.getLastTeleportTime();
        int teleportCooldown = Config.getTeleportCooldown() * 1000; // Convert cooldown to milliseconds

        return currentTime - lastTeleportTime < teleportCooldown;
    }
}