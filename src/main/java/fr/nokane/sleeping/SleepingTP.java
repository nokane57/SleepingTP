package fr.nokane.sleeping;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.event.SleepingCrouchEvent;
import fr.nokane.sleeping.network.Networking;
import fr.nokane.sleeping.utils.Reference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
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

    public void setup(FMLCommonSetupEvent e) {

        MinecraftForge.EVENT_BUS.register(new SleepingCrouchEvent());
        Networking.registerMessaged();

        if (config != null) {
            Config.teleportCooldown.get();
            Config.bedBlockNames.get();
        } else {
            getLogger().error("Erreur lors du chargement de la configuration !");
        }
    }

    public void clientSetup(FMLClientSetupEvent e) {
        // Setup client-side specific features, if any
    }
        @SubscribeEvent
        public static void onServerStarting(FMLServerStartingEvent event) {
            // Code to execute when the server is starting
        }

        @SubscribeEvent
        public static void onServerStopping(FMLServerStoppingEvent event) {
            // Code to execute when the server is stopping
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