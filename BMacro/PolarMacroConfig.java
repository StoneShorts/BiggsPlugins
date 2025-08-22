package net.runelite.client.live.inDevelopment.biggs.BMacro;

import net.runelite.client.config.*;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.*;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("PolarMacro")
public interface PolarMacroConfig extends Config
{
    // ===== CONTROLS SECTION =====
    @ConfigSection(
            name = "<html><font color='#1285C7'>Controls</font></html>",
            description = "Keybinds for executing macros and toggles",
            position = 0,
            closedByDefault = false
    )
    String controls = "controls";

    @ConfigItem(
            keyName = "macroHotkey",
            name = "<html><font color='#FF8C00'>Execute Combo</font></html>",
            description = "Hotkey to run the selected combo",
            position = 1,
            section = controls
    )
    default Keybind macroHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "castVeng",
            name = "<html><font color='#FF8C00'>Cast Vengeance</font></html>",
            description = "Hotkey to cast Vengeance alone",
            position = 2,
            section = controls
    )
    default Keybind castVeng() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "tripleEat",
            name = "<html><font color='#FF8C00'>Triple Eat</font></html>",
            description = "Hotkey to eat Anglerfish → Saradomin brew → Karambwan",
            position = 3,
            section = controls
    )
    default Keybind tripleEat() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "togglePrayerHotkey",
            name = "<html><font color='#FF8C00'>Toggle Prayer Mode</font></html>",
            description = "Hold to allow swap-prayer logic to run",
            position = 4,
            section = controls
    )
    default Keybind togglePrayerHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "whackHotkey",
            name = "<html><font color='#FF8C00'>Whack</font></html>",
            description = "Equip whack",
            position = 5,
            section = controls
    )
    default Keybind toggleWhackHotkey() { return Keybind.NOT_SET; }

    @ConfigItem(
            keyName = "whackAfterSpecial",
            name = "<html><font color='#FF8C00'>Whack After Combo</font></html>",
            description = "Whack after special",
            position = 6,
            section = controls
    )
    default boolean whackAfterSpecial() { return false; }

    @ConfigItem(
            keyName = "comboOnSpec",
            name = "<html><font color='#FF8C00'>Combo On Spec</font></html>",
            description = "Combo when opponent venges?",
            position = 7,
            section = controls
    )
    default boolean comboOnSpec() { return false; }

    // ===== COMBO SECTION =====
    @ConfigSection(
            name = "<html><font color='#00CED1'>Combo</font></html>",
            description = "Select and configure your combat combos",
            position = 10,
            closedByDefault = true
    )
    String combo = "combo";

    @ConfigItem(
            keyName = "selectedCombo",
            name = "<html><font color='#00CED1'>Combo Type</font></html>",
            description = "Choose which combo to execute",
            position = 11,
            section = combo
    )
    default ComboType selectedCombo() { return ComboType.VENG_VW_MAUL; }

    // ===== GEAR SECTION =====
    @ConfigSection(
            name = "<html><font color='#32CD32'>Gear</font></html>",
            description = "Automatic gear toggles",
            position = 19,
            closedByDefault = true
    )
    String gear = "gear";

    @ConfigItem(
            keyName = "mainGearSet",
            name = "<html><font color='#32CD32'>Main Gear Set</font></html>",
            description = "copy gear.",
            position = 21,
            section = gear
    )
    default String mainGearType() {
        return "";
    }

    @ConfigItem(
            keyName = "copyGear",
            name = "<html><font color='#32CD32'>Copy Gear</font></html>",
            description = "copy gear.",
            position = 21,
            section = gear
    )
    default boolean copyGear() {
        return false;
    }

    @ConfigItem(
            keyName = "equipMage",
            name = "<html><font color='#32CD32'>Mage Weapon</font></html>",
            description = "mage weapon",
            position = 22,
            section = gear
    )
    default String equipMage() { return ""; }

    @ConfigItem(
            keyName = "equipRanged",
            name = "<html><font color='#32CD32'>Ranged Weapon</font></html>",
            description = "ranged weapon",
            position = 23,            section = gear
    )
    default String equipRanged() { return ""; }

    @ConfigItem(
            keyName = "equipMelee",
            name = "<html><font color='#32CD32'>Melee Weapon</font></html>",
            description = "melee weapon",
            position = 24,            section = gear
    )
    default String equipMelee() { return ""; }

    @ConfigItem(
            keyName = "equipWhack",
            name = "<html><font color='#434537'>Whack Weapon</font></html>",
            description = "whack weapon",
            position = 25,            section = gear
    )
    default String equipWhack() { return ""; }


    @ConfigItem(
            keyName = "equipRecoil",
            name = "<html><font color='#32CD32'>Equip Recoil</font></html>",
            description = "Automatically equip Ring of recoil if available",
            position = 26,
            section = gear
    )
    default boolean equipRecoil() { return false; }

    // ===== PRAYER SECTION =====
    @ConfigSection(
            name = "<html><font color='#FFD700'>Prayer & Food</font></html>",
            description = "Prayer & food toggling settings",
            position = 30,
            closedByDefault = true
    )
    String prayer = "prayer";

    @ConfigItem(
            keyName = "swapPrayer",
            name = "<html><font color='#FFD700'>Swap Prayer</font></html>",
            description = "Enable prayer swap for weapons",
            position = 31,
            section = prayer
    )
    default boolean swapPrayer() { return false; }


    @ConfigItem(
            keyName = "rangedPrayer",
            name = "<html><font color='#FFD700'>Ranged Prayer</font></html>",
            description = "Ranged prayer to use",
            position = 32,
            section = prayer
    )
    default RangedPrayer rangedPrayer() { return RangedPrayer.NONE; }

    @ConfigItem(
            keyName = "meleePrayer",
            name = "<html><font color='#FFD700'>Melee Prayer</font></html>",
            description = "Melee prayer to use",
            position = 33,
            section = prayer
    )
    default MeleePrayer meleePrayer() { return MeleePrayer.NONE; }


    @ConfigItem(
            keyName = "prayerSafetty",
            name = "<html><font color='#FFD700'>Prayer Safety?</font></html>",
            description = "Drink prayer pot when prayer level is low",
            position = 34,
            section = prayer
    )
    default boolean prayerSafety() { return false; }
    @Range(min = 1, max = 15)
    @ConfigItem(
            keyName = "prayerSafetyLevel",
            name = "<html><font color='#FFD700'>Safety Level</font></html>",
            description = "Lowest level at which to drink prayer pot/restore",
            position = 35,
            section = prayer
    )
    default int prayerSafetyLevel() { return 8; }

    @ConfigItem(
            keyName = "mainFood",
            name = "<html><font color='#FFD700'>Main Food</font></html>",
            description = "main food for triple eat",
            section = prayer,
            position = 36
    )
    default String mainFood() { return "anglerfish"; }

    @ConfigItem(
            keyName = "tickFood",
            name = "<html><font color='#FFD700'>Tick food</font></html>",
            description = "tick food for triple eat",
            section = prayer,
            position = 37
    )
    default String tickFood() { return "karambwan"; }

    @ConfigItem(
            keyName = "antiTripleEat",
            name = "<html><font color='#FFD700'>Anti TripleEat</font></html>",
            description = "Triple eat when your health is < tripleeatsafety level and someone specs on you",
            position = 38,
            section = prayer
    )
    default boolean tripleEatOnThreat() { return false; }
    @Range(min = 1, max = 99)
    @ConfigItem(
            keyName = "tripleEatSafety",
            name = "<html><font color='#FFD700'>TEat Minimum HP</font></html>",
            description = "Minimum HP to anti triple eat",
            position = 39,
            section = prayer
    )
    default int minimumTripleEat() { return 50; }

    // ===== EXPERIENCE SECTION =====
    @ConfigSection(
            name = "<html><font color='#BA55D3'>Experience</font></html>",
            description = "One-drop XP spec settings",
            position = 40,
            closedByDefault = true
    )
    String experience = "experience";

    @ConfigItem(
            keyName = "expSpec",
            name = "<html><font color='#BA55D3'>Exp Drop Combo</font></html>",
            description = "Trigger combo on single-drop XP",
            position = 41,
            section = experience
    )
    default ExpType expType() { return ExpType.NONE; }

    @Range(min = 1, max = 99)
    @ConfigItem(
            keyName = "specOverExp",
            name = "<html><font color='#BA55D3'>Damage Threshold</font></html>",
            description = "Minimum hit (in game damage) to trigger combo (4XP = 1 Damage)",
            position = 42,
            section = experience
    )
    default int specOverExp() { return 25; }

    @Range(min = 1, max = 99)
    @ConfigItem(
            keyName = "targetHPForCombo",
            name = "<html><font color='#BA55D3'>Target HP %</font></html>",
            description = "MMinimum target HP % to trigger combo",
            position = 43,
            section = experience
    )
    default int targetHPForCombo() { return 67; }

    @ConfigItem(
            keyName = "useTargetHP",
            name = "<html><font color='#BA55D3'>Use Target HP</font></html>",
            description = "Use target HP % to trigger combo?",
            position = 44,
            section = experience
    )
    default boolean useTargetHP() { return false; }

    @ConfigItem(
            keyName = "comboStacks",
            name = "<html><font color='#BA55D3'>Stacks For Combo</font></html>",
            description = "Trigger combo on X valid XP Drops",
            position = 45,
            section = experience
    )
    default StacksForCombo stacksForCombo() { return StacksForCombo.OFF; }

    @ConfigItem(
            keyName = "stacksAmount",
            name = "<html><font color='#BA55D3'>Stacks Count</font></html>\",",
            description = "Sequence of XP drops to trigger combo",
            position = 46,
            section = experience
    )
    default String stacksAmount() { return "4,4"; }


    @ConfigSection(
            name = "Quick Banking",
            description = "Manage up to 30 bank‐loadout presets",
            position = 49
    )
    String quickBankSection = "quickBankSection";

    @ConfigItem(
            keyName = "bankSpeed",
            name = "Banking Speed",
            description = "How quick do you like it baby?",
            section = quickBankSection,
            position = 1
    )
    default BankSpd bankSpeed() { return BankSpd.NORMAL; }

    @ConfigItem(
            keyName = "editQuickBank",
            name = "Edit Loadouts",
            description = "Edit your loadouts for saved loadouts",
            section = quickBankSection,
            position = 2
    )
    default boolean editQuickBank()
    {
        return false;
    }

    // ===== EXTRA SECTION =====
    @ConfigSection(
            name = "<html><font color='#BA3D63'>Misc</font></html>",
            description = "Misc settings",
            position = 50,
            closedByDefault = true
    )
    String misc = "misc";

    @ConfigItem(
            keyName = "hpprayeroverlaytoggle",
            name = "<html><font color='#BA3D63'>HP/Pray Overlay</font></html>",
            description = "Toggle HP/Prayer overlay",
            position = 51,
            section = misc
    )
    default boolean hpprayeroverlaytoggle() { return false; }

    @ConfigItem(
            keyName = "expoverlaytoggle",
            name = "<html><font color='#BA3D63'>EXP Overlay</font></html>",
            description = "Toggle EXP overlay",
            position = 52,
            section = misc
    )
    default boolean expoverlaytoggle() { return false; }

    @ConfigItem(
            keyName = "expoverlayspeed",
            name = "<html><font color='#BA3D63'>EXP Overlay Speed</font></html>",
            description = "EXP overlay speed",
            position = 53,
            section = misc
    )
    default EXPDropSpeed expoverlayspeed() { return EXPDropSpeed.FAST; }

    @Alpha
    @ConfigItem(
            keyName = "highDamageColor",
            name = "High Damage Color",
            description = "Color indicating damage above your specified minimum to spec.",
            position = 54,
            section = misc
    )
    default Color highDamageColor() {
        return new Color(222, 103, 2, 202);
    }

    @Alpha
    @ConfigItem(
            keyName = "lowDamageColor",
            name = "Low Damage Color",
            description = "Color indiciating lower than specified damage.",
            position = 55,
            section = misc
    )
    default Color lowDamageColor() {
        return new Color(150, 0, 0, 150);
    }

    @ConfigItem(
            keyName = "resetCurrentTarget",
            name = "Reset Current Target",
            description = "Reset current target",
            position = 96
    )
    default boolean resetCurrentTarget() { return false; }

    @ConfigItem(
            keyName = "getAnimID",
            name = "Get Anim ID",
            description = "Reset current target",
            position = 97
    )
    default boolean getAnimID() { return false; }

    @ConfigItem(
            keyName = "hideOthers",
            name = "Hide Others",
            description = "Hide others",
            position = 98
    )
    default boolean hideOthers() { return false; }

    @ConfigItem(
            keyName = "hideOthersStrictness",
            name = "Hide Others Strictness",
            description = "Set the strictness of hiding other players",
            position = 99
    )
    default HidePlayer hideOthersStrictness() {
        return HidePlayer.FIVE;
    }


    @ConfigItem(
            keyName = "useGreaterCorruptionMenu",
            name = "Greater Corruption?",
            description = "Use it?",
            position = 99
    )
    default boolean useGreaterCorruption() { return false; }

    @ConfigItem(
            keyName = "passwordNigga",
            name = "Password Bitch",
            description = "L0l Faggot",
            position = 100)
    default String passwordNigga() { return ""; }

    @ConfigItem(
            keyName = "webHookFunName",
            name = "Webhook Name",
            description = "Name to use for webhooks",
            position = 101
    )
    default String webhookFunName() { return "Dumb Faggot"; }

}
