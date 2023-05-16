package fr.nokane.sleeping;

import fr.nokane.sleeping.config.Config;
import fr.nokane.sleeping.event.PlayerSleepingEvent;
import fr.nokane.sleeping.gui.ZoneGui;
import fr.nokane.sleeping.utils.Reference;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



@Mod(Reference.MOD_ID)
public class SleepingTP {

    public static final Logger logger = LogManager.getLogger();
    private final ForgeConfigSpec config;

    // Constructeur de la classe
    public SleepingTP() {
        // On enregistre les événements pour le setup et le client setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        // On initialise la configuration
        Config.setup();
        // On récupère la configuration
        config = Config.CONFIG;
    }

    // Setup du mod
    public void setup(FMLCommonSetupEvent e) {
        // On enregistre l'événement de sommeil des joueurs
        MinecraftForge.EVENT_BUS.register(new PlayerSleepingEvent());
        MinecraftForge.EVENT_BUS.register(new ZoneGui());
        // On enregistre la configuration
        MinecraftForge.EVENT_BUS.register(config);

        // On récupère les valeurs des paramètres de la configuration
        if (config != null) {
            Config.enableTeleport.get();
            Config.teleportCooldown.get();
            Config.bedBlockNames.get();
        } else {
            getLogger().error("Erreur lors du chargement de la configuration !");
        }
    }

    // Client setup
    public void clientSetup(FMLClientSetupEvent e) {
        // Cette méthode peut être utilisée pour le client-side setup, par exemple pour enregistrer des écrans de titre ou des HUDs
    }

    // Méthode pour sauvegarder la configuration
    public static void saveConfig() {
        if (Config.CONFIG != null) {
            try {
                Config.CONFIG.save();
            } catch (Exception e) {
                getLogger().error("Une erreur est survenue lors de la sauvegarde de la configuration !", e);
            }
        }
    }

    // Méthode pour récupérer le logger de la classe
    public static Logger getLogger() {
        return logger;
    }
}