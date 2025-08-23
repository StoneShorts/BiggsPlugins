package net.runelite.client.live.inDevelopment.biggs.Executioner;

import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.*;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.*;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.BraceletsType;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala.AmmoSelect;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala.BoltAmmoSelect;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala.RevChoice;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala.SlayerMode;
import net.runelite.client.config.*;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.TeleportOptions;

/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */
/**
 * @author BIGGS
 * discord: @biggs.exe
 */

@ConfigGroup("exe")
public interface ExecutionerConfig extends Config {

/*    @ConfigSection(
            name = "<html><font color=#42e6f5>Add-ons</font></html>",
            description = "",
            position = -1,
            closedByDefault = false
    )
    String test = "Add-ons";*/

    @ConfigSection(
            name = "<html><font color=#42e6f5>Executioner ALPHA V0.4</font></html>",
            description = "Main settings and configurations for the Wildy Slayer plugin.",
            position = 0,
            closedByDefault = false
    )
    String titleConfig = "titleConfig";

    @ConfigSection(
            name = "<html><font color=#42e6f5>General Settings</font></html>",
            description = "Basic plugin settings.",
            position = 1,
            closedByDefault = false
    )
    String generalSettings = "generalSettings";

    @ConfigSection(
            name = "<html><font color=#42e6f5>Tick Delays</font></html>",
            description = "Settings related to game tick delays.",
            position = 2,
            closedByDefault = true
    )
    String tickDelays = "tickDelays";

    @ConfigSection(
            name = "<html><font color=#42e6f5>Combat Settings</font></html>",
            description = "Configure gear and combat-related settings.",
            position = 3,
            closedByDefault = true
    )
    String combatSection = "combatSection";

    @ConfigSection(
            name = "<html><font color=#42e6f5>Gear Loadouts</font></html>",
            description = "Configure your gear.",
            position = 4,
            closedByDefault = true
    )
    String gearSec = "gearSection";

    @ConfigSection(
            name = "<html><font color=#42e6f5>Food and Potions</font></html>",
            description = "Settings for managing food and potion use.",
            position = 5,
            closedByDefault = true
    )
    String foodAndPotions = "foodAndPotions";

    @ConfigSection(
            name = "<html><font color=#42e6f5>Looting Config</font></html>",
            description = "Loot configuration.",
            position = 6,
            closedByDefault = false
    )
    String lc = "lootingConfig";

    @ConfigSection(
            name = "<html><font color=#eb0017>Monster Config</font></html>",
            description = "Monster Configuration.",
            position = 7,
            closedByDefault = false
    )
    String mC = "monsterConfig";
    /*    @ConfigItem(
                keyName = "doKBD",
                name = "KBD?",
                description = "Does KBD instead of black dragons on task.",
                position = 3,
                section = test
        )
        default boolean doKBD() {
            return false;
        }*/
    @ConfigItem(
            keyName = "debug",
            name = "debug",
            description = "",
            position = -1,
            section = generalSettings
    )
    default boolean debugMode() {
        return true;
    }


    @ConfigItem(
            keyName = "slayerMode",
            name = "Slayer Mode",
            description = "",
            position = 0,
            section = generalSettings
    )
    default SlayerMode slayerMode() {
        return SlayerMode.KRISTILIA;
    }
    /*    @ConfigItem(
                keyName = "meleeGear",
                name = "Artio Gear",
                description = "Configure the melee gear setup, separated by commas without spaces.",
                position = 3,
                section = test
        )
        default String artioStringGear() {
            return "";
        }*/
    @ConfigItem(
            keyName = "toggle",
            name = "Toggle Plugin",
            description = "Hotkey to toggle the plugin.",
            position = 0,
            section = generalSettings
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }
    @ConfigItem(
            keyName = "corn",
            name = "Cannon (or)?",
            description = "Enable if using a cannon with the ornament kit.",
            position = 1,
            section = generalSettings
    )
    default boolean useCannonDecoration() {
        return false;
    }
    @ConfigItem(
            keyName = "escapeType",
            name = "Escape Teleport",
            description = "The item to escape/leave tasks with.",
            position = 2,
            section = generalSettings
    )
    default TeleportOptions TELEPORT_OPTIONS() {
        return TeleportOptions.AMULET_OF_GLORY;
    }
    @ConfigItem(
            keyName = "sendChat",
            name = "Chat on level?",
            description = "Will send a chat upon level up from the below messages..",
            position = 3,
            section = generalSettings
    )
    default boolean useChatOnLevel() {
        return false;
    }
    @ConfigItem(
            keyName = "chatMessage",
            name = "Messages to send?",
            description = "Messages to send seperated with ','. example: omg i love this game,nice,ez,fast lvls lol",
            position = 4,
            section = generalSettings
    )
    default String messageToSend() {
        return "ez,lol yes sir!,lets go";
    }
    @Range(min = 1, max = 10)
    @ConfigItem(
            keyName = "bAmount",
            name = "Bracelet Amount",
            description = "",
            position = 5,
            section = generalSettings
    )
    default int braceletAmount() {
        return 2;
    }

    @ConfigItem(
            keyName = "minLoot",
            name = "Minimum Loot Value",
            description = "Minimum value to loot an item.",
            position = 0,
            section = lc
    )
    default int minLoot() {
        return 15;
    }
    @ConfigItem(
            keyName = "lbag",
            name = "Looting bag?",
            description = "Enable to bring and use a Looting Bag.",
            position = 1,
            section = lc
    )
    default boolean useLootingBagGangstaAssMothaFuckingLootingBagBitchYouAintOnShitDawgISwearToGawdFrFrHomieIfYouReadThisYouProbablyHaveDowNSyndromexDxDMoron() {
        return false;
    }
    @ConfigItem(
            keyName = "enableTickDelay",
            name = "Enable Tick Delays",
            description = "Toggle tick delays for more natural plugin behavior.",
            position = 0,
            section = tickDelays
    )
    default boolean tickDelay() {
        return true;
    }

    @Range(min = 1, max = 10)
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Minimum Tick Delay",
            description = "Minimum game tick delay.",
            position = 1,
            section = tickDelays
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(min = 1, max = 10)
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Maximum Tick Delay",
            description = "Maximum game tick delay.",
            position = 2,
            section = tickDelays
    )
    default int tickDelayMax() {
        return 3;
    }
    @Range(min = 1, max = 2500)
    @ConfigItem(
            keyName = "cbMax",
            name = "Cannonball Amount",
            description = "",
            position = -4,
            section = gearSec
    )
    default int cbAmount() {
        return 3;
    }
    @Range(min = 1, max = 2500)
    @ConfigItem(
            keyName = "arrowAmount",
            name = "Arrow Amount",
            description = "",
            position = -3,
            section = gearSec
    )
    default int arrowAmount() {
        return 0;
    }
    @ConfigItem(
            keyName = "ammoType",
            name = "Ammo",
            description = "**REQUIRED** FOR BOLTS / ARROWS!! CHOOSE **NONE** IF USING BOWFA ETC.",
            position = -2,
            section = gearSec
    )
    default AmmoSelect AmmoType() {
        return AmmoSelect.ADAMANT_ARROW;
    }
    @ConfigItem(
            keyName = "bammoType",
            name = "Bolt Ammo",
            description = "**REQUIRED** FOR BOLTS / ARROWS!! CHOOSE **NONE** IF USING BOWFA ETC.",
            position = -3,
            section = gearSec
    )
    default BoltAmmoSelect BoltAmmoType() {
        return BoltAmmoSelect.DIAMOND_DRAGON_BOLTS_E;
    }
    @ConfigItem(
            keyName = "copyGear",
            name = "Copy Gear",
            description = "Option to copy gear from another source.",
            position = 0,
            section = gearSec
    )
    default boolean copyGear() {
        return false;
    }

    // Gear configurations for Melee, Ranged, and Mage setups
    @ConfigItem(
            keyName = "meleeGear",
            name = "Melee Gear",
            description = "Configure the melee gear setup, separated by commas without spaces.",
            position = 1,
            section = gearSec
    )
    default String meleeGear() {
        return "";
    }

    @ConfigItem(
            keyName = "meleeBoostSelect",
            name = "Melee Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: super combat potion(",
            position = 2,
            section = gearSec
    )
    default String meleeBoostSelect() {
        return "super combat potion(";
    }

    @ConfigItem(
            keyName = "rangedGear",
            name = "Ranged Gear",
            description = "Configure the ranged gear setup, separated by commas without spaces.",
            position = 3,
            section = gearSec
    )
    default String rangedGear() {
        return "";
    }

    @ConfigItem(
            keyName = "rangedBoostSelect",
            name = "Ranged Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: ranging potion(",
            position = 4,
            section = gearSec
    )
    default String rangedBoostSelect() {
        return "ranging potion(";
    }

    @ConfigItem(
            keyName = "mageGear",
            name = "Mage Gear",
            description = "Configure the mage gear setup, separated by commas without spaces.",
            position = 5,
            section = gearSec
    )
    default String mageGear() {
        return "";
    }

    @ConfigItem(
            keyName = "mageBoostSelect",
            name = "Mage Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: magic potion(",
            position = 6,
            section = gearSec
    )
    default String mageBoostSelect() {
        return "magic potion(";
    }


    @ConfigItem(
            keyName = "dragonGear",
            name = "Dragon Gear",
            description = "Configure the Dragon gear setup, separated by commas without spaces.",
            position = 7,
            section = gearSec
    )
    default String dragonGear() {
        return "";
    }

    @ConfigItem(
            keyName = "dragBoostSelect",
            name = "Dragon Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: super combat potion(",
            position = 8,
            section = gearSec
    )
    default String dragBoostSelect() {
        return "super combat potion(";
    }


    @ConfigItem(
            keyName = "altrangedGear",
            name = "Alt Ranged Gear",
            description = "Configure the ALTERNATIVE ranged gear setup, separated by commas without spaces.",
            position = 9,
            section = gearSec
    )
    default String rangedGear2() {
        return "";
    }

    @ConfigItem(
            keyName = "ranged2BoostSelect",
            name = "Ranged2 Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: ranging potion(",
            position = 10,
            section = gearSec
    )
    default String ranged2BoostSelect() {
        return "ranging potion(";
    }

    @ConfigItem(
            keyName = "customGear",
            name = "Custom Gear",
            description = "Configure the CUSTOM gear setup, separated by commas without spaces.",
            position = 11,
            section = gearSec
    )
    default String customGear() {
        return "";
    }

    @ConfigItem(
            keyName = "customBoostSelect",
            name = "Custom Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: ranging potion(",
            position = 12,
            section = gearSec
    )
    default String customBoostSelect() {
        return "ranging potion(";
    }



    @ConfigItem(
            keyName = "customGear2",
            name = "Custom Gear 2",
            description = "Configure the CUSTOM gear 2 setup, separated by commas without spaces.",
            position = 13,
            section = gearSec
    )
    default String customGear2() {
        return "";
    }

    @ConfigItem(
            keyName = "custom2BoostSelect",
            name = "Custom2 Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: ranging potion(",
            position = 14,
            section = gearSec
    )
    default String custom2BoostSelect() {
        return "ranging potion(";
    }

    @ConfigItem(
            keyName = "barrageGear",
            name = "Barrage Gear",
            description = "Configure the Barrage Gear setup, separated by commas without spaces.",
            position = 15,
            section = gearSec
    )
    default String barrageGear() {
        return "";
    }

    @ConfigItem(
            keyName = "barrageBoostSelect",
            name = "Barrage Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: magic potion(",
            position = 16,
            section = gearSec
    )
    default String barrageBoostSelect() {
        return "magic potion(";
    }

    @ConfigItem(
            keyName = "spellGear",
            name = "Spellcasting Gear",
            description = "Configure the Spellcasting Gear setup, separated by commas without spaces.",
            position = 17,
            section = gearSec
    )
    default String spellGear() {
        return "";
    }

    @ConfigItem(
            keyName = "spellBoostSelect",
            name = "Spell Gear Boost",
            description = "Enter the name of the boost potion you'd like to use ex: magic potion(",
            position = 18,
            section = gearSec
    )
    default String spellBoostSelect() {
        return "magic potion(";
    }

    @ConfigItem(
            keyName = "potionamt",
            name = "Boost Amount",
            description = "How many boost pots",
            position = 19,
            section = gearSec
    )
    default int potionAmountToWithdraw() {
        return 1;
    }

    @ConfigItem(
            keyName = "spellSelect",
            name = "Spell Selection",
            description = "What spell should we use with Spellcasting Gear?",
            position = -1,
            section = combatSection
    )
    default Spells chooseSpell() {
        return Spells.NONE;
    }

    @ConfigItem(
            keyName = "flickPray",
            name = "Flick Prayer",
            description = "Toggle to flick prayers during combat.",
            position = 0,
            section = combatSection
    )
    default boolean flickPray() {
        return false;
    }

    @ConfigItem(
            keyName = "magicOffensive",
            name = "Magic Offensive Prayer",
            description = "Select your magic offensive prayer.",
            position = 1,
            section = combatSection
    )
    default OffensivePrayerTypeMagic magicOffensivePrayer() {
        return OffensivePrayerTypeMagic.NONE;
    }

    @ConfigItem(
            keyName = "rangeOffensive",
            name = "Ranged Offensive Prayer",
            description = "Select your ranged offensive prayer.",
            position = 2,
            section = combatSection
    )
    default OffensivePrayerTypeRanged rangedOffensivePrayer() {
        return OffensivePrayerTypeRanged.NONE;
    }

    @ConfigItem(
            keyName = "meleeOffensive",
            name = "Melee Offensive Prayer",
            description = "Select your melee offensive prayer.",
            position = 3,
            section = combatSection
    )
    default OffensivePrayerTypeMelee meleeOffensivePrayer() {
        return OffensivePrayerTypeMelee.NONE;
    }

    @ConfigItem(
            keyName = "useSpecial",
            name = "Use Special Attack",
            description = "Enable to use special attacks when conditions are met.",
            position = 4,
            section = combatSection
    )
    default boolean useSpecial() {
        return false;
    }

    @Range(min = 10, max = 100)
    @ConfigItem(
            keyName = "specPercentage",
            name = "Special Attack Threshold",
            description = "Percentage of special attack energy at which to use special attacks.",
            position = 5,
            section = combatSection
    )
    default int specPercentage() {
        return 50;
    }

    @ConfigItem(
            keyName = "specWep",
            name = "Spec Weapon",
            description = "Enter your spec weapon if using one, CASE SENSITIVE.",
            position = 6,
            section = combatSection
    )
    default String specWepName() {
        return "Abyssal dagger";
    }

    @ConfigItem(
            keyName = "foodItemName",
            name = "Food Item",
            description = "Specify the food item name, case sensitive.",
            position = 0,
            section = foodAndPotions
    )
    default String foodItemName() {
        return "Karambwan";
    }

    @ConfigItem(
            keyName = "healthLowAmount",
            name = "Eat at Health",
            description = "Health level at which to eat food.",
            position = 1,
            section = foodAndPotions
    )
    default int healthLowAmount() {
        return 10;
    }
    @ConfigItem(
            keyName = "prayerPotType",
            name = "Prayer Restore Type",
            description = "prayer restore type.",
            position = 2,
            section = foodAndPotions
    )
    default PrayerOptions PRAYER_OPTIONS() {
        return PrayerOptions.PRAYER_POTION;
    }
    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "prayLowAmount",
            name = "Restore Pray Amount",
            description = "Pray level to drink a pot.",
            position = 3,
            section = foodAndPotions
    )
    default int prayerLow() {
        return 10;
    }
    @Range(
            min = 1,
            max = 25
    )
    @ConfigItem(
            keyName = "prayerPotAmount",
            name = "Prayer Potion Amount",
            description = "Amount of prayer potions to withdraw.",
            position = 3,
            section = foodAndPotions
    )
    default int prayerPotAmount() {
        return 0;
    }
    @ConfigItem(
            keyName = "bPotAmount",
            name = "Boost Potion Amount",
            description = "Amount of boost potions to withdraw.",
            position = 4,
            section = foodAndPotions
    )
    default int boostPotAmount() {
        return 0;
    }
    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            keyName = "whenBPot",
            name = "Melee Boost Target",
            description = "How many from our actual level should we boost? EX: if set to 5, and our ranged is 80, it'll repot at 85.",
            position = 5,
            section = foodAndPotions
    )
    default int whenToBoost() {
        return 7;
    }
    @ConfigItem(
            keyName = "whenRPot",
            name = "Range Boost Target",
            description = "How many from our actual level should we boost? EX: if set to 5, and our ranged is 80, it'll repot at 85.",
            position = 6,
            section = foodAndPotions
    )
    default int whenToBoostR() {
        return 4;
    }
    @ConfigItem(
            keyName = "whenMPot",
            name = "Mage Boost Target",
            description = "How many from our actual level should we boost? EX: if set to 5, and our ranged is 80, it'll repot at 85.",
            position = 7,
            section = foodAndPotions
    )
    default int whenToBoostM() {
        return 2;
    }

    @ConfigItem(
            keyName = "instructions",
            name = "Plugin Instructions",
            description = "Instructions for using the plugin.",
            position = -1,
            section = titleConfig
    )
    default String instructions() {
        return "Please start the plugin at EDGEVILLE bank.\n\n" +
                "Ensure you have all required items for the plugin, in your bank.\n\n" +
                "Make sure you have the correct food and potions in your bank.\n\n" +
                "If using bracelets, make sure your hands gear slot doesnt have anything.\n\n" +
                "MAKE SURE ALL JEWLERY IS MAX CHARGED EX: (amulet of glory(6)!!!.\n\n" +
                "Start the plugin and begin executing monsters.";
    }

    /** ALL MONSTER CONFIG BELOW!!!!!!!!!!!!!!!!!!!!!!!!!! **/
    @ConfigSection(
            name = "<html><font color=#7df5bd>Abyssal Demons</font></html>",
            description = "Settings for Abyssal Demons",
            position = 10,
            closedByDefault = true
    )
    String abyssalDemonsSection = "abyssalDemonsSection";
    @ConfigItem(
            keyName = "abyssalDemonsGear",
            name = "Gear Selection",
            description = "Select gear for Abyssal Demons",
            position = 0,
            section = abyssalDemonsSection
    )
    default AttackType abyssalDemonsGear() {
        return AttackType.MELEE;
    }
    @ConfigItem(
            keyName = "abyssalDemonsPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Abyssal Demons",
            position = 1,
            section = abyssalDemonsSection
    )
    default DefensivePrayerType abyssalDemonsPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "abyssalDemonsOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Abyssal Demons",
            position = 2,
            section = abyssalDemonsSection
    )
    default OffensivePrayerType abyssalDemonsOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonAbyssalDemons",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Abyssal Demons.",
            position = 3,
            section = abyssalDemonsSection
    )
    default boolean useCannonAbyssalDemons() {
        return false;
    }
    @ConfigItem(
            keyName = "abyssalBraceletType",
            name = "Bracelet Type",
            description = "Select bracelet for Abyssal Demons",
            position = 4,
            section = abyssalDemonsSection
    )
    default BraceletsType abyssalDemonsBracelet() {
        return BraceletsType.NONE;
    }


    @ConfigSection(
            name = "<html><font color=#7df5bd>Ankou</font></html>",
            description = "Settings for Ankou",
            position = 11,
            closedByDefault = true
    )
    String ankouSection = "ankouSection";
    @ConfigItem(
            keyName = "ankouGear",
            name = "Gear Selection",
            description = "Select gear for Ankou",
            position = 0,
            section = ankouSection
    )
    default AttackType ankouGear() {
        return AttackType.MELEE;
    }
    @ConfigItem(
            keyName = "ankouPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Ankou",
            position = 1,
            section = ankouSection
    )
    default DefensivePrayerType ankouPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }

    @ConfigItem(
            keyName = "ankouDemonsOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Ankous",
            position = 2,
            section = ankouSection
    )
    default OffensivePrayerType ankouOffensivePrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonAnkou",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Ankou.",
            position = 3,
            section = ankouSection
    )
    default boolean useCannonAnkou() {
        return false;
    }
    @ConfigItem(
            keyName = "ankBraceletType",
            name = "Bracelet Type",
            description = "Select bracelet for ankou",
            position = 4,
            section = ankouSection
    )
    default BraceletsType ankouBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Aviansie</font></html>",
            description = "Settings for Aviansie",
            position = 12,
            closedByDefault = true
    )
    String aviansieSection = "aviansieSection";

    @ConfigItem(
            keyName = "aviansieGear",
            name = "Gear Selection",
            description = "Select gear for Aviansie",
            position = 0,
            section = aviansieSection
    )
    default AttackType aviansieGear() {
        return AttackType.RANGED;
    }

    @ConfigItem(
            keyName = "aviansiePrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Aviansie",
            position = 1,
            section = aviansieSection
    )
    default DefensivePrayerType aviansiePrayer() {
        return DefensivePrayerType.PROTECT_MISSILES;
    }

    @ConfigItem(
            keyName = "abyssalDemonsOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Abyssal Demons",
            position = 2,
            section = aviansieSection
    )
    default OffensivePrayerType aviansieOffensivePrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonAviansie",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Aviansie.",
            position = 3,
            section = aviansieSection
    )
    default boolean useCannonAviansie() {
        return false;
    }
    @ConfigItem(
            keyName = "aviansieBraceletType",
            name = "Bracelet Type",
            description = "Select bracelet for Aviansie",
            position = 4,
            section = aviansieSection
    )
    default BraceletsType aviansieBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Bandits</font></html>",
            description = "Settings for Bandit",
            position = 13,
            closedByDefault = true
    )
    String banditSection = "banditSection";

    @ConfigItem(
            keyName = "banditGear",
            name = "Gear Selection",
            description = "Select gear for Bandit",
            position = 0,
            section = banditSection
    )
    default AttackType banditGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "banditPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Bandit",
            position = 1,
            section = banditSection
    )
    default DefensivePrayerType banditPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }

    @ConfigItem(
            keyName = "banditOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for bandit",
            position = 2,
            section = banditSection
    )
    default OffensivePrayerType banditOffensivePrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonBandits",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Bandits.",
            position = 3,
            section = banditSection
    )
    default boolean useCannonBandits() {
        return false;
    }

    @ConfigItem(
            keyName = "banditBraceletType",
            name = "Bracelet Type",
            description = "Select bracelet for bandits",
            position = 4,
            section = banditSection
    )
    default BraceletsType banditsBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Bears</font></html>",
            description = "Settings for Bear",
            position = 14,
            closedByDefault = true
    )
    String bearSection = "bearSection";

    @ConfigItem(
            keyName = "bearGear",
            name = "Gear Selection",
            description = "Select gear for Bear",
            position = 0,
            section = bearSection
    )
    default AttackType bearGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "bearPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Bear",
            position = 1,
            section = bearSection
    )
    default DefensivePrayerType bearPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }

    @ConfigItem(
            keyName = "bearOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Bears",
            position = 2,
            section = bearSection
    )
    default OffensivePrayerType bearOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonBears",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Bears.",
            position = 3,
            section = bearSection
    )
    default boolean useCannonBears() {
        return false;
    }
    @ConfigItem(
            keyName = "bearBraceletType",
            name = "Bracelet Type",
            description = "Select bracelet for bears",
            position = 4,
            section = bearSection
    )
    default BraceletsType bearsBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Black Demons</font></html>",
            description = "Settings for Black Demon",
            position = 15,
            closedByDefault = true
    )
    String blackDemonSection = "blackDemonSection";

    @ConfigItem(
            keyName = "blackDemonGear",
            name = "Gear Selection",
            description = "Select gear for Black Demon",
            position = 0,
            section = blackDemonSection
    )
    default AttackType blackDemonGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "blackDemonPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Black Demon",
            position = 1,
            section = blackDemonSection
    )
    default DefensivePrayerType blackDemonPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "bdemonoffprayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Black Demons",
            position = 2,
            section = blackDemonSection
    )
    default OffensivePrayerType blackDemonOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonBlackDemons",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Black Demons.",
            position = 3,
            section = blackDemonSection
    )
    default boolean useCannonBlackDemons() {
        return false;
    }

    @ConfigItem(
            keyName = "blackDemonsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Black Demons",
            position = 4,
            section = blackDemonSection
    )
    default BraceletsType blackDemonsBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Black Dragons</font></html>",
            description = "Settings for Black Dragon",
            position = 16,
            closedByDefault = true
    )
    String blackDragonSection = "blackDragonSection";

    @ConfigItem(
            keyName = "blackDragonGear",
            name = "Gear Selection",
            description = "Select gear for Black Dragon",
            position = 0,
            section = blackDragonSection
    )
    default AttackType blackDragonGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "blackDragonPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Black Dragon",
            position = 1,
            section = blackDragonSection
    )
    default DefensivePrayerType blackDragonPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }

    @ConfigItem(
            keyName = "blackDragonsOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Black Dragons",
            position = 2,
            section = blackDragonSection
    )
    default OffensivePrayerType blackDragonOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonBlackDragons",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Black Dragons.",
            position = 3,
            section = blackDragonSection
    )
    default boolean useCannonBlackDragons() {
        return false;
    }
    @ConfigItem(
            keyName = "blackDragonsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Black Dragons",
            position = 4,
            section = blackDragonSection
    )
    default BraceletsType blackDragonsBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Black Knights</font></html>",
            description = "Settings for Black Knight",
            position = 17,
            closedByDefault = true
    )
    String blackKnightSection = "blackKnightSection";

    @ConfigItem(
            keyName = "blackKnightGear",
            name = "Gear Selection",
            description = "Select gear for Black Knight",
            position = 0,
            section = blackKnightSection
    )
    default AttackType blackKnightGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "blackKnightPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Black Knight",
            position = 1,
            section = blackKnightSection
    )
    default DefensivePrayerType blackKnightPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }

    @ConfigItem(
            keyName = "blackKnightOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Black Knights",
            position = 2,
            section = blackKnightSection
    )
    default OffensivePrayerType blackKnightsOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonBlackKnights",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Black Knights.",
            position = 3,
            section = blackKnightSection
    )
    default boolean useCannonBlackKnights() {
        return false;
    }

    @ConfigItem(
            keyName = "blackKnightsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Black Knights",
            position = 4,
            section = blackKnightSection
    )
    default BraceletsType blackKnightsBracelet() {
        return BraceletsType.NONE;
    }


    @ConfigSection(
            name = "<html><font color=#7df5bd>Bloodvelds</font></html>",
            description = "Settings for Bloodveld",
            position = 18,
            closedByDefault = true
    )
    String bloodveldSection = "bloodveldSection";

    @ConfigItem(
            keyName = "bloodveldGear",
            name = "Gear Selection",
            description = "Select gear for Bloodveld",
            position = 0,
            section = bloodveldSection
    )
    default AttackType bloodveldGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "bloodveldPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Bloodveld",
            position = 1,
            section = bloodveldSection
    )
    default DefensivePrayerType bloodveldPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "bloodveldOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Bloodveld",
            position = 2,
            section = bloodveldSection
    )
    default OffensivePrayerType bloodveldOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonBloodveld",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Bloodveld.",
            position = 3,
            section = bloodveldSection
    )
    default boolean useCannonBloodveld() {
        return false;
    }

    @ConfigItem(
            keyName = "bloodveldBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Bloodveld",
            position = 4,
            section = bloodveldSection
    )
    default BraceletsType bloodveldBracelet() {
        return BraceletsType.NONE;
    }


    @ConfigSection(
            name = "<html><font color=#7df5bd>Chaos Druids</font></html>",
            description = "Settings for Chaos Druid",
            position = 19,
            closedByDefault = true
    )
    String chaosDruidSection = "chaosDruidSection";

    @ConfigItem(
            keyName = "chaosDruidGear",
            name = "Gear Selection",
            description = "Select gear for Chaos Druid",
            position = 0,
            section = chaosDruidSection
    )
    default AttackType chaosDruidGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "chaosDruidPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Chaos Druid",
            position = 1,
            section = chaosDruidSection
    )
    default DefensivePrayerType chaosDruidPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }

    @ConfigItem(
            keyName = "chaosDruidOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Chaos Druids",
            position = 2,
            section = chaosDruidSection
    )
    default OffensivePrayerType chaosDruidOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonChaosDruids",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Chaos Druids.",
            position = 3,
            section = chaosDruidSection
    )
    default boolean useCannonChaosDruids() {
        return false;
    }

    @ConfigItem(
            keyName = "chaosDruidsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Chaos Druids",
            position = 4,
            section = chaosDruidSection
    )
    default BraceletsType chaosDruidsBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Dark Warriors</font></html>",
            description = "Settings for Dark Warrior",
            position = 20,
            closedByDefault = true
    )
    String darkWarriorSection = "darkWarriorSection";

    @ConfigItem(
            keyName = "darkWarriorGear",
            name = "Gear Selection",
            description = "Select gear for Dark Warrior",
            position = 0,
            section = darkWarriorSection
    )
    default AttackType darkWarriorGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "darkWarriorPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Dark Warrior",
            position = 1,
            section = darkWarriorSection
    )
    default DefensivePrayerType darkWarriorPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "darkWarriorOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Dark Warriors",
            position = 2,
            section = darkWarriorSection
    )
    default OffensivePrayerType darkWarriorOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonDarkWarriors",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Dark Warriors.",
            position = 3,
            section = darkWarriorSection
    )
    default boolean useCannonDarkWarriors() {
        return false;
    }

    @ConfigItem(
            keyName = "darkWarriorsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Dark Warriors",
            position = 4,
            section = darkWarriorSection
    )
    default BraceletsType darkWarriorsBracelet() {
        return BraceletsType.NONE;
    }


    @ConfigSection(
            name = "<html><font color=#7df5bd>Dust Devils</font></html>",
            description = "Settings for Dust Devil",
            position = 21,
            closedByDefault = true
    )
    String dustDevilSection = "dustDevilSection";

    @ConfigItem(
            keyName = "dustDevilGear",
            name = "Gear Selection",
            description = "Select gear for Dust Devil",
            position = 0,
            section = dustDevilSection
    )
    default AttackType dustDevilGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "dustDevilPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Dust Devil",
            position = 1,
            section = dustDevilSection
    )
    default DefensivePrayerType dustDevilPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "dustDevilOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Dust Devils",
            position = 2,
            section = dustDevilSection
    )
    default OffensivePrayerType dustDevilOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonDustDevils",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Dust Devils.",
            position = 3,
            section = dustDevilSection
    )
    default boolean useCannonDustDevils() {
        return false;
    }

    @ConfigItem(
            keyName = "dustDevilsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Dust Devils",
            position = 4,
            section = dustDevilSection
    )
    default BraceletsType dustDevilsBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Earth Warriors</font></html>",
            description = "Settings for Earth Warrior",
            position = 22,
            closedByDefault = true
    )
    String earthWarriorSection = "earthWarriorSection";

    @ConfigItem(
            keyName = "earthWarriorGear",
            name = "Gear Selection",
            description = "Select gear for Earth Warrior",
            position = 0,
            section = earthWarriorSection
    )
    default AttackType earthWarriorGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "earthWarriorPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Earth Warrior",
            position = 1,
            section = earthWarriorSection
    )
    default DefensivePrayerType earthWarriorPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "earthWarriorOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Earth Warriors",
            position = 2,
            section = earthWarriorSection
    )
    default OffensivePrayerType earthWarriorOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonEarthWarriors",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Earth Warriors.",
            position = 3,
            section = earthWarriorSection
    )
    default boolean useCannonEarthWarriors() {
        return false;
    }
    @ConfigItem(
            keyName = "earthWarriorsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Earth Warriors",
            position = 4,
            section = earthWarriorSection
    )
    default BraceletsType earthWarriorsBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Ents</font></html>",
            description = "Settings for Ent",
            position = 23,
            closedByDefault = true
    )
    String entSection = "entSection";

    @ConfigItem(
            keyName = "entGear",
            name = "Gear Selection",
            description = "Select gear for Ent",
            position = 0,
            section = entSection
    )
    default AttackType entGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "entPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Ent",
            position = 1,
            section = entSection
    )
    default DefensivePrayerType entPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "entOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Ents",
            position = 2,
            section = entSection
    )
    default OffensivePrayerType entOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonEnts",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Ents.",
            position = 3,
            section = entSection
    )
    default boolean useCannonEnts() {
        return false;
    }
    @ConfigItem(
            keyName = "entsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Ents",
            position = 4,
            section = entSection
    )
    default BraceletsType entsBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Fire Giant</font></html>",
            description = "Settings for Fire Giant",
            position = 24,
            closedByDefault = true
    )
    String fireGiantSection = "fireGiantSection";

    @ConfigItem(
            keyName = "fireGiantGear",
            name = "Gear Selection",
            description = "Select gear for Fire Giant",
            position = 0,
            section = fireGiantSection
    )
    default AttackType fireGiantGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "fireGiantPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Fire Giant",
            position = 1,
            section = fireGiantSection
    )
    default DefensivePrayerType fireGiantPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "fireGiantOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Fire Giants",
            position = 2,
            section = fireGiantSection
    )
    default OffensivePrayerType fireGiantOffPrayer() {
        return OffensivePrayerType.NONE;
    }

    @ConfigItem(
            keyName = "useCannonFireGiants",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Fire Giants.",
            position = 3,
            section = fireGiantSection
    )
    default boolean useCannonFireGiants() {
        return false;
    }

    @ConfigItem(
            keyName = "fireGiantsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Fire Giants",
            position = 4,
            section = fireGiantSection
    )
    default BraceletsType fireGiantsBracelet() {
        return BraceletsType.NONE;
    }


    @ConfigSection(
            name = "<html><font color=#7df5bd>Greater Demons</font></html>",
            description = "Settings for Greater Demon",
            position = 25,
            closedByDefault = true
    )
    String greaterDemonSection = "greaterDemonSection";

    @ConfigItem(
            keyName = "greaterDemonGear",
            name = "Gear Selection",
            description = "Select gear for Greater Demon",
            position = 0,
            section = greaterDemonSection
    )
    default AttackType greaterDemonGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "greaterDemonPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Greater Demon",
            position = 1,
            section = greaterDemonSection
    )
    default DefensivePrayerType greaterDemonPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "greaterDemonOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Greater Demons",
            position = 2,
            section = greaterDemonSection
    )
    default OffensivePrayerType greaterDemonOffPrayer() {
        return OffensivePrayerType.NONE;
    }

    @ConfigItem(
            keyName = "useCannonGreaterDemons",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Greater Demons.",
            position = 3,
            section = greaterDemonSection
    )
    default boolean useCannonGreaterDemons() {
        return false;
    }

    @ConfigItem(
            keyName = "greaterDemonsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Greater Demons",
            position = 4,
            section = greaterDemonSection
    )
    default BraceletsType greaterDemonsBracelet() {
        return BraceletsType.NONE;
    }


    @ConfigSection(
            name = "<html><font color=#7df5bd>Green Dragons</font></html>",
            description = "Settings for Green Dragon",
            position = 26,
            closedByDefault = true
    )
    String greenDragonSection = "greenDragonSection";

    @ConfigItem(
            keyName = "greenDragonGear",
            name = "Gear Selection",
            description = "Select gear for Green Dragon",
            position = 0,
            section = greenDragonSection
    )
    default AttackType greenDragonGear() {
        return AttackType.RANGED;
    }

    @ConfigItem(
            keyName = "greenDragonPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Green Dragon",
            position = 1,
            section = greenDragonSection
    )
    default DefensivePrayerType greenDragonPrayer() {
        return DefensivePrayerType.PROTECT_MISSILES;
    }
    @ConfigItem(
            keyName = "greenDragonOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Green Dragons",
            position = 2,
            section = greenDragonSection
    )
    default OffensivePrayerType greenDragonOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonGreenDragons",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Green Dragons.",
            position = 3,
            section = greenDragonSection
    )
    default boolean useCannonGreenDragons() {
        return false;
    }
    @ConfigItem(
            keyName = "greenDragonsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Green Dragons",
            position = 4,
            section = greenDragonSection
    )
    default BraceletsType greenDragonsBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Hellhounds</font></html>",
            description = "Settings for Hellhound",
            position = 27,
            closedByDefault = true
    )
    String hellhoundSection = "hellhoundSection";

    @ConfigItem(
            keyName = "hellhoundGear",
            name = "Gear Selection",
            description = "Select gear for Hellhound",
            position = 0,
            section = hellhoundSection
    )
    default AttackType hellhoundGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "hellhoundPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Hellhound",
            position = 1,
            section = hellhoundSection
    )
    default DefensivePrayerType hellhoundPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "hellhoundOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Hellhounds",
            position = 2,
            section = hellhoundSection
    )
    default OffensivePrayerType hellhoundOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonHellhounds",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Hellhounds.",
            position = 3,
            section = hellhoundSection
    )
    default boolean useCannonHellhounds() {
        return false;
    }

    @ConfigItem(
            keyName = "hellhoundsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Hellhounds",
            position = 4,
            section = hellhoundSection
    )
    default BraceletsType hellhoundsBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Hill Giants</font></html>",
            description = "Settings for Hill Giant",
            position = 28,
            closedByDefault = true
    )
    String hillGiantSection = "hillGiantSection";

    @ConfigItem(
            keyName = "hillGiantGear",
            name = "Gear Selection",
            description = "Select gear for Hill Giant",
            position = 0,
            section = hillGiantSection
    )
    default AttackType hillGiantGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "hillGiantPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Hill Giant",
            position = 1,
            section = hillGiantSection
    )
    default DefensivePrayerType hillGiantPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "hillGiantOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Hill Giants",
            position = 2,
            section = hillGiantSection
    )
    default OffensivePrayerType hillGiantOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonHillGiants",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Hill Giants.",
            position = 3,
            section = hillGiantSection
    )
    default boolean useCannonHillGiants() {
        return false;
    }
    @ConfigItem(
            keyName = "hillGiantsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Hill Giants",
            position = 4,
            section = hillGiantSection
    )
    default BraceletsType hillGiantsBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Ice Giants</font></html>",
            description = "Settings for Ice Giant",
            position = 29,
            closedByDefault = true
    )
    String iceGiantSection = "iceGiantSection";

    @ConfigItem(
            keyName = "iceGiantGear",
            name = "Gear Selection",
            description = "Select gear for Ice Giant",
            position = 0,
            section = iceGiantSection
    )
    default AttackType iceGiantGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "iceGiantPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Ice Giant",
            position = 1,
            section = iceGiantSection
    )
    default DefensivePrayerType iceGiantPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "iceGiantOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Ice Giants",
            position = 2,
            section = iceGiantSection
    )
    default OffensivePrayerType iceGiantOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonIceGiants",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Ice Giants.",
            position = 3,
            section = iceGiantSection
    )
    default boolean useCannonIceGiants() {
        return false;
    }
    @ConfigItem(
            keyName = "iceGiantsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Ice Giants",
            position = 4,
            section = iceGiantSection
    )
    default BraceletsType iceGiantsBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Ice Warriors</font></html>",
            description = "Settings for Ice Warrior",
            position = 30,
            closedByDefault = true
    )
    String iceWarriorSection = "iceWarriorSection";

    @ConfigItem(
            keyName = "iceWarriorGear",
            name = "Gear Selection",
            description = "Select gear for Ice Warrior",
            position = 0,
            section = iceWarriorSection
    )
    default AttackType iceWarriorGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "iceWarriorPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Ice Warrior",
            position = 1,
            section = iceWarriorSection
    )
    default DefensivePrayerType iceWarriorPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "iceWarriorOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Ice Warriors",
            position = 2,
            section = iceWarriorSection
    )
    default OffensivePrayerType iceWarriorOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonIceWarriors",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Ice Warriors.",
            position = 3,
            section = iceWarriorSection
    )
    default boolean useCannonIceWarriors() {
        return false;
    }
    @ConfigItem(
            keyName = "iceWarriorBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Ice Warriors",
            position = 4,
            section = iceWarriorSection
    )
    default BraceletsType iceWarriorBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Jellies</font></html>",
            description = "Settings for Jellies",
            position = 31,
            closedByDefault = true
    )
    String jelliesSection = "jelliesSection";

    @ConfigItem(
            keyName = "jelliesGear",
            name = "Gear Selection",
            description = "Select gear for Jellies",
            position = 0,
            section = jelliesSection
    )
    default AttackType jelliesGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "jelliesPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Jellies",
            position = 1,
            section = jelliesSection
    )
    default DefensivePrayerType jelliesPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "jelliesOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Jellies",
            position = 2,
            section = jelliesSection
    )
    default OffensivePrayerType jelliesOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonJellies",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Jellies.",
            position = 3,
            section = jelliesSection
    )
    default boolean useCannonJellies() {
        return false;
    }
    @ConfigItem(
            keyName = "jellyBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Jellies",
            position = 4,
            section = jelliesSection
    )
    default BraceletsType jellyBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Lava Dragons</font></html>",
            description = "Settings for Lava Dragon",
            position = 32,
            closedByDefault = true
    )
    String lavaDragonSection = "lavaDragonSection";

    @ConfigItem(
            keyName = "lavaDragonGear",
            name = "Gear Selection",
            description = "Select gear for Lava Dragon",
            position = 0,
            section = lavaDragonSection
    )
    default AttackType lavaDragonGear() {
        return AttackType.RANGED;
    }

    @ConfigItem(
            keyName = "lavaDragonPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Lava Dragon",
            position = 1,
            section = lavaDragonSection
    )
    default DefensivePrayerType lavaDragonPrayer() {
        return DefensivePrayerType.PROTECT_MISSILES;
    }
    @ConfigItem(
            keyName = "lavaDragonOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Lava Dragons",
            position = 2,
            section = lavaDragonSection
    )
    default OffensivePrayerType lavaDragonOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonLavaDragons",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Lava Dragons.",
            position = 3,
            section = lavaDragonSection
    )
    default boolean useCannonLavaDragons() {
        return false;
    }

    @ConfigItem(
            keyName = "lavaDragonBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Lava Dragons",
            position = 4,
            section = lavaDragonSection
    )
    default BraceletsType lavaDragonBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Lesser Demons</font></html>",
            description = "Settings for Lesser Demon",
            position = 33,
            closedByDefault = true
    )
    String lesserDemonSection = "lesserDemonSection";

    @ConfigItem(
            keyName = "lesserDemonGear",
            name = "Gear Selection",
            description = "Select gear for Lesser Demon",
            position = 0,
            section = lesserDemonSection
    )
    default AttackType lesserDemonGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "lesserDemonPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Lesser Demon",
            position = 1,
            section = lesserDemonSection
    )
    default DefensivePrayerType lesserDemonPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "lesserDemonOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Lesser Demons",
            position = 2,
            section = lesserDemonSection
    )
    default OffensivePrayerType lesserDemonOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonLesserDemons",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Lesser Demons.",
            position = 3,
            section = lesserDemonSection
    )
    default boolean useCannonLesserDemons() {
        return false;
    }

    @ConfigItem(
            keyName = "lesserDemonsBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Lesser Demons",
            position = 4,
            section = lesserDemonSection
    )
    default BraceletsType lesserDemonsBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Magic Axes</font></html>",
            description = "Settings for Magic Axes",
            position = 34,
            closedByDefault = true
    )
    String magicAxesSection = "magicAxesSection";

    @ConfigItem(
            keyName = "magicAxesGear",
            name = "Gear Selection",
            description = "Select gear for Magic Axes",
            position = 0,
            section = magicAxesSection
    )
    default AttackType magicAxesGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "magicAxesPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Magic Axes",
            position = 1,
            section = magicAxesSection
    )
    default DefensivePrayerType magicAxesPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "magicAxeOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Magic Axes",
            position = 2,
            section = magicAxesSection
    )
    default OffensivePrayerType magicAxeOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonMagicAxes",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Magic Axes.",
            position = 3,
            section = magicAxesSection
    )
    default boolean useCannonMagicAxes() {
        return false;
    }
    @ConfigItem(
            keyName = "magicAxeBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Magic Axes",
            position = 4,
            section = magicAxesSection
    )
    default BraceletsType magicAxeBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Mammoths</font></html>",
            description = "Settings for Mammoth",
            position = 35,
            closedByDefault = true
    )
    String mammothSection = "mammothSection";

    @ConfigItem(
            keyName = "mammothGear",
            name = "Gear Selection",
            description = "Select gear for Mammoth",
            position = 0,
            section = mammothSection
    )
    default AttackType mammothGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "mammothPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Mammoth",
            position = 1,
            section = mammothSection
    )
    default DefensivePrayerType mammothPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "mammothOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Mammoths",
            position = 2,
            section = mammothSection
    )
    default OffensivePrayerType mammothOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonMammoths",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Mammoths.",
            position = 3,
            section = mammothSection
    )
    default boolean useCannonMammoths() {
        return false;
    }
    @ConfigItem(
            keyName = "mammothBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Mammoths",
            position = 4,
            section = mammothSection
    )
    default BraceletsType mammothBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Moss Giants</font></html>",
            description = "Settings for Moss Giant",
            position = 36,
            closedByDefault = true
    )
    String mossGiantSection = "mossGiantSection";

    @ConfigItem(
            keyName = "mossGiantGear",
            name = "Gear Selection",
            description = "Select gear for Moss Giant",
            position = 0,
            section = mossGiantSection
    )
    default AttackType mossGiantGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "mossGiantPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Moss Giant",
            position = 1,
            section = mossGiantSection
    )
    default DefensivePrayerType mossGiantPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "mossGiantOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Moss Giants",
            position = 2,
            section = mossGiantSection
    )
    default OffensivePrayerType mossGiantOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonMossGiants",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Moss Giants.",
            position = 3,
            section = mossGiantSection
    )
    default boolean useCannonMossGiants() {
        return false;
    }

    @ConfigItem(
            keyName = "mossGiantBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Moss Giants",
            position = 4,
            section = mossGiantSection
    )
    default BraceletsType mossGiantBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Pirates</font></html>",
            description = "Settings for Pirate",
            position = 37,
            closedByDefault = true
    )
    String pirateSection = "pirateSection";

    @ConfigItem(
            keyName = "pirateGear",
            name = "Gear Selection",
            description = "Select gear for Pirate",
            position = 0,
            section = pirateSection
    )
    default AttackType pirateGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "piratePrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Pirate",
            position = 1,
            section = pirateSection
    )
    default DefensivePrayerType piratePrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "pirateOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Pirates",
            position = 2,
            section = pirateSection
    )
    default OffensivePrayerType pirateOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonPirates",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Pirates.",
            position = 3,
            section = pirateSection
    )
    default boolean useCannonPirates() {
        return false;
    }

    @ConfigItem(
            keyName = "pirateBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Pirates",
            position = 4,
            section = pirateSection
    )
    default BraceletsType pirateBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Revenants</font></html>",
            description = "Settings for Revenants",
            position = 38,
            closedByDefault = true
    )
    String revenantsSection = "revenantsSection";

    @ConfigItem(
            keyName = "revenantsGear",
            name = "Gear Selection",
            description = "Select gear for Revenants",
            position = 0,
            section = revenantsSection
    )
    default AttackType revenantsGear() {
        return AttackType.MAGIC;
    }

    @ConfigItem(
            keyName = "revenantsPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Revenants",
            position = 1,
            section = revenantsSection
    )
    default DefensivePrayerType revenantsPrayer() {
        return DefensivePrayerType.PROTECT_MAGIC;
    }
    @ConfigItem(
            keyName = "revenantOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Revenants",
            position = 2,
            section = revenantsSection
    )
    default OffensivePrayerType revenantOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "revenantBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Revenants",
            position = 4,
            section = revenantsSection
    )
    default BraceletsType revenantBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigItem(
            keyName = "revenanchoice",
            name = "Rev choice",
            description = "Select Revenant to kill",
            position = 5,
            section = revenantsSection
    )
    default RevChoice revChoice() {
        return RevChoice.GOBLIN;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Rogues</font></html>",
            description = "Settings for Rogues",
            position = 39,
            closedByDefault = true
    )
    String roguesSection = "roguesSection";

    @ConfigItem(
            keyName = "roguesGear",
            name = "Gear Selection",
            description = "Select gear for Rogues",
            position = 0,
            section = roguesSection
    )
    default AttackType roguesGear() {
        return AttackType.RANGED;
    }

    @ConfigItem(
            keyName = "roguesPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Rogues",
            position = 1,
            section = roguesSection
    )
    default DefensivePrayerType roguesPrayer() {
        return DefensivePrayerType.PROTECT_MISSILES;
    }
    @ConfigItem(
            keyName = "rogueOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Rogues",
            position = 2,
            section = roguesSection
    )
    default OffensivePrayerType rogueOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonRogue",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Rogues.",
            position = 3,
            section = roguesSection
    )
    default boolean useCannonRogue() {
        return false;
    }
    @ConfigItem(
            keyName = "rogueBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Rogues",
            position = 4,
            section = roguesSection
    )
    default BraceletsType rogueBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Scorpions</font></html>",
            description = "Settings for Scorpions",
            position = 40,
            closedByDefault = true
    )
    String scorpionsSection = "scorpionsSection";

    @ConfigItem(
            keyName = "scorpionsGear",
            name = "Gear Selection",
            description = "Select gear for Scorpions",
            position = 0,
            section = scorpionsSection
    )
    default AttackType scorpionsGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "scorpionsPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Scorpions",
            position = 1,
            section = scorpionsSection
    )
    default DefensivePrayerType scorpionsPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "scorpionOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Scorpions",
            position = 2,
            section = scorpionsSection
    )
    default OffensivePrayerType scorpionOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonScorpion",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Scorpions.",
            position = 3,
            section = scorpionsSection
    )
    default boolean useCannonScorpion() {
        return false;
    }
    @ConfigItem(
            keyName = "scorpionBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Scorpions",
            position = 4,
            section = scorpionsSection
    )
    default BraceletsType scorpionBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Skeletons</font></html>",
            description = "Settings for Skeletons",
            position = 41,
            closedByDefault = true
    )
    String skeletonsSection = "skeletonsSection";

    @ConfigItem(
            keyName = "skeletonsGear",
            name = "Gear Selection",
            description = "Select gear for Skeletons",
            position = 0,
            section = skeletonsSection
    )
    default AttackType skeletonsGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "skeletonsPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Skeletons",
            position = 1,
            section = skeletonsSection
    )
    default DefensivePrayerType skeletonsPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "skeletonOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Skeletons",
            position = 2,
            section = skeletonsSection
    )
    default OffensivePrayerType skeletonOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonSkeleton",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Skeletons.",
            position = 3,
            section = skeletonsSection
    )
    default boolean useCannonSkeleton() {
        return false;
    }

    @ConfigItem(
            keyName = "skeletonBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Skeletons",
            position = 4,
            section = skeletonsSection
    )
    default BraceletsType skeletonBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Spiders</font></html>",
            description = "Settings for Spiders",
            position = 42,
            closedByDefault = true
    )
    String spidersSection = "spidersSection";

    @ConfigItem(
            keyName = "spidersGear",
            name = "Gear Selection",
            description = "Select gear for Spiders",
            position = 0,
            section = spidersSection
    )
    default AttackType spidersGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "spidersPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Spiders",
            position = 1,
            section = spidersSection
    )
    default DefensivePrayerType spidersPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "spiderOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Spiders",
            position = 2,
            section = spidersSection
    )
    default OffensivePrayerType spiderOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonSpiders",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Spiders.",
            position = 3,
            section = spidersSection
    )
    default boolean useCannonSpiders() {
        return false;
    }
    @ConfigItem(
            keyName = "spiderBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Spiders",
            position = 4,
            section = spidersSection
    )
    default BraceletsType spiderBracelet() {
        return BraceletsType.NONE;
    }
    @ConfigSection(
            name = "<html><font color=#7df5bd>Spiritual Creatures</font></html>",
            description = "Settings for Spiritual Creatures",
            position = 43,
            closedByDefault = true
    )
    String spiritualCreaturesSection = "spiritualCreaturesSection";

    @ConfigItem(
            keyName = "spiritualCreaturesGear",
            name = "Gear Selection",
            description = "Select gear for Spiritual Creatures",
            position = 0,
            section = spiritualCreaturesSection
    )
    default AttackType spiritualCreaturesGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "spiritualCreaturesPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Spiritual Creatures",
            position = 1,
            section = spiritualCreaturesSection
    )
    default DefensivePrayerType spiritualCreaturesPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "spiritualCreatureOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Spiritual Creatures",
            position = 2,
            section = spiritualCreaturesSection
    )
    default OffensivePrayerType spiritualCreatureOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonSpiritualCreatures",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Spiritual Creatures.",
            position = 3,
            section = spiritualCreaturesSection
    )
    default boolean useCannonSpiritualCreatures() {
        return false;
    }
    @ConfigItem(
            keyName = "spiritualCreaturesBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Spiritual Creatures",
            position = 4,
            section = spiritualCreaturesSection
    )
    default BraceletsType spiritualCreaturesBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Zombies</font></html>",
            description = "Settings for Zombies",
            position = 44,
            closedByDefault = true
    )
    String zombiesSection = "zombiesSection";

    @ConfigItem(
            keyName = "zombiesGear",
            name = "Gear Selection",
            description = "Select gear for Zombies",
            position = 0,
            section = zombiesSection
    )
    default AttackType zombiesGear() {
        return AttackType.MELEE;
    }

    @ConfigItem(
            keyName = "zombiesPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Zombies",
            position = 1,
            section = zombiesSection
    )
    default DefensivePrayerType zombiesPrayer() {
        return DefensivePrayerType.PROTECT_MELEE;
    }
    @ConfigItem(
            keyName = "zombieOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Zombies",
            position = 2,
            section = zombiesSection
    )
    default OffensivePrayerType zombieOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonZombies",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Zombies.",
            position = 3,
            section = zombiesSection
    )
    default boolean useCannonZombies() {
        return false;
    }
    @ConfigItem(
            keyName = "zombieBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Zombies",
            position = 4,
            section = zombiesSection
    )
    default BraceletsType zombieBracelet() {
        return BraceletsType.NONE;
    }

    @ConfigSection(
            name = "<html><font color=#7df5bd>Nechryaels</font></html>",
            description = "Settings for Nechryael",
            position = 45,
            closedByDefault = true
    )
    String nechryaelSection = "nechryaelSection";

    @ConfigItem(
            keyName = "nechryaelGear",
            name = "Gear Selection",
            description = "Select gear for Nechryael",
            position = 0,
            section = nechryaelSection
    )
    default AttackType nechryaelGear() {
        return AttackType.MAGIC;
    }

    @ConfigItem(
            keyName = "nechryaelPrayer",
            name = "Protection Prayer",
            description = "Select protection prayer for Nechryael",
            position = 1,
            section = nechryaelSection
    )
    default DefensivePrayerType nechryaelPrayer() {
        return DefensivePrayerType.PROTECT_MAGIC;
    }
    @ConfigItem(
            keyName = "nechryaelOffPrayer",
            name = "Offensive Prayer",
            description = "Select offensive prayer for Nechryael",
            position = 2,
            section = nechryaelSection
    )
    default OffensivePrayerType nechryaelOffPrayer() {
        return OffensivePrayerType.NONE;
    }
    @ConfigItem(
            keyName = "useCannonNechryael",
            name = "Use Cannon?",
            description = "Enable or disable using a cannon for Nechryael.",
            position = 3,
            section = nechryaelSection
    )
    default boolean useCannonNechryael() {
        return false;
    }
    @ConfigItem(
            keyName = "nechryaelBracelet",
            name = "Bracelet Type",
            description = "Select bracelet for Nechryaels",
            position = 4,
            section = nechryaelSection
    )
    default BraceletsType nechryaelBracelet() {
        return BraceletsType.NONE;
    }

    /**
     * @author BIGGS
     * discord: @biggs.exe
     */
}
