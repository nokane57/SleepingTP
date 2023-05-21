package fr.nokane.sleeping;

import com.mojang.brigadier.CommandDispatcher;
import fr.nokane.sleeping.command.ReturnToBedCommand;
import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.event.PvPBlockPlaceEventHandler;
import fr.nokane.sleeping.event.SleepingCrouchEvent;
import fr.nokane.sleeping.network.Networking;
import fr.nokane.sleeping.utils.Reference;
import net.minecraft.command.CommandSource;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@Mod(value = Reference.MOD_ID)
public class SleepingTP {
    public static final Logger logger = LogManager.getLogger();
    private final ForgeConfigSpec config;

    public SleepingTP() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        Config.setup();
        config = Config.CONFIG;
    }

    public void setup(FMLCommonSetupEvent event) {

        MinecraftForge.EVENT_BUS.register(new SleepingCrouchEvent());
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(new PvPBlockPlaceEventHandler());
        Networking.registerMessaged();
        saveConfig();

        if (config != null) {
            Config.teleportCooldown.get();
            Config.bedBlockNames.get();
        } else {
            getLogger().error("Erreur lors du chargement de la configuration !");
        }
    }

    public void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        ReturnToBedCommand.register(dispatcher);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        // Configurer des fonctionnalités spécifiques au côté client, le cas échéant
    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        // Code à exécuter lorsque le serveur démarre
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {
        // Code à exécuter lorsque le serveur s'arrête
    }

    public static void saveConfig() {
        if (Config.CONFIG != null) {
            try {
                Config.CONFIG.save();
            } catch (Exception e) {
                getLogger().error("Une erreur est survenue lors de la sauvegarde de la configuration !", e);
            }
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}