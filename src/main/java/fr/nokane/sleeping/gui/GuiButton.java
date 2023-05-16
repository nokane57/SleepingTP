package fr.nokane.sleeping.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GuiButton extends Screen {

    private GuiDormirButton dormirButton;
    private GuiTeleporterButton teleporterButton;

    public GuiButton(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        int x = width / 2 - 100;
        int y = height / 2 - 20;
        int z = 0;

        // Créer le bouton "Dormir"
        dormirButton = new GuiDormirButton(x, y,200, 20, new StringTextComponent("Dormir"));
        addButton(dormirButton);

        // Créer le bouton "Téléporter"
        y += 30;
        teleporterButton = new GuiTeleporterButton(x, y, 200, 20,  new StringTextComponent("Téléporter"));
        addButton(teleporterButton);
    }
}
