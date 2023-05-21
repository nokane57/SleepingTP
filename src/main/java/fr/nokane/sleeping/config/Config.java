package fr.nokane.sleeping.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Arrays;
import java.util.List;

public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;
    public static ForgeConfigSpec.IntValue teleportCooldown;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> bedBlockNames;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> zones;
    public static ForgeConfigSpec.IntValue commandTeleportCooldownTimer;
    public static ForgeConfigSpec.IntValue PVPCombatTimer;
    private static long lastTeleportTime = 0;

    public static void setup() {
        BUILDER.comment("Sleeping TP configuration").push("générale");

        teleportCooldown = BUILDER.comment("Temps de recharge en secondes pour la téléportation du joueur en dormant")
                .defineInRange("teleportCooldown", 60, 1, Integer.MAX_VALUE);

        bedBlockNames = BUILDER.comment("Liste des noms de bloc de lit valides pour la téléportation du joueur pendant son sommeil")
                .defineList("bedBlockNames", getDefaultBedBlockNames(), Config::isValidBedBlockName);

        zones = BUILDER.comment("Liste des nom des zones").defineList("zones", getDefaultZones(), Config::isValidedZones);

        commandTeleportCooldownTimer = BUILDER.comment("Délai en minutes entre chaque utilisation de la commande returnToBed")
                .defineInRange("teleportCooldownTimer", 5, 1, Integer.MAX_VALUE);

        PVPCombatTimer = BUILDER.comment("Délai en seconde pour ne plus etre en combat")
                .defineInRange("PvpCombatTimer", 10, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        CONFIG = BUILDER.build();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG);
    }

    private static List<String> getDefaultBedBlockNames() {
        return Arrays.asList(
                "minecraft:white_bed",
                "minecraft:orange_bed",
                "minecraft:magenta_bed",
                "minecraft:light_blue_bed",
                "minecraft:yellow_bed",
                "minecraft:lime_bed",
                "minecraft:pink_bed",
                "minecraft:gray_bed",
                "minecraft:light_gray_bed",
                "minecraft:cyan_bed",
                "minecraft:purple_bed",
                "minecraft:blue_bed",
                "minecraft:brown_bed",
                "minecraft:green_bed",
                "minecraft:red_bed",
                "minecraft:black_bed"
        );
    }

    private static List<String> getDefaultZones() {
        return Arrays.asList("zone1:100,100,100");
    }

    private static boolean isValidBedBlockName(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        String bedBlockName = (String) value;
        return bedBlockName.contains(":");
    }

    private static boolean isValidedZones(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        String zoneEntry = (String) value;
        String[] parts = zoneEntry.split(":");
        if (parts.length != 2) {
            return false;
        }
        String name = parts[0];
        String[] coordinates = parts[1].split(",");
        if (coordinates.length != 3) {
            return false;
        }
        try {
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int z = Integer.parseInt(coordinates[2]);

            // Add additional validation for the coordinates
            if (x < 0 || y < 0 || z < 0) {
                return false;
            }
            // Add more validation rules for the coordinates if needed
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static int getTeleportCooldown() {
        return teleportCooldown.get();
    }

    public static long getLastTeleportTime() {
        return lastTeleportTime;
    }

    public static void setLastTeleportTime(long lastTeleportTime) {
        Config.lastTeleportTime = lastTeleportTime;
    }

    public static void setTeleportCooldown(int value) {
        teleportCooldown.set(value);
    }

    private static boolean hasTeleported = false;

    public static boolean hasTeleported() {
        return hasTeleported;
    }

    public static void setHasTeleported(boolean value) {
        hasTeleported = value;
    }

    public static int getPvPCombatTimer() {
        return PVPCombatTimer.get();
    }
}