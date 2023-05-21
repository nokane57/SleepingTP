package fr.nokane.sleeping.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import fr.nokane.sleeping.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class TimerScreen extends Screen {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 80;

    private int teleportCooldown;
    private long startTime;
    private int remainingTime;

    public TimerScreen() {
        super(new StringTextComponent("Timer Screen"));
    }

    public TimerScreen(int remainingCooldown) {
        super(new StringTextComponent("Timer Screen"));
        teleportCooldown = remainingCooldown;
        startTime = System.currentTimeMillis();
        remainingTime = teleportCooldown;
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new TimerScreen());
    }

    public static void openWithCooldown(int remainingCooldown) {
        Minecraft.getInstance().setScreen(new TimerScreen(remainingCooldown));
    }

    @Override
    protected void init() {
        teleportCooldown = Config.getTeleportCooldown();
        startTime = Config.getLastTeleportTime();
        remainingTime = Math.max(teleportCooldown - (int) ((System.currentTimeMillis() - startTime) / 1000), 0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        if (remainingTime <= 0) {
            // Le timer est terminé, effectuez le switch automatique de fenêtre ici
            if (minecraft.screen instanceof TimerScreen) {
                minecraft.setScreen(new TeleporteScreen());
                return;
            }
        }

        int relX = (width - WIDTH) / 2;
        int relY = (height - HEIGHT) / 2;

        // Calculer le temps restant du cooldown
        remainingTime = Math.max(teleportCooldown - (int) ((System.currentTimeMillis() - startTime) / 1000), 0);

        drawCenteredString(matrixStack, font, "Teleport Cooldown: " + remainingTime, width / 2, relY + 20, 0xFFFFFF);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}