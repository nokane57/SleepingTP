package fr.nokane.sleeping.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class GuiDormirButton extends Button {

    public GuiDormirButton(int x, int y, int width, int height, ITextComponent text, Button.IPressable onPress) {
        super(x, y, width, height, text, onPress);
    }

    public GuiDormirButton(int x, int y, int width, int height, ITextComponent text) {
        super(x, y, width, height, text, button -> {
            PlayerEntity player = Minecraft.getInstance().player;
            player.getPersistentData().putBoolean("SleepButtonPressed", true);
            Optional<BlockPos> bedPos = player.getSleepingPos();
            bedPos.ifPresent(player::startSleeping);
            Minecraft.getInstance().player.closeContainer();
            System.out.println("Le joueur dort");
        });
    }
}