package fr.nokane.sleeping.gui;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.event.PlayerSleepingEvent;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;


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
                PlayerSleepingEvent.teleportToZone(getMinecraft().player, zoneEntry);
                Minecraft.getInstance().player.closeContainer();
            }));
            y += buttonSpacing;
        }
    }
}
