package fr.nokane.sleeping.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class GuiButton extends Screen {

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

        // Créer le bouton "Téléporter"
        y += 30;
        teleporterButton = new GuiTeleporterButton(x, y, 200, 20, new StringTextComponent("Téléporter"));
        addButton(teleporterButton);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack ,mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (teleporterButton.mouseClicked(mouseX, mouseY, button)) {
            // Récupérer le joueur qui a ouvert l'écran
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                // Ouvrir l'écran côté serveur
                minecraft.player.closeContainer();
                minecraft.setScreen(this);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
