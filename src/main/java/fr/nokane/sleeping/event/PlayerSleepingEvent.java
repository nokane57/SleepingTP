package fr.nokane.sleeping.event;

import fr.nokane.sleeping.SleepingTP;
import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.gui.GuiButton;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;



@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSleepingEvent {

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
            PlayerEntity player = event.getPlayer();
            World world = event.getWorld();
            BlockPos pos = event.getPos();
            BlockState state = world.getBlockState(pos);
            ForgeConfigSpec.ConfigValue<List<? extends String>> bedBlockNamesConfig = Config.bedBlockNames;
            List<String> bedBlockNamesList = (List<String>) bedBlockNamesConfig.get();
            List<Block> bedBlocks = new ArrayList<>();
            for (String blockName : bedBlockNamesList) {
                ResourceLocation blockLocation = new ResourceLocation(blockName);
                Block block = ForgeRegistries.BLOCKS.getValue(blockLocation);
                if (block != null) {
                    bedBlocks.add(block);
                } else {
                    SleepingTP.getLogger().error("Invalid bed block name: " + blockName);
                }
            }
                if (bedBlocks.contains(state.getBlock())) {
                    Minecraft.getInstance().setScreen(new GuiButton(new TranslationTextComponent("gui.sleeping.title")));
            }
        }
    }
