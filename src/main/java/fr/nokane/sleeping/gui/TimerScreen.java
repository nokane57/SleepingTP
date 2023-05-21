package fr.nokane.sleeping.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.network.Networking;
import fr.nokane.sleeping.network.PacketTimer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class TimerScreen extends Screen {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 80;

    private int teleportCooldown;
    private long startTime;
    private int remainingTime;

    protected TimerScreen() {
        super(new StringTextComponent("Timer Screen"));
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TimerScreen());
    }

    @Override
    protected void init() {
        teleportCooldown = Config.getTeleportCooldown();
        startTime = System.currentTimeMillis();
        remainingTime = teleportCooldown;

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        int relX = (width - WIDTH) / 2;
        int relY = (height - HEIGHT) / 2;

        // Calculer le temps restant du cooldown
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        remainingTime = Math.max(teleportCooldown - (int) (elapsedTime / 1000), 0);

        drawCenteredString(matrixStack, font, "Teleport Cooldown: " + remainingTime, width / 2, relY + 20, 0xFFFFFF);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}