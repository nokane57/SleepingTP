package fr.nokane.sleeping.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;


public class GuiTeleporterButton extends Button {


    public GuiTeleporterButton(int x, int y, int width, int height, ITextComponent text, Button.IPressable onPress) {
        super(x, y, width, height, text, onPress);
    }

    public GuiTeleporterButton(int x, int y, int width, int height, ITextComponent text) {
        super(x, y, width, height, text, button -> {
            Minecraft.getInstance().setScreen(new ZoneGui());
            System.out.println("Le ce teleporte");
        });
    }
}