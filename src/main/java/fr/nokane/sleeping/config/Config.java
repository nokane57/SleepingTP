package fr.nokane.sleeping.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Arrays;
import java.util.List;

public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.IntValue pvpDetectionTimer;
    public static ForgeConfigSpec.BooleanValue enableTeleport;
    public static ForgeConfigSpec.IntValue teleportCooldown;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> bedBlockNames;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> zones;
    private static long lastTeleportTime = 0;

    public static void setup() {
        BUILDER.comment("Sleeping TP configuration").push("general");

        enableTeleport = BUILDER.comment("Enable player teleportation when sleeping")
                .define("enableTeleport", true);

        teleportCooldown = BUILDER.comment("Cooldown time in seconds for player teleportation when sleeping")
                .defineInRange("teleportCooldown", 60, 1, Integer.MAX_VALUE);

        bedBlockNames = BUILDER.comment("List of valid bed block names for player teleportation when sleeping")
                .defineList("bedBlockNames", getDefaultBedBlockNames(), Config::isValidBedBlockName);

        pvpDetectionTimer = BUILDER.comment("Duration in seconds for PVP detection timer")
                .defineInRange("pvpDetectionTimer", 10, 1, Integer.MAX_VALUE); // Nouveau paramètre


        zones = BUILDER.comment("test").defineList("zones", getDefaultZones(), Config::isValidedZones);

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
        return Arrays.asList("zone1:100,100,100", "zone2:100,100,100");
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

    public static boolean isTeleportEnabled() {
        return enableTeleport.get();
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

    public static int getPvpDetectionTimer() {return pvpDetectionTimer.get();}
}