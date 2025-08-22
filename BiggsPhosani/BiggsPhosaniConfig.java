package net.runelite.client.live.inDevelopment.biggs.BiggsPhosani;

import lombok.Getter;
import net.runelite.client.config.*;

import java.awt.event.KeyEvent;

@ConfigGroup(BiggsPhosaniPlugin.GROUP_NAME)
public interface BiggsPhosaniConfig extends Config {
    String GROUP_NAME = "phosani";

    @ConfigSection(
            name = "<html><font color=#3ebede>General Settings</font></html>",
            description = "General configurations for BiggsPhosani",
            position = 1,
            closedByDefault = true
    )
    String generalConfig = "generalConfig";

    @ConfigSection(
            name = "<html><font color=#3ebede>Gear Configuration</font></html>",
            description = "Gear and consumable configurations for BiggsPhosani",
            position = 2,
            closedByDefault = true
    )
    String gearConsumablesConfig = "gearConsumablesConfig";

    @ConfigSection(
            name = "<html><font color=#3ebede>Restocking and Healing</font></html>",
            description = "Restocking and healing configurations for BiggsPhosani",
            position = 3,
            closedByDefault = true
    )
    String restockHealConfig = "restockHealConfig";

    @ConfigSection(
            name = "<html><font color=#3ebede>Control Settings</font></html>",
            description = "Control configurations for Biggs Phosani",
            position = 0,
            closedByDefault = false
    )
    String controlConfig = "controlConfig";

    // General Settings
    @ConfigItem(
            keyName = "slepeTablet",
            name = "Slepe Teleport?",
            description = "Enable Slepe teleport.",
            position = 0,
            section = generalConfig
    )
    default boolean useSlepe() {
        return true;
    }

    @ConfigItem(
            keyName = "useRunePouch",
            name = "Use Rune Pouch?",
            description = "Enable Rune Pouch support.",
            position = 1,
            section = generalConfig
    )
    default boolean useRunePouch() {
        return true;
    }

    @ConfigItem(
            keyName = "useThralls",
            name = "Use Thralls?",
            description = "Enable Thralls for combat.",
            position = 2,
            section = generalConfig
    )
    default boolean useThralls() {
        return false;
    }

    @ConfigItem(
            keyName = "prayerFlick",
            name = "Prayer Flick?",
            description = "Enable prayer flicking during combat.",
            position = 3,
            section = generalConfig
    )
    default boolean prayerFlick() {
        return true;
    }

    @ConfigItem(
            keyName = "stam",
            name = "Stamina @ Bank?",
            description = "Use stamina at bank when needed.",
            position = 4,
            section = controlConfig
    )
    default boolean useStamina() {
        return false;
    }

    // Gear and Consumables
    @ConfigItem(
            keyName = "copyGear",
            name = "Copy Gear?",
            description = "Plugin must be turned on.",
            position = -1,
            section = gearConsumablesConfig
    )
    default boolean copyGear() {
        return false;
    }

    @ConfigItem(
            keyName = "specFaggotParasiteCUNT",
            name = "Spec Parasite?",
            description = "MUST HAVE YOUR PARASITE GEAR SET WITH YOUR SPEC WEAPON, NEEDS A 25% MINIMUM SPEC OPTION WEAPON..",
            position = -1,
            section = gearConsumablesConfig
    )
    default boolean specFaggot() {
        return false;
    }



    @ConfigItem(
            keyName = "mainGear",
            name = "Main Gear",
            description = "Gear for standard combat phases.",
            position = 1,
            section = gearConsumablesConfig
    )
    default String mainGear() {
        return "";
    }

    @ConfigItem(
            keyName = "parasiteGear",
            name = "Parasite Gear",
            description = "Gear for parasite phases.",
            position = 2,
            section = gearConsumablesConfig
    )
    default String parasiteGear() {
        return "";
    }

    @ConfigItem(
            keyName = "huskGear",
            name = "Husk Gear",
            description = "Gear for husk phases.",
            position = 3,
            section = gearConsumablesConfig
    )
    default String huskGear() {
        return "";
    }

    @ConfigItem(
            keyName = "dwGear",
            name = "SWalker Gear",
            description = "Sleepwalker Gear (MAKE SURE YOUR ON CRUSH).",
            position = 4,
            section = gearConsumablesConfig
    )
    default String sleepwalkerGear() {
        return "";
    }

    @ConfigItem(
            keyName = "specGear",
            name = "Spec Gear",
            description = "Spec gear.",
            position = 5,
            section = gearConsumablesConfig
    )
    default String specGear() {
        return "";
    }

    @Range(max = 100)
    @ConfigItem(
            keyName = "specPercent",
            name = "Spec Percent",
            description = "Spec percent.",
            position = 6,
            section = gearConsumablesConfig
    )
    default int specPercent() {
        return 50;
    }

    @ConfigItem(
            keyName = "totemGear",
            name = "Totem Gear",
            description = "Gear for totem (CHARGED STAFFS ONLY).",
            position = 5,
            section = gearConsumablesConfig
    )
    default String totemGear() {
        return "";
    }


    // Restocking and Healing
// Restocking and Healing
    @ConfigItem(
            keyName = "healAtPOH",
            name = "Heal at POH?",
            description = "Enable healing at POH.",
            position = -1,
            section = restockHealConfig
    )
    default boolean healAtPOH() {
        return true;
    }

    @ConfigItem(
            keyName = "healAtHP",
            name = "Eat Threshold",
            description = "Minimum HP to heal.",
            position = 0,
            section = restockHealConfig
    )
    default int hpAmount() {
        return 55;
    }
    @ConfigItem(
            keyName = "pa",
            name = "Pray Threshold",
            description = "Minimum prayer to heal.",
            position = 1,
            section = restockHealConfig
    )
    default int prayerAmount() {
        return 25;
    }

    @ConfigItem(
            keyName = "foodType",
            name = "Food Type",
            description = "Type of food to bring.",
            position = 2,
            section = restockHealConfig
    )
    default String foodType() {
        return "Anglerfish";
    }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Food Amount",
            description = "Number of food to bring.",
            position = 3,
            section = restockHealConfig
    )
    default int foodAmount() {
        return 20;
    }
@Range(min = 1, max = 25)
    @ConfigItem(
            keyName = "minFoodToRestock",
            name = "Min Food to Restock",
            description = "Minimum food before restocking.",
            position = 4,
            section = restockHealConfig
    )
    default int minFoodToRestock() {
        return 5;
    }

    @ConfigItem(
            keyName = "boostType",
            name = "Boost Potion Type",
            description = "Type of boost potion to bring.",
            position = 5,
            section = restockHealConfig
    )
    default String boostType() {
        return "Super combat potion";
    }

    @ConfigItem(
            keyName = "boostPotionAmount",
            name = "Boost Potion Amount",
            description = "Number of boost potions to bring.",
            position = 6,
            section = restockHealConfig
    )
    default int boostPotionAmount() {
        return 2;
    }
    @Range(min = 1, max = 15)
    @ConfigItem(
            keyName = "bw",
            name = "Boost when?",
            description = "How many levels away from our real Strength level should we boost?",
            position = 1,
            section = restockHealConfig
    )
    default int boostWhen() {
        return 11;
    }
@Range(min = 1, max = 25)
    @ConfigItem(
            keyName = "minBoostToRestock",
            name = "Min Boost to Restock",
            description = "Minimum boost potions before restocking.",
            position = 7,
            section = restockHealConfig
    )
    default int minBoostToRestock() {
        return 1;
    }

    @ConfigItem(
            keyName = "cureType",
            name = "Cure Potion Type",
            description = "Type of cure potion to bring.",
            position = 8,
            section = restockHealConfig
    )
    default String cureType() {
        return "Sanfew serum";
    }

    @ConfigItem(
            keyName = "curePotionAmount",
            name = "Cure Potion Amount",
            description = "Number of cure potions to bring.",
            position = 9,
            section = restockHealConfig
    )
    default int curePotionAmount() {
        return 2;
    }
@Range(min = 1, max = 15)
    @ConfigItem(
            keyName = "minCureToRestock",
            name = "Min Cure to Restock",
            description = "Minimum cure potions before restocking.",
            position = 10,
            section = restockHealConfig
    )
    default int minCureToRestock() {
        return 1;
    }

    @ConfigItem(
            keyName = "prayerRestoreType",
            name = "Prayer Restore Type",
            description = "Type of prayer restore potion to bring.",
            position = 11,
            section = restockHealConfig
    )
    default String prayerRestoreType() {
        return "Prayer potion";
    }

    @ConfigItem(
            keyName = "prayerPotionAmount",
            name = "Prayer Restore Amount",
            description = "Number of prayer restore potions to bring.",
            position = 12,
            section = restockHealConfig
    )
    default int prayerPotionAmount() {
        return 4;
    }
@Range(min = 1, max = 15)
    @ConfigItem(
            keyName = "minPrayerRestoreToRestock",
            name = "Min Prayer Restore to Restock",
            description = "Minimum prayer restore potions before restocking.",
            position = 13,
            section = restockHealConfig
    )
    default int minPrayerRestoreToRestock() {
        return 2;
    }


    // Control Settings
    @ConfigItem(
            keyName = "startStopHotkey",
            name = "Start/Stop",
            description = "Hotkey for activating or deactivating the plugin.",
            position = 0,
            section = controlConfig
    )
    default Keybind toggle() {
        return new Keybind(KeyEvent.VK_F5, 0);
    }

    @ConfigItem(
            keyName = "po",
            name = "BP Spec Sleepwalker?",
            description = "Plugin must be turned on.",
            position = 47,
            section = controlConfig
    )
    default boolean BPSleepwalker() {
        return false;
    }


    @ConfigItem(
            keyName = "db",
            name = "Debug overlay",
            description = "Plugin must be turned on.",
            position = 48,
            section = controlConfig
    )
    default boolean debug() {
        return false;
    }

    @ConfigItem(
            keyName = "po",
            name = "Phase overlay",
            description = "Plugin must be turned on.",
            position = 49,
            section = controlConfig
    )
    default boolean phaseOverlay() {
        return false;
    }

    @ConfigItem(
            keyName = "po",
            name = "Status overlay",
            description = "Plugin must be turned on.",
            position = 50,
            section = controlConfig
    )
    default boolean statusOverlay() {
        return false;
    }
}
