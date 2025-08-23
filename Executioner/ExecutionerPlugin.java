/*
 * Copyright (c) 2024 Polar Plugins All rights reserved.
 *
 * This software is the confidential and proprietary information of Polar Plugins
 * ("Confidential Information"). You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement you entered into
 * with Polar Plugins
 *
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * You may not use, modify, or distribute this code without explicit permission.
 *
 * This code is for educational purposes only and is not for resale or commercial use.
 * Any violation will be prosecuted to the fullest extent of the law.
 */
//IF YOU HAVE FOUND THIS WITHOUT PERMISSION PLEASE CONTACT STAFF ON DISCORD: discord.gg/polarplugins.
//YOU WILL BE REWARDED. THIS IS FOR TEST PURPOSES EVERYTHING IS LOGGED, UNAUTHORIZED LOGINS WILL NOT ALLOW USE!
package net.runelite.client.live.inDevelopment.biggs.Executioner;

import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.*;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtils.API.*;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.BankInventoryInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.InventoryInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.NPCInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.TileObjectInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtility.TabType;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtility.Util.MouseUtil;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtility.Util.TabUtil;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.DinoUtils.util.IntUtil;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.Packets.MousePackets;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.Packets.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.Packets.WidgetPackets;
import net.runelite.client.live.breakhandler.PolarBreakHandler.PolarBreakHandler;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.*;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala.AmmoSelect;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala.RevChoice;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.MonsterRules;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI.isBankOpen;

@PluginDependency(PacketUtilsPlugin.class)
@PluginDependency(PolarAPI.class)
@PluginDescriptor(
        name = "<html><font color=#48f542>[$]</font>[<font color=#42e6f5>\uD83C\uDD7F</font>] Wildy Slayer",
        description = "Executioner.",
        enabledByDefault = false,
        tags = {"polar", "biggs", "combat", "slayer"})
@Slf4j
public class ExecutionerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ExecutionerConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ExecutionerOverlay overlay;
    @Inject
    private WorldHopper worldHopper;
    @Inject
    private PolarBreakHandler breakHandler;
    //  @Inject private LicenseVerificationHandler licenseVerificationHandler;
    public MonsterRules currentMonster;
    @Getter
    private String npcName;
    @Getter
    public Prayer[] prayersToFlick;
    @Getter
    private int cannonAmmo;
    public static boolean escape;
    public static boolean logout;
    @Getter
    private int slayerPoints;
    @Getter
    private int slayerTaskStreak;
    @Getter
    private int slayerTasksComplete;
    @Getter
    private final HashMap<WorldPoint, Integer> bearTraps = new HashMap<>();
    @Getter
    private final HashMap<WorldPoint, Integer> projectiles = new HashMap<>();
    @Getter
    @Setter
    public int wildyLvl = 0;
    private int restockProgress = 0;
    private int stamProgress = 0;
    private int itemsAddedToBag = 0;
    @Getter
    private int hopTimer = 0; // This will track our hop timer across ticks.
    @Getter
    private final Map<GraphicsObject, Integer> theSkyIsFalling = new HashMap<>();
    @Getter
    private Instant startTime;
    @Getter
    private State state, currentState;
    @Getter
    private boolean pluginRunning;
    @Getter
    public int tickDelay;
    @Getter
    public int nextRunEnergy;
    @Getter
    public int timeout;
    @Getter
    public Player lastAttackedPlayer;
    @Getter
    public Prayer toPray;
    public boolean hasLootingBag = InventoryUtil.hasItem(ItemID.LOOTING_BAG);
    public boolean bagFull = false;
    @Getter
    private boolean shouldGetCannon = false;
    private boolean initialHopAttempted = false;
    private int cannonFireTimer = 0;
    int prayer = 0;
    int hitpoints = 0;
    int slayer = 0;
    private final List<String> messages = new ArrayList<>();
    @Getter
    public PriorityQueue<Player> pkQueue;
    @Getter
    int currentHitpoints, currentPrayer, currentStrength, maxHitpoints, maxPrayer, maxStrength, wildyLevel, minWildernessLevel, maxWildernessLevel;
    private int getHitpoints() {
        return this.client.getBoostedSkillLevel(Skill.HITPOINTS);
    }

    private boolean justDodged = false;
    public int tick = 0;
    private int lastDodgeTick = 0;
    @Getter
    private int previousWorld = -1;

    private int getSlayer() {
        return this.client.getBoostedSkillLevel(Skill.SLAYER);
    }

    private int getPrayer() {
        return this.client.getBoostedSkillLevel(Skill.PRAYER);
    }

    WorldArea slayerMasterHouse = new WorldArea(3107, 3511, 5, 7, 0);
    WorldArea edge = new WorldArea(3073, 3464, 52, 53, 0);
    WorldArea d = new WorldArea(3042, 3513, 82, 9, 0);
    WorldArea dd = new WorldArea(3124, 3519, 116, 3, 0);
    WorldArea dee = new WorldArea(3042, 3521, 206, 4, 0);
    WorldArea deeast = new WorldArea(3134, 3519, 159, 3, 0);
    WorldArea edgeBanky = new WorldArea(3091, 3488, 8, 11, 0);

    WorldArea corp = new WorldArea(2961, 4377, 12, 14, 2);
    WorldArea outsideCorp = new WorldArea(3198, 3649, 59, 37, 0);
    WorldArea wildySlayerCaveEntrance = new WorldArea(3257, 3660, 6, 9, 0);
    WorldArea wildySlayerCave = new WorldArea(3320, 10041, 144, 135, 0);

    WorldArea safeTeleWildy = new WorldArea(2946, 3539, 417, 211, 0);

    WorldArea revHole = new WorldArea(3070, 3651, 11, 9, 0);

    WorldArea banditCampTeleSpawn = new WorldArea(3011, 3635, 59, 30, 0);
    WorldArea wildy = new WorldArea(2946, 3536, 451, 363, 0);

    WorldArea outsidelavamaze = new WorldArea(3020, 3822, 23, 32, 0);
    WorldArea kbdGateArea = new WorldArea(3007, 3848, 2, 4, 0);
    WorldArea kbdTrapDoorArea = new WorldArea(3013, 3845, 7, 8, 0);
    WorldArea spiderKbdArea = new WorldArea(3063, 10252, 10, 11, 0);

    WorldArea nulodionArea = new WorldArea(3006, 3447, 12, 11, 0);
    WorldArea nulodionDoorArea = new WorldArea(3015, 3452, 2, 3, 0);
    WorldArea insideNulodionArea = new WorldArea(3007, 3452, 8, 3, 0);

    WorldArea gwd = new WorldArea(3015, 10114, 54, 53, 0);
    WorldArea gwdObstacleArea = new WorldArea(3056, 10150, 14, 17, 3);
    WorldArea gwdCaveEntrance = new WorldArea(3013, 3735, 6, 10, 0);
    WorldArea creviceEntrance = new WorldArea(3048, 10164, 6, 3, 3);


    WorldArea revImpArea =  new WorldArea(3191, 10057, 20, 27, 0);
    WorldArea revGobArea = new WorldArea(3214, 10055, 32, 28, 0);
    WorldArea revHobArea = new WorldArea(3237, 10075, 11, 50, 0);
    WorldArea revOrkArea = new WorldArea(3185, 10083, 51, 31, 0);
    WorldArea southRevs = new WorldArea(3115, 10050, 175, 123, 0);



    WorldArea edgeDungeonWildy = new WorldArea(3075, 9918, 61, 87, 0);
    WorldArea edgeDungeonGate1 = new WorldArea(3099, 9904, 9, 10, 0);
    WorldArea edgeDungeonGate2 = new WorldArea(3129, 9914, 6, 4, 0);
    WorldArea edgeDungeonFAR = new WorldArea(3098, 9902, 38, 11, 0);
    WorldArea edgeDungeonBelow =  new WorldArea(3091, 9866, 10, 29, 0);
    WorldArea edgeDungeonTrapArea = new WorldArea(3091, 3467, 8, 7, 0);



    WorldPoint slayerMaster = new WorldPoint(3109, 3514, 0);
    WorldPoint edgeBank = new WorldPoint(3094, 3491, 0);
    WorldPoint ditchPoint = new WorldPoint(3107, 3520, 0);
    WorldPoint ditchPoint2 = new WorldPoint(3103, 3520, 0);
    WorldPoint ditchPoint3 = new WorldPoint(3096, 3520, 0);
    WorldPoint cavePoint = new WorldPoint(3259, 3662, 0);
    WorldPoint revPoint = new WorldPoint(3077, 3654, 0);
    WorldPoint artioPoint = new WorldPoint(3116, 3675, 0);
    WorldPoint edgeLeverPoint = new WorldPoint(3090, 3475, 0);
    WorldPoint safePoint = new WorldPoint(2977, 3728, 0);
    WorldPoint deepGate1 = new WorldPoint(3041, 10307, 0);
    WorldPoint deepGate2 = new WorldPoint(3023, 10311, 0);
    WorldPoint deepGate3 = new WorldPoint(3044, 10342, 0);
    WorldPoint kbdGatePoint = new WorldPoint(3007, 3849, 0);
    WorldPoint kbdLadderPoint = new WorldPoint(3017, 3850, 0);
    WorldPoint gwdCavePoint = new WorldPoint(3017, 3740, 0);

    WorldPoint edgeDungeonTrapPoint = new WorldPoint(3096, 3468, 0);
    WorldPoint edgeDungeonGatePoint = new WorldPoint(3104, 9909, 0);
    WorldPoint edgeDungeonGate2Point = new WorldPoint(3131, 9916, 0);

    WorldPoint nulodionDoor = new WorldPoint(3015, 3453, 0);

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    @Provides
    private ExecutionerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ExecutionerConfig.class);
    }

    private boolean depositWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withId(786478).first();
        return widget.isPresent() && !widget.get().isHidden();
    }

    private boolean LBDepositWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withId(983046).first();
        return widget.isPresent() && !widget.get().isHidden();
    }

    private boolean exitCaveWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withId(14352385).first();
        return widget.isPresent() && !widget.get().isHidden();
    }

    private boolean LBCloseWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withId(983048).first();
        return widget.isPresent() && !widget.get().isHidden();
    }

    private boolean needsTeleport() {
        Set<String> obstacleMonsters = new HashSet<>(Arrays.asList("Moss giant", "Ice warrior", "Hill giant", "Black knight"));
        String currentMonsterName = currentMonster != null ? currentMonster.getNpcName() : "";

        // Check if the current monster is one of the obstacle monsters, player is not ready to slay, and player is in the safe teleport area
        return obstacleMonsters.contains(currentMonsterName) && !isReadyToSlay() && PolarAPI.isPlayerInArea(safeTeleWildy);
    }

    private boolean needsSafeTeleport() {
        return getSlayerTaskSize() < 1 && PolarAPI.isPlayerInArea(safeTeleWildy);
    }


    private boolean LBWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withText("Bank your loot").first();
        return widget.isPresent() && !widget.get().isHidden();
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        String[] whitelistNames = config.messageToSend().split(",");
        messages.addAll(Arrays.asList(whitelistNames));
        keyManager.registerKeyListener(pluginToggle);
        breakHandler.registerPlugin(this);
        startTime = Instant.now();
        previousWorld = -1;
        stamProgress = 0;
        initialHopAttempted = false;
        hopTimer = 0;
        pluginRunning = false;
        prayersToFlick = new Prayer[2];
        restockProgress = 0;
        log.info("Executioner started successfully.");
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
        keyManager.unregisterKeyListener(pluginToggle);
        breakHandler.unregisterPlugin(this);
        pluginRunning = false;
        initialHopAttempted = false;
        shouldGetCannon = false;
        previousWorld = -1;
        stamProgress = 0;
        hopTimer = 0;
        prayersToFlick = null;
        restockProgress = 0;
        messages.clear();
        log.info("Executioner shut down.");
    }

    public void togglePlugin() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        pluginRunning = !pluginRunning;
        if (pluginRunning) {
            breakHandler.startPlugin(this);
        } else {
            breakHandler.stopPlugin(this);
        }
    }

    private int tickDelay() {
        return config.tickDelay() ? IntUtil.randomInt(config.tickDelayMin(), config.tickDelayMax()) + IntUtil.randomInt(1, 3) : 0;
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        updateTaskDetails();
        if (client.getGameState() != GameState.LOGGED_IN || !pluginRunning || breakHandler.isBreakActive(this)) {
            return;
        }
        if (slayerPoints < 30) {
            if (state == State.SKIP_TASK) {
                PolarAPI.stopPlugin(this);
                PolarAPI.sendClientMessage("Not enough points to skip task.. shutting down.");
                log.info("Not enough points to skip task.. shutting down.");
            }
        }
        int currentWorld = client.getWorld();
        if (hopTimer > 0) {
            hopTimer--;
            hopWorlds();
            PolarAPI.sendClientMessage("Attempting to hop worlds.");
        }
        if (client.getVarbitValue(Varbits.ANTIFIRE) == 0 || client.getVarbitValue(Varbits.SUPER_ANTIFIRE) == 0) {
            PolarAPI.useItemContainsName("antifire", "Drink");
        }
        if (currentWorld != previousWorld) {
            hopTimer = 0;
            log.info("World changed from {} to {}. Resetting hop timer.", previousWorld, currentWorld);
            PolarAPI.sendClientMessage("World changed from " + previousWorld + " to" + currentWorld + ". Resetting hop timer.");
            previousWorld = currentWorld;  // Update the previous world to the current one
        }
        if (state == State.ENERGY && InventoryUtil.nameContainsNoCase("stamina potion(").first().isPresent() && PolarAPI.runEnergy() < 62) {
            PolarAPI.drinkPotion("Stamina potion(4)");
            PolarAPI.drinkPotion("Stamina potion(3)");
            PolarAPI.drinkPotion("Stamina potion(2)");
            PolarAPI.drinkPotion("Stamina potion(1)");
        }
        if (PolarAPI.isPlayerInArea(nulodionArea) && Inventory.search().nameContainsNoCase("cannon base").first().isPresent()) {
            teleForRestock();
            shouldGetCannon = false;
        }
        if (PolarAPI.isPlayerInArea(edge)) {
            togglePrayersOff();
            }
        if (PolarAPI.isPlayerInArea(spiderKbdArea)) {
            exitCaveIfPossible();
        }
        if (!Widgets.search().withTextContains("Enter amount:").hiddenState(false).empty() && !isBankOpen()) {
            client.runScript(101, -1);
        }
        handleLootingBag();
        if (shouldEatFood() && !isBankOpen()) {
            handleFood();
        }
        if (shouldDrinkPrayer() && !isBankOpen()) {
            drinkPotion();
        }
        if (shouldDrinkBoostPot() && !isBankOpen()) {
            drinkBoostPotion();
        }
        String boostPotionName = getBoostPotion(getRequiredGear(currentMonster));

        if (InventoryUtil.hasItem(config.AmmoType().getId()) || InventoryUtil.hasItem(config.BoltAmmoType().getId())) {
            InventoryInteraction.useItem(config.AmmoType().getId(), "Wield", "Equip", "Wear");
            InventoryInteraction.useItem(config.BoltAmmoType().getId(), "Wield", "Equip", "Wear");
        }
        if (getSlayerTaskSize() > 0 && (!PolarAPI.isPlayerInArea(edge) || !PolarAPI.isPlayerInArea(d) || !PolarAPI.isPlayerInArea(dee) || !PolarAPI.isPlayerInArea(dd) || !PolarAPI.isPlayerInArea(deeast))) {
            checkAndTeleportIfPlayerInArea();
        }
        if (PolarAPI.isPlayerInArea(wildy) && getSlayerTaskSize() < 1 && !PolarAPI.isPlayerInArea(safeTeleWildy)) {
            walkSafety();
            log.info("We need to get to safety!");
        }
        if (needsTeleport() || needsSafeTeleport()) {
            performTeleport();
            log.info("Performing teleport out");
        }
        if (TileItems.search().withName("Looting bag").first().isPresent() && !Inventory.full()) {
            PolarAPI.pickupItem("Looting bag");
        } else if (Inventory.full() && TileItems.search().withName("Looting bag").first().isPresent()) {
            PolarAPI.eatFood(config.foodItemName());
        }
        if (TileItems.search().withName("Ecumenical key").first().isPresent() && !Inventory.full()) {
            PolarAPI.pickupItem("Ecumenical key");
        } else if (Inventory.full() && TileItems.search().withName("Ecumenical key").first().isPresent()) {
            PolarAPI.eatFood(config.foodItemName());
        }
        if (currentMonster != null) {
            if (PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {// || currentMonster.getNpcName().equals("Black dragon") && config.doKBD() && isInDragonLocalArea()) {
                loot();
            }
        }
        if (PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {
            if (TileObjects.search().nameContains("stand").atArea(currentMonster.getNpcWorldArea()).first().isPresent()) {
                PolarAPI.interactObj("Cannon stand", "Pick-up", true);
            }
        }
        openRandomTabAtRandomIntervals();
        randomCameraDirection();
        if (InventoryUtil.nameContainsNoCase("Looting bag").withAction("Open").first().isPresent()) {
            InventoryUtil.useItemNoCase("Looting bag", "Open");
        }
        if (PolarAPI.isPlayerInArea(corp) && !exitCaveWidgetVisible()) {
            if (TileObjects.search().withName("Cave exit").first().isPresent()) {
                TileObjectInteraction.interact(679, "Exit");
            }
        }
        shutTheFuckUp();
        handleRun(10, 33);
        updateCameraYawEveryRandomTicks();
        if (PolarAPI.isPlayerInArea(corp)) {
            exitCaveIfPossible();
        }
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }
        if (currentMonster != null && PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {// config.flickPray() && currentMonster.getNpcName().equals("Black dragon") && config.doKBD() && isInDragonLocalArea()) {
            // Fetch the prayer type from the configuration for the current monster
            DefensivePrayerType configuredPrayer = getPrayerTypeForMonster(currentMonster);
            prayersToFlick[0] = getDefensivePrayer(configuredPrayer);

            OffensivePrayerType offensiveType = getOffensivePrayerTypeForMonster(currentMonster);
            prayersToFlick[1] = getOffensivePrayer(offensiveType);

            handlePrayers(pluginRunning);
        }
        Widgets.search().withId(27918346).first().ifPresent(widget -> {
            Widgets.search().withId(27918346).hiddenState(false).first().ifPresent(MouseUtil::ClickWidget);
            log.info("Clicked 'Confirm' to finalize the cancellation of the task.");
        });
        state = getNextState();
        handleState(state);
        // logDetails();
    }

    private Prayer getDefensivePrayer(DefensivePrayerType prayerType) {
        switch (prayerType) {
            case PROTECT_MELEE:
                return Prayer.PROTECT_FROM_MELEE;
            case PROTECT_MAGIC:
                return Prayer.PROTECT_FROM_MAGIC;
            case PROTECT_MISSILES:
                return Prayer.PROTECT_FROM_MISSILES;
            case NONE:
            default:
                return null;
        }
    }

    private Prayer getOffensivePrayer(OffensivePrayerType prayerType) {
        switch (prayerType) {
            case MELEE:
                return getMeleePrayer(config.meleeOffensivePrayer());
            case MAGIC:
                return getMagicPrayer(config.magicOffensivePrayer());
            case RANGED:
                return getRangedPrayer(config.rangedOffensivePrayer());
            case NONE:
            default:
                return null;
        }
    }

    public State getNextState() {
        if (breakHandler.shouldBreak(this) && PolarAPI.isPlayerInArea(edge)) {
            return State.BREAKING;
        }
        if (PolarAPI.isMoving() || PolarAPI.isAnimating()) {
            return State.ANIMATE;
        }
        if (!pluginRunning) {
            return State.IDLE;
        }
        if (currentMonster != null) {
            if (isReadyToSlay() && PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {//|| isReadyToSlay() && isInDragonLocalArea() && config.doKBD() && !PolarAPI.isPlayerInArea(edge)) {
                return State.ATTACKING;
            }
        }
        if (getSlayerTaskSize() < 1 && PolarAPI.isPlayerInArea(edge) && !PolarAPI.isPlayerInArea(slayerMasterHouse)) {
            return State.GO_SLAYER_MASTER;
        }

        if (getSlayerTaskSize() < 1 && PolarAPI.isPlayerInArea(slayerMasterHouse)) {
            return State.GETTING_TASK;
        }
        if (currentMonster != null) {
            if (PolarAPI.isPlayerInArea(slayerMasterHouse) && currentMonster.isSkipTask()) {
                return State.SKIP_TASK;
            }
        }
        if (PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {
            if (currentMonster == null && !PolarAPI.isPlayerInArea(edge) && !PolarAPI.isPlayerInArea(edgeBanky)) {
                return State.RETURNING;
            }
        }
        if (currentMonster != null && !isReadyToSlay() && !PolarAPI.isPlayerInArea(edgeBanky)) {
            if ((PolarAPI.isPlayerInArea(edge) || PolarAPI.isPlayerInArea(slayerMasterHouse))) {
                return State.GOING_TO_BANK;
            }
        }
        if (shouldGetCannon && !PolarAPI.isInWilderness() && Inventory.getEmptySlots() >= 4 && !PolarAPI.isPlayerInArea(nulodionArea)) {
            return State.GO_NULODION;
        }
        if (shouldGetCannon && PolarAPI.isPlayerInArea(nulodionArea) && Inventory.getEmptySlots() >= 4) {
            return State.RETRIEVE_CANNON;
        }

        if (PolarAPI.isPlayerInArea(edge) && !isReadyToSlay() && hopTimer < 2 && PolarAPI.runEnergy() < 60 || PolarAPI.isPlayerInArea(edge) && isReadyToSlay() && hopTimer < 2 && PolarAPI.runEnergy() < 60) {
            return State.ENERGY;
        }

        if (PolarAPI.isPlayerInArea(edgeBanky) && !isReadyToSlay() && hopTimer < 2 && PolarAPI.runEnergy() > 60) {
            return State.CHECK_RESTOCK;
        }

        if (currentMonster != null && PolarAPI.isPlayerInArea(edge) && isReadyToSlay() && !currentMonster.isUseTraversalItem()) {
            return State.GO_DITCH;
        }
        if (currentMonster != null && PolarAPI.isPlayerInArea(edge) && isReadyToSlay() && currentMonster.isUseTraversalItem()) {
            return State.TELE_TO_AREA;
        }
        if (currentMonster != null && (PolarAPI.isPlayerInArea(d) || PolarAPI.isPlayerInArea(dd)) && isReadyToSlay()) {
            return State.HOP_DITCH;
        }
        if (currentMonster != null && (!PolarAPI.isPlayerInArea(d) || !PolarAPI.isPlayerInArea(dd)) && isReadyToSlay() && !PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {
            return State.TRAVERSE;
        }
        return State.IDLE; // Default state if none of the conditions are met
    }

    private void handleState(State state) {
        switch (state) {
            case ANIMATE:
                break;
            case LOOTING:
                log.info("state: Looting");
                loot();
                break;
            case BREAKING:
                log.info("state: Breaking");
                goBreak();
                break;
            case SKIP_TASK:
                log.info("state: Skipping task.. ew");
                skipTask();
                break;
            case TRAVERSE:
                log.info("state: Traversing");
                walkToTask();
                break;
            case GO_DITCH:
                log.info("state: Go Ditch");
                ditch();
                break;
            case GO_NULODION:
                log.info("state: Going to nulodion");
                goNulodion();
                break;
            case RETRIEVE_CANNON:
                log.info("state: Retrieve cannon");
                talkToNul();
                break;
            case HOP_DITCH:
                log.info("state: Hop Ditch");
                hopDitch();
                break;
            case ATTACKING:
                log.info("state: Attacking Monster");
                if (currentMonster != null && PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {// || config.doKBD() && currentMonster != null && isInDragonLocalArea()) {
                    if (currentMonster.getNpcName().equals("Revenant goblin")) {
                        attackRevenants();
                            /* case "Black dragon":
                            if (config.doKBD()) {
                                attackKBD();
                                log.info("Attacking KBD!");
                            }
                            break;*/
                    } else {
                        attackMonster();
                        log.info("Attacking normal monster!");
                    }
                }
                break;
            case GO_SLAYER_MASTER:
                log.info("state: Going Slayer Master");
                goSlayerMaster();
                break;
            case RETURNING:
                log.info("state: Returning");
                teleportToEdge();
                break;
            case GETTING_TASK:
                log.info("state: Getting Task");
                getTask();
                break;
            case GOING_TO_BANK:
                log.info("state: Going Bank");
                goToBank();
                break;
            case TELE_TO_AREA:
                log.info("state: Tele to area");
                walkToTask();
                break;
            case CHECK_RESTOCK:
                log.info("state: Check Restock");
                checkAndRestock();
                break;
            case ENERGY:
                log.info("state: Energy");
                sipStam();
                break;
            case TIMEOUT:
                timeout--;
                break;
            default:
                break;
        }
    }

    private void sipStam() {
            if (currentMonster == null) {
                log.warn("No current monster task set. Cannot restock.");
                return;
            }
            switch (stamProgress) {

                case 0:
                    log.info("Withdrawing Stage BEGIN: Opening bank.");
                    if (PolarAPI.isPlayerInArea(edge) && !isBankOpen()) {
                            PolarAPI.walk(edgeBank);
                        ensureBankIsOpen();
                        PolarAPI.depositAll();
                        timeout = tickDelay();
                    } else {
                        stamProgress++;  // Move to next stage
                    }
                    break;
                case 1:
                    if (isBankOpen()) {
                        if (PolarAPI.runEnergy() < 60) {
                            PolarAPI.depositAll();
                            PolarAPI.withdrawNoCase("Stamina potion(");
                            PolarAPI.closeShop();  // Close the bank after handling food
                            log.info("Stam Stage 1: Withdraw Stamina.");
                            stamProgress++;
                        }
                    } else {
                        ensureBankIsOpen();
                    }
                    break;
                case 2:
                    if (isBankOpen()) {
                        if (PolarAPI.runEnergy() < 60) {
                            PolarAPI.drinkPotion("Stamina potion(1)");
                            log.info("Stam 2: Sip Stamina.");
                            stamProgress = 0;
                        } else {
                            PolarAPI.closeShop();
                        }
                    }
                    break;
                default:
                    log.warn("Unexpected restockProgress value: {}", stamProgress);
                    stamProgress = 0;  // Reset on unexpected value to avoid getting stuck.
                    break;
            }
        }

    private void goNulodion() {
        PolarAPI.walk(nulodionDoor);
    }
    private void talkToNul() {
        int currentWorld = client.getWorld();
        if (shouldGetCannon) {
            // Only hop if no hop has been attempted yet
            if (!initialHopAttempted) {
                hopWorlds(); // Perform the hop
                log.info("Hopping worlds, attempting to retrieve the cannon.");
                previousWorld = currentWorld; // Store the current world before the hop
                initialHopAttempted = true; // Flag that a hop has been attempted
                timeout = tickDelay(); // Set a delay to wait for the hop to complete
                return; // Exit the method to allow the hop to complete
            } else if (previousWorld == currentWorld) {
                // If we come back and the world has not changed after the initial hop attempt
                log.info("World hop attempt made, but world has not changed.");
                initialHopAttempted = false; // Reset for the next time we need to get a cannon
            } else {
                log.info("World has successfully changed from " + previousWorld + " to " + currentWorld);
                initialHopAttempted = false; // Reset the hop attempt flag as the hop has succeeded
            }
        }

        // Continue with interacting with Nulodion
        if (PolarAPI.isPlayerInArea(nulodionDoorArea) &&
                TileObjects.search().withId(3).withAction("Open").withinDistance(2).nearestToPlayer().isPresent() &&
                !PolarAPI.isPlayerInArea(insideNulodionArea)) {
            openDoorNul();
            log.info("Trying to open the door.");
        } else if (NPCs.search().withName("Nulodion").first().isPresent() &&
                PolarAPI.isPlayerInArea(insideNulodionArea) &&
                !PolarAPI.isPlayerInArea(nulodionDoorArea) &&
                (!isWidget1Visible() && !isWidget2Visible() && !isWidget3Visible())) {
            PolarAPI.interactNPC("Nulodion", "Talk-to", false);
            log.info("Talking to Nulodion..");
        } else {
            handleWidgetInteractions(); // Simplified widget handling logic into a separate line for clarity.
        }
    }

    private void handleWidgetInteractions() {
        // Handle widget interactions
        if (Widgets.search().withId(14221317).first().isPresent()) {
            WidgetPackets.queueResumePause(14221317, -1);
        }
        if (Widgets.search().withId(14352385).first().isPresent()) {
            WidgetPackets.queueResumePause(14352385, -1);
            WidgetPackets.queueResumePause(14352385, 4);
        }
        if (Widgets.search().withId(15138821).first().isPresent()) {
            WidgetPackets.queueResumePause(15138821, -1);
        }
        // If the cannon is found, stop retrieving it
        if (Inventory.search().nameContainsNoCase("Cannon base").first().isPresent()) {
            shouldGetCannon = false;
            teleForRestock();
            initialHopAttempted = false;
        }
    }


    private WorldArea getRevenantArea(String revenantName) {
        switch (revenantName) {
            case "Revenant imp":
                return revImpArea;
            case "Revenant goblin":
                return revGobArea;
            case "Revenant hobgoblin":
                return revHobArea;
            case "Revenant ork":
                return revOrkArea;
            default:
                return null;
        }
    }

    public boolean isWidget1Visible() {
        return Widgets.search().withId(14221317).first().isPresent();
    }

    // Method to check if Widget 2 is visible
    public boolean isWidget2Visible() {
        return Widgets.search().withId(14352385).first().isPresent();
    }

    // Method to check if Widget 3 is visible
    public boolean isWidget3Visible() {
        return Widgets.search().withId(15138821).first().isPresent();
    }

    private void attackKBD() {
        if (!PolarAPI.isAnimating() && !PolarAPI.isInteracting()) {
            NPCs.search()
                    .nameContains("King Black Dragon")
                    .nearestToPlayer()
                    .ifPresentOrElse(
                            npc -> {
                                log.info("Attacking King Black Dragon: {}", npc.getName());
                                NPCInteraction.interact(npc, "Attack");
                            },
                            () -> {
                                log.info("King Black Dragon not found, moving to center.");
                                WorldPoint kbdCenter = new WorldPoint(7232, 8256, client.getPlane()); // Adjust the Z coordinate if needed
                                PolarAPI.move(kbdCenter);
                            }
                    );
        }
    }


    private void attackRevenants() {
        // Define a mapping from configuration choices to revenant names
        Map<RevChoice, String> revenantMap = new HashMap<>();
        revenantMap.put(RevChoice.HOBGOBLIN, "Revenant hobgoblin");
        revenantMap.put(RevChoice.IMP, "Revenant imp");
        revenantMap.put(RevChoice.GOBLIN, "Revenant goblin");
        revenantMap.put(RevChoice.ORK, "Revenant ork");
        String targetRevenantName = revenantMap.get(config.revChoice());

        NPCs.search()
                .filter(npc -> npc.getName().equals(targetRevenantName) && !npc.isDead() && !npc.isInteracting())
                .nearestToPlayer()
                .ifPresent(npc -> {
                    log.info("Attacking configured revenant: {}", npc.getName());
                    PolarAPI.attackNPC(npc);
                    equipBracelets(currentMonster);
                });
    }


    private void loot() {
        if (Inventory.full()) {
            handleFood();
        } else {
            PolarAPI.pickupClosestTileItemAboveXWikiWithinArea(config.minLoot(), 15, currentMonster.getNpcWorldArea());
        }
    }

    private int getWildernessLvl() {
        Widget widget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
        if (widget == null || widget.isHidden()) {
            return 0;
        }
        Pattern pattern = Pattern.compile("Level:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(widget.getText());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    public void checkAndTeleportIfPlayerInArea() {
        wildyLvl = getWildernessLvl();  // Assuming getWildernessLvl() correctly fetches the wilderness level of the player
        if (wildyLvl > 30) {
            if (!PolarAPI.isInWilderness() || PolarAPI.isPlayerInArea(safeTeleWildy)) {
                log.warn("Above 30 wilderness and safe to teleport.");
                if (needsSafeTeleport()) {
                    performTeleport();
                }
            } else {
                log.warn("Above 30 wilderness. Can't teleport. RIP.");
                walkSafety(); // Consider moving to safety before showing the log message.
            }
            return;
        }

        if (currentMonster == null) {
            log.warn("No current monster task set, cannot perform area check.");
            if (wildyLvl <= 30 && !PolarAPI.isPlayerInArea(edge)) {
                teleForRestock();
            }
            return;
        }

        int detectionRadius = 6; // Consider making this a configurable parameter
        Player localPlayer = client.getLocalPlayer();

        for (Player player : client.getPlayers()) {
            if (player != null && !player.equals(localPlayer) && PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea()) && lastAttackedPlayer != null && isPkerUsingWeaponForEscape(lastAttackedPlayer)) {
                int distanceToPlayer = player.getWorldLocation().distanceTo(localPlayer.getWorldLocation());
                if (!currentMonster.getNpcName().equals("Revenant goblin") && distanceToPlayer <= detectionRadius) {
                    handlePlayerDetection(detectionRadius);
                    return; // Ensures the method exits after handling a detection
                } else if (currentMonster.getNpcName().equals("Revenant goblin")) {
                    handleRevenantsDetection(player, config.revChoice());
                    return; // Ensures the method exits after handling revenant-specific logic
                }
            }
        }
    }

    private void handlePlayerDetection(int detectionRadius) {
        log.info("Another player detected within {} tiles. Handling cannon and teleporting out.", detectionRadius);
        Optional<TileObject> cannon = TileObjects.search()
                .nameContainsNoCase("dwarf multicannon")
                .withinDistancetoPoint(PolarAPI.getPlayerLoc(), 7)
                .nearestToPlayer();

        if (cannon.isPresent()) {
            if (Inventory.getEmptySlots() < 4) {
                PolarAPI.dropItem(config.foodItemName(), 4); // Dropping items to make space for cannon
            }
            if (Inventory.getEmptySlots() >= 4) {
                TileObjectInteraction.interact(cannon.get(), "Pick-up");
                PolarAPI.sendClientMessage("Picked up cannon");
            }
            teleForRestock(); // Always teleporting for restock after picking up cannon
            hopTimer = 21; // Resetting the hop timer
        } else {
            log.info("No cannon found, teleporting for restock.");
            teleForRestock();
            hopTimer = 21; // Resetting the hop timer when no cannon is found
        }
    }


    private void handleRevenantsDetection(Player player, RevChoice revChoice) {
        WorldArea specificArea = getSpecificAreaForRevChoice(revChoice);
        int specificDistance = getSpecificDistanceForRevChoice(revChoice);

        if (player.getWorldLocation().distanceTo(specificArea) <= specificDistance) {
            log.info("Player detected near Revenants area configured for {}", revChoice);
            teleForRestock();
        }
    }
    private WorldArea getSpecificAreaForRevChoice(RevChoice revChoice) {
        switch (revChoice) {
            case GOBLIN:
                return revGobArea;
            case HOBGOBLIN:
                return revHobArea;
            case IMP:
                return revImpArea;
            case ORK:
                return revOrkArea;
            default:
                return null;
        }
    }

    private int getSpecificDistanceForRevChoice(RevChoice revChoice) {
        return 3;
    }
    // Define the local points
    LocalPoint southWest = new LocalPoint(5184, 6208);
    LocalPoint northEast = new LocalPoint(9152, 10176);

    // Create a method to check if the player is within this local area
    public boolean isInDragonLocalArea() {
        return ArrayUtils.contains(this.client.getMapRegions(), 9033);
    }

    private void walkToTask() {
        if (currentMonster == null) {
            log.warn("No current monster task set, cannot walk to task.");
            return;
        }

        String travelItemId = currentMonster.getTraversalItemName();
        WorldArea monsterArea = currentMonster.getNpcWorldArea();
        WorldPoint outsidePoint = currentMonster.getOutsideWorldPoint();
        WorldPoint cannonPoint = currentMonster.getCannonPoint();
        boolean useTraversalItem = currentMonster.isUseTraversalItem();

        // Monster sets
        Set<String> caveMonsters = new HashSet<>(Arrays.asList("Ice giant", "Ankou", "Greater Nechryael", "Hellhound", "Jelly", "Dust devil", "Greater demon", "Black demon", "Lesser demon", "Abyssal demon", "Black dragon"));
        Set<String> revMonsters = new HashSet<>(Arrays.asList("Revenant goblin"));
        Set<String> kbdMonsters = new HashSet<>(Arrays.asList("Black dragon"));
        Set<String> gwdMonsters = new HashSet<>(Arrays.asList("Bloodveld", "Aviansie"));
        Set<String> dungMonsters = new HashSet<>(Arrays.asList("Chaos druid", "Deadly red spider", "Earth warrior"));
        Set<String> outsideGamesNeckMonsters = new HashSet<>(Arrays.asList("Ent"));
        // Use traversal item if applicable
        if (useTraversalItem && travelItemId != null && InventoryUtil.getItemNameContains(travelItemId).isPresent() && PolarAPI.isPlayerInArea(edgeBanky)) {// || (config.doKBD()) && InventoryUtil.hasItem("Burning amulet(5)")) {
            useTraversalItem((travelItemId));
            log.info("useTravelItem is activated.");
            timeout = 5;
            return;
        }
        if (outsideGamesNeckMonsters.contains(currentMonster.getNpcName())) {
            handleOutsideWithGamesNeck();
            return;
        }
        // Handle specific monsters
        if (revMonsters.contains(currentMonster.getNpcName())) {
            handleRevMonsters();
            return;
        }
        if (gwdMonsters.contains(currentMonster.getNpcName())) {
            if (!PolarAPI.isPlayerInArea(edge)) {
                if (PolarAPI.isPlayerInArea(banditCampTeleSpawn)) {
                    PolarAPI.walk(gwdCavePoint);
                } else if (PolarAPI.isPlayerInArea(gwdCaveEntrance)) {
                    enterGWDCave();
                } else if (PolarAPI.isPlayerInArea(gwdObstacleArea)) {
                    moveBoulder();
                } else if (PolarAPI.isPlayerInArea(creviceEntrance)) {
                    useCrevice();
                } else if (!PolarAPI.isPlayerInArea(monsterArea)) {
                    PolarAPI.walk(outsidePoint);
                }
            }
            return;
        }
        if (dungMonsters.contains(currentMonster.getNpcName())) {
            if (PolarAPI.isPlayerInArea(edge)) {
            } else if (PolarAPI.isPlayerInArea(edgeDungeonTrapArea)) {
                useTrap();
                log.info("using trap");
                } else if (PolarAPI.isPlayerInArea(edgeDungeonBelow)) {
                    PolarAPI.walk(edgeDungeonGatePoint);
                    log.info("moving to gate");
                } else if (PolarAPI.isPlayerInArea(edgeDungeonGate1) && TileObjects.search().withAction("Open").withinDistance(6).first().isPresent()) {
                    useGayte();
                    log.info("using gate");
                } else if (PolarAPI.isPlayerInArea(edgeDungeonFAR)) {
                    PolarAPI.move(edgeDungeonGate2Point);
                    log.info("moving to gate 2");
                } else if (PolarAPI.isPlayerInArea(edgeDungeonGate2)) {
                    useGayte2();
                    log.info("using gate 2");
                } else if (!PolarAPI.isPlayerInArea(monsterArea) && PolarAPI.isPlayerInArea(edgeDungeonWildy)) {
                PolarAPI.move(outsidePoint);
                log.info("moving to monster dungeon area");
                }
            return;
        }
/*        if (kbdMonsters.contains(currentMonster.getNpcName()) && config.doKBD()) {
            handleKbdMonsters();
            return;
        }*/

        if (caveMonsters.contains(currentMonster.getNpcName())) {
            handleCaveMonsterRouting();
            return;
        }
        WorldArea unda = new WorldArea(3092, 9864, 48, 53, 0);

        if (!PolarAPI.isPlayerInArea(monsterArea)) {
            log.info("Walking to general outside task location for {}: {}", currentMonster.getNpcName(), outsidePoint);
            PolarAPI.walk(outsidePoint);
        }
    }

    private void handleKbdMonsters() {
        if (PolarAPI.isPlayerInArea(outsidelavamaze)) {
            log.info("At lava maze, walking to gate entrance for {}", currentMonster.getNpcName());
            PolarAPI.walk(kbdGatePoint);
        } else if (PolarAPI.isPlayerInArea(kbdGateArea)) {
            // Check if gate is present and needs to be opened
            Optional<TileObject> gate = TileObjects.search().withAction("Open").atArea(kbdGateArea).first();
            if (gate.isPresent()) {
                log.info("Gate found at KBD area, attempting to open.");
                openGates();
            } else {
                log.warn("No gate found to open in KBD area, checking ladder directly.");
                enterKBDLadder();
            }
        }
        if (PolarAPI.isPlayerInArea(spiderKbdArea)) {
            useLever();
            log.info("Using lever in KBD spider area.");
        }
    }


    private void handleRevMonsters() {
        if (PolarAPI.isPlayerInArea(banditCampTeleSpawn)) {
            log.info("At bandit camp, walking to rev cave for {}", currentMonster.getNpcName());
            PolarAPI.walkRandom(revPoint);
        } else if (PolarAPI.isPlayerInArea(revHole)) {
            enterCavern();
        } else if (PolarAPI.isPlayerInArea(southRevs)) {
            walkToConfiguredRevLocation();
        }
    }

    private void walkToConfiguredRevLocation() {
        // Define points for different Revenants based on the configuration
        WorldPoint impPoint = new WorldPoint(3204, 10072, 0);
        WorldPoint gobPoint = new WorldPoint(3230, 10064, 0);
        WorldPoint hobPoint = new WorldPoint(3242, 10096, 0);
        WorldPoint orkPoint = new WorldPoint(3215, 10092, 0); // Assuming 'orkPoint' needs to be defined similar to others

        // Select the point based on the revenant type in config
        switch (config.revChoice()) {
            case IMP:
                log.info("Walking to imp point.");
                PolarAPI.move(impPoint);
                break;
            case GOBLIN:
                log.info("Walking to goblin point.");
                PolarAPI.move(gobPoint);
                break;
            case HOBGOBLIN:
                log.info("Walking to hobgoblin point.");
                PolarAPI.move(hobPoint);
                break;
            case ORK:
                log.info("Walking to ork point.");
                PolarAPI.move  (orkPoint);
                break;
            default:
                log.warn("Uh oh, issue moving to the rev point. biggs this is for walking to configured rev location.");
                break;
        }
    }

    private void handleCaveMonsterRouting() {
        // This method handles routing for monsters that are typically found in caves
        if (PolarAPI.isPlayerInArea(outsideCorp)) {
            log.info("At outsideCorp, walking to slayer cave for {}", currentMonster.getNpcName());
            PolarAPI.sendClientMessage("Walking to slayer cave for "+ currentMonster.getNpcName() + " ");
            PolarAPI.walkRandom(cavePoint);
        } else if (PolarAPI.isPlayerInArea(wildySlayerCaveEntrance) && !PolarAPI.isPlayerInArea(outsideCorp) && !PolarAPI.isPlayerInArea(wildySlayerCave)) {
            log.info("At wildySlayerCaveEntrance, not in cave, entering cave for {}", currentMonster.getNpcName());
            PolarAPI.sendClientMessage("Entering slayer cave");
            enterCave();
        } else if (PolarAPI.isPlayerInArea(wildySlayerCave)) {
            WorldPoint cannonPoint = currentMonster.getCannonPoint();
            if (cannonPoint != null && (InventoryUtil.hasItem("Cannon base") || InventoryUtil.hasItem("Cannon base (or)"))) {
                log.info("Cannon setup possible, moving to cannon point for {}", currentMonster.getNpcName());
                PolarAPI.walk(cannonPoint);
                PolarAPI.sendClientMessage("Walking to cannon point for "+ currentMonster.getNpcName() + " ");
            } else if (!PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea()) && (!InventoryUtil.hasItem("Cannon base") || !InventoryUtil.hasItem("Cannon base (or)"))) {
                log.info("In wildySlayerCave but not in specific monster area, walking randomly to inside cave point for {}", currentMonster.getNpcName());
                PolarAPI.walk(currentMonster.getInsideCavePoint());
                PolarAPI.sendClientMessage("Walking to cave point for "+ currentMonster.getNpcName() + " ");
            }
        }
    }


    private void handleOutsideWithGamesNeck() {
        if (PolarAPI.isPlayerInArea(outsideCorp)) {
            log.info("At outsideCorp, walking to {}", currentMonster.getNpcName());
            PolarAPI.sendClientMessage("Walking to "+ currentMonster.getNpcName() + " ");
            PolarAPI.walkRandom(currentMonster.getOutsideWorldPoint());
        }
    }


    private boolean shouldEatFood() {
        int hitpoints = getHitpoints();
        return (hitpoints <= config.healthLowAmount() || PolarAPI.isPlayerInArea(edgeBanky) && hitpoints <= client.getRealSkillLevel(Skill.SLAYER) * 0.75);
    }

    private void ditch() {
        PolarAPI.closeAmountInterface();
        restockProgress = 0;
        if (!PolarAPI.isMoving() && !PolarAPI.isAnimating()) {
            Set<String> dung = new HashSet<>(Arrays.asList("Deadly red spider", "Chaos druid", "Earth warrior"));
            Set<String> ddWalkers = new HashSet<>(Arrays.asList("Mammoth", "Hill giant"));
            List<WorldPoint> ditchPoints = Arrays.asList(ditchPoint, ditchPoint2, ditchPoint3);

            if (currentMonster != null) {
                if (dung.contains(currentMonster.getNpcName())) {
                    PolarAPI.walk(edgeDungeonTrapPoint);
                    log.info("Walking to Edge Dungeon Trapdoor.");
                    useTrap();
                } else if (ddWalkers.contains(currentMonster.getNpcName())) {
                    PolarAPI.walk(currentMonster.getInsideWorldPoint());
                    log.info("Walking to a designated point outside ditch for dd walkers' area.");
                } else {
                    Random random = new Random();
                    int randomIndex = random.nextInt(ditchPoints.size());
                    WorldPoint randomDitchPoint = ditchPoints.get(randomIndex);
                    PolarAPI.walkRandomer(randomDitchPoint);
                    log.info("Walking to the ditch at a random point.");
                }
            }
        }
    }


    private void hopDitch() {
        PolarAPI.closeAmountInterface();
        if (PolarAPI.isPlayerInArea(d) || PolarAPI.isPlayerInArea(dd)) {
            if (!PolarAPI.isAnimating()) {
                TileObjects.search()
                        .withinDistance(5)
                        .withAction("Cross")
                        .nearestToPlayer()
                        .ifPresentOrElse(
                                obj -> TileObjectInteraction.interact(obj, "Cross"),
                                () -> PolarAPI.sendClientMessage("No visible wildy ditch.")
                        );
                ObjectUtil.getNearest(23271)
                        .ifPresentOrElse(
                                obj -> TileObjectInteraction.interact(obj, "Cross"),
                                () -> PolarAPI.sendClientMessage("Can't find wildy ditch.")
                        );
            }
        }
    }

    private void getTask() {
        PolarAPI.interactNPC("Krystilia", "Assignment", false);
    }

    private boolean tasksCancelWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withId(14352385).first();
        return widget.isPresent() && !widget.get().isHidden();
    }

    private boolean continueToCancelWidgetVisible() {
        Optional<Widget> widget = Widgets.search().withId(15138821).first();
        return widget.isPresent() && !widget.get().isHidden();
    }

    private void skipTask() {
        clientThread.invoke(() -> {
            if (continueToCancelWidgetVisible() && !tasksCancelWidgetVisible()) {
                Widgets.search().withId(15138821).first().ifPresent(widget -> {
                    WidgetPackets.queueResumePause(15138821, 1);
                    log.info("Clicked on 'Continue' to go to the next screen!");
                });
            }
            if (tasksCancelWidgetVisible()) {
                Widgets.search().withId(14352385).first().ifPresent(widget -> {
                    WidgetPackets.queueResumePause(14352385, 1);
                    log.info("Clicked on 'Cancel task'!");
                });
            }
        });
    }
//widget 27918348 childID:6 for going to the cancel screen
    //widget 27918362 for cancel
    //widget 27918343 for confirming cancel

    private void goToBank() {
        PolarAPI.walkRandom(edgeBank);
        log.info("Walking to Edgeville Bank.");
        bagFull = false;
    }

    private void goSlayerMaster() {
        if (PolarAPI.isPlayerInArea(edge)) {
            PolarAPI.walkRandom(slayerMaster);  // Coordinates of Slayer Master
            System.out.println("Walking towards the Slayer Master as no current task is assigned.");
        }
    }

    private void attackMonster() {
        if (!PolarAPI.isPlayerInArea(edge)) {
            if (currentMonster == null) {
                log.warn("No monster task set, cannot initiate attack.");
                return;
            }

            WorldArea monsterArea = currentMonster.getNpcWorldArea();
            if (!PolarAPI.isPlayerInArea(monsterArea)) {
                log.warn("Not in the correct area for the monster: {}", currentMonster.getNpcName());
                return;
            }

            if (!isReadyToSlay()) {
                log.warn("Not ready to slay. Ensure all conditions are met before attacking.");
                return;
            }

            boolean dwarfMulticannon = ObjectUtil.getNearest("dwarf multicannon", false).isPresent();
            int cannonAmmo = client.getVarpValue(VarPlayer.CANNON_AMMO);
            log.info("Dwarf Multicannon present: {}, Cannon Ammo: {}", dwarfMulticannon, cannonAmmo);
            if (config.debugMode()) {
                PolarAPI.sendClientMessage("Dwarf Multicannon present: "+dwarfMulticannon+", Cannon Ammo: {} "+ cannonAmmo+" ");
            }

            if (shouldUseCannon(currentMonster)) {
                if (!dwarfMulticannon) {
                    placeCannon();
                    log.info("Placing cannon for monster: {}", currentMonster.getNpcName());
                }

                if (cannonAmmo == 30 || cannonAmmo <= 11) {
                    fireCannon();
                    equipBracelets(currentMonster);
                    log.info("Firing cannon at ammo count: {}", cannonAmmo);
                } else {
                    log.info("Cannon ammo sufficient for engagement.");
                }

                // Repair the cannon if needed
                TileObjects.search()
                        .nameContainsNoCase("broken multicannon")
                        .atArea(monsterArea)
                        .nearestToPlayer()
                        .ifPresent(obj -> {
                            TileObjectInteraction.interact(obj, "Repair", "repair");
                            log.info("Repairing cannon!");
                        });

                if (cannonAmmo > 11 && cannonAmmo != 30) {
                    log.info("Ammo is sufficient, proceed to attack directly while monitoring cannon.");
                    attackDirectly();
                    equipBracelets(currentMonster);
                } else {
                    log.info("Ammo too low, focus on cannon management before direct engagement.");
                }
            } else {
                attackDirectly();
                equipBracelets(currentMonster);
            }
        }
    }

    private void attackDirectly() {
        if (NPCs.search().nameContains("trunk").first().isPresent() && !PolarAPI.isAnimating() && Objects.equals(currentMonster.getNpcName(), "Ent")) {
            PolarAPI.interactNPC("Ent trunk", "Chop", true);
            log.info("Trying to chop Ent!");
            if (config.debugMode()) {
                PolarAPI.sendClientMessage("Attempting to chop ent. fuck ents.");
            }
        } else {
            if (!PolarAPI.isAnimating()) {
                PolarAPI.interactNPC(currentMonster.getNpcName(), "Attack", true);
                log.info("Attacking monster directly: {}", currentMonster.getNpcName());
                equipBracelets(currentMonster);
                if (config.debugMode()) {
                    PolarAPI.sendClientMessage("Attacking monster directly.");
                }
            }
        }
    }







    private void teleportToEdge() {
        Set<String> obstacleMonsters = new HashSet<>(Arrays.asList("Moss giant", "Ice warrior", "Hill giant", "Black knight"));
        String currentMonsterName = currentMonster.getNpcName();
        log.info("Current monster: {}", currentMonsterName);

        if (PolarAPI.isPlayerInArea(edge) || PolarAPI.isPlayerInArea(edgeBanky)) {
            log.info("Already in edge or edgeBanky area, no teleport needed.");
            return;
        }

        Optional<TileObject> cannon = TileObjects.search()
                .nameContainsNoCase("Dwarf multicannon")
                .withinDistancetoPoint(PolarAPI.getPlayerLoc(), 9)
                .nearestToPlayer()
                .stream().findFirst();

        if (cannon.isPresent()) {
            handleCannon(cannon.get());
        } else {
            log.info("No cannon found, proceeding with teleport.");
            maybeTeleport(currentMonsterName);
        }
    }

    private void handleCannon(TileObject cannon) {
        if (Inventory.getEmptySlots() < 4) {
            handleFood();
        }
        if (Inventory.getEmptySlots() >= 4) {
            TileObjectInteraction.interact(cannon, "Pick-up");
            log.info("Picking up cannon before teleporting.");
        }
    }

    private void maybeTeleport(String monsterName) {
        Set<String> obstacleMonsters = new HashSet<>(Arrays.asList("Moss giant", "Ice warrior", "Hill giant", "Black knight"));
        if (obstacleMonsters.contains(monsterName) && !PolarAPI.isPlayerInArea(safeTeleWildy)) {
            log.info("Walking to obstacle point for {}", monsterName);
            PolarAPI.walkRandom(currentMonster.getObstacleWorldPoint());
        } else {
            performTeleport();
        }
    }

    private void performTeleport() {
        if (config.TELEPORT_OPTIONS() == TeleportOptions.AMULET_OF_GLORY) {
            // First, try to use the amulet from the equipment
            Equipment.search().nameContains("Amulet of glory(").first().ifPresentOrElse(
                    glory -> {
                        EquipmentUtil.useItem(glory, "Edgeville");  // Assuming "Edgeville" is a valid action
                        log.info("Using Amulet of Glory from equipment to teleport to Edgeville.");
                    },
                    () -> {
                        // If not found in equipment, check in the inventory
                        Inventory.search().nameContains("glory(").first().ifPresentOrElse(
                                glory -> {
                                    InventoryUtil.useItemNoCase(String.valueOf(glory), "Wear");  // Assuming "Edgeville" is a valid action for the inventory item as well
                                    log.info("equipping Amulet of Glory from inventory to teleport to Edgeville.");
                                },
                                () -> log.warn("No Amulet of Glory with charges found in equipment or inventory.")
                        );
                    }
            );
        }
    }


    private void walkSafety() {
        PolarAPI.walkRandom(safePoint);
    }

    private void checkAndRestock() {
        if (currentMonster == null) {
            log.warn("No current monster task set. Cannot restock.");
            return;
        }
        switch (restockProgress) {

            case 0:
                log.info("Withdrawing Stage BEGIN: Opening bank.");
                if (PolarAPI.isPlayerInArea(edgeBanky) && !isBankOpen()) {
                    ensureBankIsOpen();
                    PolarAPI.depositWornItems();
                    timeout = tickDelay();
                } else {
                    restockProgress++;  // Move to next stage
                }
                break;
            case 1:
                PolarAPI.depositWornItems();
                PolarAPI.depositAll();
                log.info("Withdrawing Stage 1: Depositing all items.");
                timeout = tickDelay();
                restockProgress++;
                break;

            case 2:
                log.info("Withdrawing Stage 2: Withdrawing Gear for " + currentMonster.getNpcName() + ".");
                AttackType gearType = getRequiredGear(currentMonster);  // Fetch the attack type for the monster
                String requiredGear = getRequiredGear(gearType);  // Fetch the gear string based on attack type
                withdrawGear(requiredGear);

                // Handle ammunition based on gear type
                if (requiredGear.contains("bow") && !requiredGear.contains("fae") && !requiredGear.contains("craw") && !requiredGear.contains("venator") && !requiredGear.contains("weaver") && !requiredGear.contains("cross")) {
                    // Withdraw arrows for bows
                    int arrowAmount = config.arrowAmount();
                    String arrowType = config.AmmoType().getDisplayName();
                    PolarAPI.withdraw(arrowType, arrowAmount);
                    log.info("Arrow Ammo withdrawn: - {} - {}!", arrowAmount, arrowType);
                } else if (requiredGear.contains("cross")) {
                    // Withdraw bolts for crossbows
                    int boltAmount = config.arrowAmount();  // Assuming bolt amount is the same as arrow amount
                    String boltType = config.BoltAmmoType().getDisplayName();
                    PolarAPI.withdraw(boltType, boltAmount);
                    log.info("Bolt Ammo withdrawn: - {} - {}!", boltAmount, boltType);
                }
                if (Objects.equals(currentMonster.getNpcName(), "Ent")) {
                    PolarAPI.withdrawNoCase(" axe");
                }
                timeout = tickDelay();
                restockProgress++;
                break;
            case 3:
                log.info("Restocking Stage 3: Equipping Gear for " + currentMonster.getNpcName() + ".");
                gearType = getRequiredGear(currentMonster);  // Fetch the attack type again
                requiredGear = getRequiredGear(gearType);  // Get the gear string based on attack type
                PolarAPI.swapGear(requiredGear);
                timeout = 5;
                restockProgress++;
                break;
            case 4:
                log.info("Restocking Stage 4: Opening bank.");
                ensureBankIsOpen();
                timeout = tickDelay();
                restockProgress++;
                break;
            case 5:
                if (config.useLootingBagGangstaAssMothaFuckingLootingBagBitchYouAintOnShitDawgISwearToGawdFrFrHomieIfYouReadThisYouProbablyHaveDowNSyndromexDxDMoron()) {
                    log.info("Withdrawing Stage 5: Looting bag");
                    PolarAPI.withdraw("Looting bag", 1);
                    timeout = 5;
                    restockProgress++;
                    break;
                }
            case 6:
                if (config.useLootingBagGangstaAssMothaFuckingLootingBagBitchYouAintOnShitDawgISwearToGawdFrFrHomieIfYouReadThisYouProbablyHaveDowNSyndromexDxDMoron()) {
                    if (isBankOpen()) {
                        BankInventoryInteraction.useItem(22586, "View");
                    }
                    log.info("Withdrawing Stage 6: View Looting Bag.");
                }
                timeout = tickDelay;
                restockProgress++;
                break;
            case 7:
                if (config.useLootingBagGangstaAssMothaFuckingLootingBagBitchYouAintOnShitDawgISwearToGawdFrFrHomieIfYouReadThisYouProbablyHaveDowNSyndromexDxDMoron()) {
                    if (LBDepositWidgetVisible() && isBankOpen()) {
                        depositLootIfPossible();
                    }
                    log.info("Withdrawing Stage 7: Deposit Looting Bag.");
                }
                timeout = 3;
                restockProgress++;
                break;
            case 8:
                if (config.useLootingBagGangstaAssMothaFuckingLootingBagBitchYouAintOnShitDawgISwearToGawdFrFrHomieIfYouReadThisYouProbablyHaveDowNSyndromexDxDMoron()) {
                    if (LBWidgetVisible() && isBankOpen()) {
                        BankInventoryInteraction.useItem("Looting bag", "View");
                    }
                    if (LBDepositWidgetVisible() && isBankOpen()) {
                        depositLootIfPossible();
                    }
                    if (LBCloseWidgetVisible() && isBankOpen()) {
                        closeLB();
                    }
                    log.info("Withdrawing Stage 8: Finish Looting Bag.");
                }
                timeout = 3;
                restockProgress++;
                break;
            case 9:
                log.info("Restocking Stage 9: Withdrawing slayer items and traversing method.");
                if ("dragon".contains(currentMonster.getNpcName())) {
                    PolarAPI.withdrawContainsNoCase("antifire", 2);
                }
                if (currentMonster.getRequiredSlayerItems() != null) {
                    for (int itemId : currentMonster.getRequiredSlayerItems()) {
                        if (BankUtil.getItemAmount(itemId) > 0) {  // Ensure the item is available
                            PolarAPI.withdrawNoCase(itemId, 1);
                            log.info("Withdrew 1 of item ID {}", itemId);
                            timeout = tickDelay();
                        } else {
                            log.warn("Item ID {} not available in bank.", itemId);
                        }
                    }
                }
                if (currentMonster.getTraversalItemName() != null) {
                    String itemIdToWithdraw = currentMonster.getTraversalItemName(); // Default to the regular traversing method ID

                  /*  // Check if the special case for KBD is applicable
                    if (config.doKBD() && "Black dragon".equalsIgnoreCase(currentMonster.getNpcName())) {
                        // Withdrawing specific item for Black dragon when KBD config is enabled
                            PolarAPI.withdrawContainsNoCase("burning amulet(5", 1);
                            log.info("Withdrew special item for Black Dragon: Item ID 21166");
                            timeout = tickDelay();*/
                    // Handle normal withdrawal for other monsters or when KBD is not the focus
                    PolarAPI.withdrawContainsNoCase(itemIdToWithdraw, 1);
                        log.info("Withdrew traversing method item ID {}", itemIdToWithdraw);
                        timeout = tickDelay();
                       // log.warn("Traversing method item ID {} not available in bank.", itemIdToWithdraw);
                    }



                restockProgress++;
                timeout = tickDelay();
                break;
            case 10:
                log.info("Withdrawing Stage 10: Withdrawing Cannon.");
                if (currentMonster != null && shouldUseCannon(currentMonster) && Bank.search().withNameNoCase("cannonball").first().isPresent()) {
                    withdrawCannonParts();
                } else {
                    ensureBankIsOpen();
                }
                timeout = tickDelay();
                restockProgress++;
                break;
            case 11:
                log.info("Withdrawing Stage 11: Withdrawing Bracelets.");
                withdrawBraceletsForMonster(currentMonster);
                timeout = tickDelay();
                restockProgress++;
                break;
            case 12:
                if (isBankOpen()) {
                    log.info("Restocking Stage 12: Handling prayer potions and boost potions.");
                    // Withdraw Prayer Potions
                    PrayerOptions selectedPrayerPotion = config.PRAYER_OPTIONS();  // Ensure you have this getter in your config
                    boolean potionFound = false;
                    for (int potionId : selectedPrayerPotion.getItemIds()) {
                        if (BankUtil.getItemAmount(potionId) > 0) {  // Check if the item is available in the bank
                            PolarAPI.withdrawNoCase(potionId, config.prayerPotAmount());
                            log.info("Withdrew {} of potion ID {}", config.prayerPotAmount(), potionId);
                            potionFound = true;
                            break;  // Exit after successfully finding and withdrawing the highest available dose
                        }
                    }
                    if (!potionFound) {
                        log.warn("No available doses found for the selected prayer potion type.");
                    }

                    // Withdraw Boost Potions
                    String boostPotionName = getBoostPotion(getRequiredGear(currentMonster));  // Fetch the boost potion based on the attack type
                    if (!boostPotionName.isEmpty()) {
                        PolarAPI.withdrawContainsNoCase(boostPotionName, config.boostPotAmount());  // Assume you need 2 potions
                        log.info("Withdrawing of {}", boostPotionName);
                    } else {
                        log.warn("Boost potion '{}' not available or not specified.", boostPotionName);
                    }

                    timeout = tickDelay();  // Adding a short delay to allow bank operations to complete
                    restockProgress++;
                }
                break;

            case 13:
                log.info("Withdrawing Stage 13: Withdrawing Teleport.");
                if (Equipment.search().nameContains("glory(").first().isEmpty()) {
                    if (config.TELEPORT_OPTIONS() == TeleportOptions.AMULET_OF_GLORY) {
                        PolarAPI.withdrawIfContainsSomeText("Glory(", 1);
                    } else {
                        ensureBankIsOpen();
                    }
                }
                timeout = tickDelay();
                restockProgress++;
                break;
            case 14:
                if (shouldUseCannon(currentMonster) && (Bank.search().nameContainsNoCase("cannon base").first().isEmpty() && BankInventory.search().nameContainsNoCase("cannon base").first().isEmpty())) {
                    log.warn("We need to go get our cannon! shouldGetCannon = true!");
                    shouldGetCannon = true;
                    restockProgress = 0;
                } else if (isBankOpen() && Bank.search().nameContainsNoCase("cannon base").first().isPresent() || BankInventory.search().nameContainsNoCase("cannon base").first().isPresent() || !shouldUseCannon(currentMonster)) {
                    shouldGetCannon = false;
                    log.info("We dont need to get a cannon!");
                    PolarAPI.withdrawNoCase(config.foodItemName(), InventoryUtil.emptySlots());
                    log.info("Withdrew food '{}'", config.foodItemName());
                    log.info("Restocking Stage 14: Food.");
                    restockProgress = 0;
                }
                break;
            default:
                log.warn("Unexpected restockProgress value: {}", restockProgress);
                restockProgress = 0;  // Reset on unexpected value to avoid getting stuck.
                break;
        }
    }

    public void equipBracelets(MonsterRules currentMonster) {
        BraceletsType braceletType = getBraceletsTypeForMonster(currentMonster);
        int equipmentId = client.getLocalPlayer().getPlayerComposition().getEquipmentId(KitType.HANDS);
        if (equipmentId != braceletType.getItemID() && InventoryUtil.hasItem(braceletType.getItemID())) {
            InventoryInteraction.useItem(braceletType.getItemID(), "wear");
            log.info("Equipping bracelet for " + currentMonster.name() + ": " + braceletType.name());
        }
    }


    private void handleFood() {
        Inventory.search().nameContainsNoCase(config.foodItemName().toLowerCase()).first().ifPresent(x -> {
            InventoryInteraction.useItem(x, "Eat", "Drink");
            log.info("Eating food: " + config.foodItemName());
            timeout = tickDelay();
        });
    }

    private String getBoostPotionForAttackType(AttackType boostType) {
        switch (boostType) {
            case MELEE:
                return config.meleeBoostSelect();
            case RANGED:
                return config.rangedBoostSelect();
            case MAGIC:
                return config.mageBoostSelect();
            case DRAGON:
                return config.dragBoostSelect();
            case ALTERNATE_RANGED:
                return config.ranged2BoostSelect();
            case CUSTOM:
                return config.customBoostSelect();
            case CUSTOM2:
                return config.custom2BoostSelect();
            case BARRAGE:
                return config.barrageBoostSelect();
            case SPELL:
                return config.spellBoostSelect();
            default:
                return "";  // No boost needed or unknown type
        }
    }

    private String getRequiredGear(AttackType attackType) {
        switch (attackType) {
            case MELEE:
                return config.meleeGear();
            case RANGED:
                return config.rangedGear();
            case ALTERNATE_RANGED:
                return config.rangedGear2();
            case MAGIC:
                return config.mageGear();
            case BARRAGE:
                return config.barrageGear();
            case DRAGON:
                return config.dragonGear();
            case CUSTOM:
                return config.customGear();
            case CUSTOM2:
                return config.customGear2();
            default:
                log.warn("Unknown attack type: {}", attackType);
                return "";
        }
    }

    private void placeCannon() {
        if (PolarAPI.getPlayerLoc().distanceTo(currentMonster.getCannonPoint()) < 5) {
            if (InventoryUtil.hasItem("Cannon base") || InventoryUtil.hasItem("Cannon base (or)")) {
                PolarAPI.useItem("Cannon base", "Set-up");
                PolarAPI.useItem("Cannon base (or)", "Set-up");
            }
        }
    }

    private void fireCannon() {
        if (InventoryUtil.hasItem("Cannonball")) {
            TileObjects.search()
                    .withinDistance(8)
                    .withAction("Fire")
                    .nearestToPlayer()
                    .ifPresent(obj -> TileObjectInteraction.interact(obj, "Fire"));

            ObjectUtil.getNearest(6) // Make sure this is targeting the correct object.
                    .ifPresent(obj -> {
                        TileObjectInteraction.interact(obj, "Fire");
                        PolarAPI.sendClientMessage("Firing Cannon");
                    });
        }
    }


    private void enterCavern() {
        TileObjects.search()
                .withinDistance(8)
                .withAction("Enter")
                .nearestToPlayer()
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Enter"),
                        () -> PolarAPI.sendClientMessage("No cave.")
                );
        ObjectUtil.getNearest(31555)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Enter"),
                        () -> PolarAPI.sendClientMessage("no cave")
                );

    }

    private void enterKBDLadder() {
        TileObjects.search()
                .withinDistance(12)
                .withAction("Climb-down")
                .nearestToPlayer()
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Climb-down"),
                        () -> PolarAPI.sendClientMessage("No kbd cave.")
                );
        ObjectUtil.getNearest(18987)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Climb-down"),
                        () -> PolarAPI.sendClientMessage("no kbd ladder cave")
                );

    }

    private void openGates() {
        TileObjects.search()
                .withinDistance(3)
                .withAction("Open")
                .nearestToPlayer()
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Open"),
                        () -> PolarAPI.sendClientMessage("No visible Gate1.")
                );
        ObjectUtil.getNearest(1568)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Open"),
                        () -> PolarAPI.sendClientMessage("No visible Gate2.")
                );
    }

    private void openDoorNul() {
        TileObjects.search()
                .atLocation(nulodionDoor)
                .withAction("Open")
                .nearestToPlayer()
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Open"),
                        () -> PolarAPI.sendClientMessage("No visible Nulodion Door.")
                );
        ObjectUtil.getNearest(3)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Open"),
                        () -> PolarAPI.sendClientMessage("No visible Nulodion Door!!!!!")
                );
    }

    private void enterCave() {
        TileObjects.search()
                .withinDistance(5)
                .atArea(wildySlayerCaveEntrance)
                .withAction("Walk-down")
                .nearestToPlayer()
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Walk-down"),
                        () -> PolarAPI.sendClientMessage("No visible Slayer Cave Entrance.")
                );
        ObjectUtil.getNearest(40388)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Walk-down"),
                        () -> PolarAPI.sendClientMessage("Can't find Slayer Cave Entrance!")
                );

    }

    private void enterGWDCave() {
        TileObjects.search()
                .withinDistance(5)
                .atArea(gwdCaveEntrance)
                .withAction("Enter")
                .nearestToPlayer()
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Enter"),
                        () -> PolarAPI.sendClientMessage("No visible Slayer Cave Entrance.")
                );
        ObjectUtil.getNearest(26766)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Enter"),
                        () -> PolarAPI.sendClientMessage("Can't find Slayer Cave Entrance!")
                );

    }

    //6621
    private void moveBoulder() {
        PolarAPI.interactNPC("Boulder", "Move", true);
    }

    private void useCrevice() {
        ObjectUtil.getNearest(26767)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Use"),
                        () -> PolarAPI.sendClientMessage("no crev!")
                );

    }

    private void useTrap() {
        ObjectUtil.getNearest(1579)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Open"),
                        () -> PolarAPI.sendClientMessage("no trapdoor!")
                );
        ObjectUtil.getNearest(1581)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Climb-down"),
                        () -> PolarAPI.sendClientMessage("no trapdoor to climb down!")
                );
    }


    private void useGayte() {
        ObjectUtil.getNearest(1569)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Open"),
                        () -> PolarAPI.sendClientMessage("no gayte1!")
                );
    }
    private void useGayte2() {
        ObjectUtil.getNearest(1727)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Open"),
                        () -> PolarAPI.sendClientMessage("no gayteeee2!")
                );
    }
    private void pickupCannon() {
        TileObjects.search()
                .withinDistance(9)
                .atArea(currentMonster.getNpcWorldArea())
                .withAction("Pick-up")
                .nearestToPlayer()
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Pick-up"),
                        () -> PolarAPI.sendClientMessage("Picking up cannon.")
                );
        ObjectUtil.getNearest(16664)
                .ifPresentOrElse(
                        obj -> TileObjectInteraction.interact(obj, "Pick-up"),
                        () -> PolarAPI.sendClientMessage("Picking up cannon.")
                );
    }

    private boolean isGearEquipped(String gearList) {
        // Assume gearList is a comma-separated list of item names.
        for (String gear : gearList.split(",")) {
            if (!Equipment.search().withName(gear).empty()) {
                return true;
            }
        }
        return false;
    }

    private void withdrawGear(String gearList) {
        for (String gear : gearList.split(",")) {
            PolarAPI.withdraw(gear.trim(), 1); // Adjust quantity as needed.
        }
        log.info("Withdrew all items for {}", gearList);
    }

    private void withdrawCannonParts() {
        if (config.useCannonDecoration()) {
            PolarAPI.withdrawNoCase("Cannon base (or)", 1);
            PolarAPI.withdrawNoCase("Cannon barrels (or)", 1);
            PolarAPI.withdrawNoCase("Cannon furnace (or)", 1);
            PolarAPI.withdrawNoCase("Cannon stand (or)", 1);
            PolarAPI.withdrawNoCase("Cannonball", config.cbAmount());
        } else {
            log.info("Cannon parts withdrawn");
            PolarAPI.withdrawNoCase("Cannon base", 1);
            PolarAPI.withdrawNoCase("Cannon barrels", 1);
            PolarAPI.withdrawNoCase("Cannon furnace", 1);
            PolarAPI.withdrawNoCase("Cannon stand", 1);
            PolarAPI.withdrawNoCase("Cannonball", config.cbAmount());
        }
    }

    /*    private void goToSlayerLocation() {
            PolarAPI.walk(config.vyreType().getDoorWorldPoint());
            lastAvoidBitchTick = 0;
        }*/
    public void depositLootIfPossible() {
        WidgetPackets.queueWidgetActionPacket(1, 983046, -1, -1);
    }

    public void exitCaveIfPossible() {
        WidgetPackets.queueResumePause(14352385, 1);
        log.info("Attempting to leave cave!");
    }

    public void closeLB() {
        WidgetPackets.queueWidgetActionPacket(1, 983048, -1, -1);
    }

    public void hopWorlds() {
        MousePackets.queueClickPacket();
        worldHopper.setupHop();
        worldHopper.hopWorlds();
        log.info("HOPPING");
    }

    /*    private void openDoor() {
            timeout = tickDelay();
            if (!PolarAPI.isPlayerInArea(new WorldArea(0,0,0,0,0))) {
                if (PolarAPI.isPlayerOnPoint(config.vyreType().getDoorWorldPoint()) && TileObjects.search().withName("Door").atLocation(config.vyreType().getDoorWorldPoint())
                        .withAction("Open")
                        .first().isPresent() && !PolarAPI.isPlayerInArea(gay)) {
                    TileObjectInteraction.interact("Door", "Open");
                }
            }
        }*/
    private boolean isInCombat() {
        Player localPlayer = client.getLocalPlayer();

        boolean playerInCombat = localPlayer.getAnimation() != -1;

        boolean npcInCombat = client.getNpcs().stream().anyMatch(npc -> {
            if (npc.getInteracting() != null) {
                return npc.getInteracting().equals(localPlayer);
            }
            return false;
        });
        return playerInCombat || npcInCombat;
    }

    public boolean isRunEnabled() {
        return client.getVarpValue(173) == 1;
    }

    public void handleRun(int minEnergy, int randMax) {
        if (nextRunEnergy < minEnergy || nextRunEnergy > minEnergy + randMax) {
            nextRunEnergy = getRandomIntBetweenRange(minEnergy, minEnergy + getRandomIntBetweenRange(0, randMax));
        }
        if ((PolarAPI.runEnergy() / 100) > nextRunEnergy ||
                client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) != 0) {
            if (!isRunEnabled()) {
                nextRunEnergy = 0;
                Widget runOrb = client.getWidget(WidgetInfo.MINIMAP_RUN_ORB);

                if (runOrb != null) {
                    enableRun();
                }
            }
        }
    }

    private List<String> getMissingGear(String requiredGear) {
        List<String> missingItems = new ArrayList<>();
        List<String> requiredItems = Arrays.asList(requiredGear.split(","));

        for (String item : requiredItems) {
            if (!PolarAPI.isItemEquipped(item.trim())) {
                missingItems.add(item);
            }
        }
        return missingItems;
    }

    private void handleOffensivePrayers(boolean pluginRunning) {
        if (!pluginRunning || currentMonster == null) {
            return;
        }

        OffensivePrayerType offensiveType = getOffensivePrayerTypeForMonster(currentMonster);
        Prayer offensivePrayerToUse = null;
        switch (offensiveType) {
            case MELEE:
                offensivePrayerToUse = getMeleePrayer(config.meleeOffensivePrayer());
                break;
            case MAGIC:
                offensivePrayerToUse = getMagicPrayer(config.magicOffensivePrayer());
                break;
            case RANGED:
                offensivePrayerToUse = getRangedPrayer(config.rangedOffensivePrayer());
                break;
            case NONE:
                //   System.out.println("No offensive prayer needed for " + currentMonster.getNpcName());
                return;
        }

        if (!PrayerUtil.isPrayerActive(offensivePrayerToUse)) {
            if (config.flickPray()) {
                Prayer finalPrayerToUse = offensivePrayerToUse;
                clientThread.invoke(() -> {
                    PrayerUtil.togglePrayer(finalPrayerToUse); // Toggle on
                    PrayerUtil.togglePrayer(finalPrayerToUse); // Toggle off
                });
            } else {
                Prayer finalPrayerToUse = offensivePrayerToUse;
                clientThread.invoke(() -> PrayerUtil.togglePrayer(finalPrayerToUse));
            }
        }
    }


    private void handlePrayers(Boolean pluginRunning) {
        if (currentMonster == null) {
            return;
        }

        if (prayersToFlick == null) {
            return;
        }
        if (pluginRunning) {
            for (Prayer prayer : prayersToFlick) {
                if (prayer != null && !PrayerUtil.isPrayerActive(prayer)) {
                    PrayerUtil.togglePrayer(prayer);
                }
            }
            if (config.flickPray()) {
                PrayerUtil.toggleMultiplePrayers(prayersToFlick);
                PrayerUtil.toggleMultiplePrayers(prayersToFlick);
            }
        }
    }
    private void withdrawBraceletsForMonster(MonsterRules currentMonster) {
        BraceletsType braceletType = getBraceletsTypeForMonster(currentMonster);
        log.info("Withdrawing bracelets for {}: {}", currentMonster.name(), braceletType.name());
        switch (braceletType) {
            case SLAUGHTER:
                PolarAPI.withdrawContainsNoCase("Bracelet of slaughter", config.braceletAmount());
                break;
            case EXPEDITIOUS:
                PolarAPI.withdrawContainsNoCase("Expeditious bracelet", config.braceletAmount());
                break;
        }
    }

    private OffensivePrayerType getOffensivePrayerTypeForMonster(MonsterRules monster) {
        switch (monster) {
            case ABYSSAL_DEMONS:
                return config.abyssalDemonsOffPrayer();
            case ANKOU:
                return config.ankouOffensivePrayer();
            case AVIANSIE:
                return config.aviansieOffensivePrayer();
            case BANDITS:
                return config.banditOffensivePrayer();
            case BEARS:
            case BEARS_TURAEL:
                return config.bearOffPrayer();
            case BLACK_DEMONS:
                return config.blackDemonOffPrayer();
            case BLACK_DRAGONS:
                return config.blackDragonOffPrayer();
            case BLACK_KNIGHTS:
                return config.blackKnightsOffPrayer();
            case BLOODVELD:
                return config.bloodveldOffPrayer();
            case CHAOS_DRUIDS:
                return config.chaosDruidOffPrayer();
            case DARK_WARRIORS:
                return config.darkWarriorOffPrayer();
            case DUST_DEVILS:
                return config.dustDevilOffPrayer();
            case EARTH_WARRIORS:
                return config.earthWarriorOffPrayer();
            case ENTS:
                return config.entOffPrayer();
            case FIRE_GIANTS:
                return config.fireGiantOffPrayer();
            case GREATER_DEMONS:
                return config.greaterDemonOffPrayer();
            case GREEN_DRAGONS:
                return config.greenDragonOffPrayer();
            case HELLHOUNDS:
                return config.hellhoundOffPrayer();
            case HILL_GIANTS:
                return config.hillGiantOffPrayer();
            case ICE_GIANTS:
                return config.iceGiantOffPrayer();
            case ICE_WARRIORS:
                return config.iceWarriorOffPrayer();
            case JELLIES:
                return config.jelliesOffPrayer();
            case LAVA_DRAGONS:
                return config.lavaDragonOffPrayer();
            case LESSER_DEMONS:
                return config.lesserDemonOffPrayer();
            case MAGIC_AXES:
                return config.magicAxeOffPrayer();
            case MAMMOTHS:
                return config.mammothOffPrayer();
            case MOSS_GIANTS:
                return config.mossGiantOffPrayer();
            case NECHRYAELS:
                return config.nechryaelOffPrayer();
            case PIRATES:
                return config.pirateOffPrayer();
            case REVENANTS:
                return config.revenantOffPrayer();
            case ROGUES:
                return config.rogueOffPrayer();
            case SCORPIONS:
                return config.scorpionOffPrayer();
            case SKELETONS:
                return config.skeletonOffPrayer();
            case SPIDERS:
                return config.spiderOffPrayer();
            case SPIRITUAL_CREATURES:
                return config.spiritualCreatureOffPrayer();
            case ZOMBIES:
                return config.zombieOffPrayer();
            default:
                System.out.println("No specific prayer configured for " + monster.getNpcName());
                return OffensivePrayerType.NONE;
        }
    }

    private BraceletsType getBraceletsTypeForMonster(MonsterRules monster) {
        switch (monster) {
            case ABYSSAL_DEMONS:
                return config.abyssalDemonsBracelet();
            case ANKOU:
                return config.ankouBracelet();
            case AVIANSIE:
                return config.aviansieBracelet();
            case BANDITS:
                return config.banditsBracelet();
            case BEARS:
            case BEARS_TURAEL:
                return config.bearsBracelet();
            case BLACK_DEMONS:
                return config.blackDemonsBracelet();
            case BLACK_DRAGONS:
                return config.blackDragonsBracelet();
            case BLACK_KNIGHTS:
                return config.blackKnightsBracelet();
            case BLOODVELD:
                return config.bloodveldBracelet();
            case CHAOS_DRUIDS:
                return config.chaosDruidsBracelet();
            case DARK_WARRIORS:
                return config.darkWarriorsBracelet();
            case DUST_DEVILS:
                return config.dustDevilsBracelet();
            case EARTH_WARRIORS:
                return config.earthWarriorsBracelet();
            case ENTS:
                return config.entsBracelet();
            case FIRE_GIANTS:
                return config.fireGiantsBracelet();
            case GREATER_DEMONS:
                return config.greaterDemonsBracelet();
            case GREEN_DRAGONS:
                return config.greenDragonsBracelet();
            case HELLHOUNDS:
                return config.hellhoundsBracelet();
            case HILL_GIANTS:
                return config.hillGiantsBracelet();
            case ICE_GIANTS:
                return config.iceGiantsBracelet();
            case ICE_WARRIORS:
                return config.iceWarriorBracelet();
            case JELLIES:
                return config.jellyBracelet();
            case LAVA_DRAGONS:
                return config.lavaDragonBracelet();
            case LESSER_DEMONS:
                return config.lesserDemonsBracelet();
            case MAGIC_AXES:
                return config.magicAxeBracelet();
            case MAMMOTHS:
                return config.mammothBracelet();
            case MOSS_GIANTS:
                return config.mossGiantBracelet();
            case NECHRYAELS:
                return config.nechryaelBracelet();
            case PIRATES:
                return config.pirateBracelet();
            case REVENANTS:
                return config.revenantBracelet();
            case ROGUES:
                return config.rogueBracelet();
            case SCORPIONS:
                return config.scorpionBracelet();
            case SKELETONS:
                return config.skeletonBracelet();
            case SPIDERS:
                return config.spiderBracelet();
            case SPIRITUAL_CREATURES:
                return config.spiritualCreaturesBracelet();
            case ZOMBIES:
                return config.zombieBracelet();
            default:
                System.out.println("No specific prayer configured for " + monster.getNpcName());
                return BraceletsType.NONE;
        }
    }

    private Prayer getMeleePrayer(OffensivePrayerTypeMelee meleePrayer) {
        switch (meleePrayer) {
            case PIETY:
                return Prayer.PIETY;
            case CHIVALRY:
                return Prayer.CHIVALRY;
            case ULTIMATE_STRENGTH:
                return Prayer.ULTIMATE_STRENGTH;
            case IMPROVED_REFLEXES:
                return Prayer.IMPROVED_REFLEXES;
            case NONE:
                return null;
        }
        return null;
    }
    public void togglePrayersOff() {
        List<Prayer> prayersToToggle = Arrays.asList(
                Prayer.PROTECT_FROM_MELEE,
                Prayer.PROTECT_FROM_MAGIC,
                Prayer.PROTECT_FROM_MISSILES,
                Prayer.EAGLE_EYE,
                Prayer.PIETY,
                Prayer.RIGOUR,
                Prayer.HAWK_EYE,
                Prayer.MYSTIC_MIGHT,
                Prayer.MYSTIC_LORE,
                Prayer.AUGURY,
                Prayer.ULTIMATE_STRENGTH,
                Prayer.CHIVALRY,
                Prayer.IMPROVED_REFLEXES
        );

        if (PolarAPI.isPlayerInArea(edge)) {
            for (Prayer prayer : prayersToToggle) {
                if (PrayerUtil.isPrayerActive(prayer)) {
                    PrayerUtil.togglePrayer(prayer);
                }
            }
        }
    }
    private Prayer getMagicPrayer(OffensivePrayerTypeMagic magicPrayer) {
        switch (magicPrayer) {
            case AUGURY:
                return Prayer.AUGURY;
            case MYSTIC_MIGHT:
                return Prayer.MYSTIC_MIGHT;
            case MYSTIC_LORE:
                return Prayer.MYSTIC_LORE;
            case NONE:
                return null; // Return null for NONE
        }
        return null;
    }

    private Prayer getRangedPrayer(OffensivePrayerTypeRanged rangedPrayer) {
        switch (rangedPrayer) {
            case RIGOUR:
                return Prayer.RIGOUR;
            case EAGLE_EYE:
                return Prayer.EAGLE_EYE;
            case HAWK_EYE:
                return Prayer.HAWK_EYE;
            case NONE:
                return null; // Return null for NONE
        }
        return null;
    }

    private DefensivePrayerType getPrayerTypeForMonster(MonsterRules monster) {
        switch (monster) {
            case ABYSSAL_DEMONS:
                return config.abyssalDemonsPrayer();
            case ANKOU:
                return config.ankouPrayer();
            case AVIANSIE:
                return config.aviansiePrayer();
            case BANDITS:
                return config.banditPrayer();
            case BEARS:
            case BEARS_TURAEL:
                return config.bearPrayer();
            case BLACK_DEMONS:
                return config.blackDemonPrayer();
            case BLACK_DRAGONS:
                return config.blackDragonPrayer();
            case BLACK_KNIGHTS:
                return config.blackKnightPrayer();
            case BLOODVELD:
                return config.bloodveldPrayer();
            case CHAOS_DRUIDS:
                return config.chaosDruidPrayer();
            case DARK_WARRIORS:
                return config.darkWarriorPrayer();
            case DUST_DEVILS:
                return config.dustDevilPrayer();
            case EARTH_WARRIORS:
                return config.earthWarriorPrayer();
            case ENTS:
                return config.entPrayer();
            case FIRE_GIANTS:
                return config.fireGiantPrayer();
            case GREATER_DEMONS:
                return config.greaterDemonPrayer();
            case GREEN_DRAGONS:
                return config.greenDragonPrayer();
            case HELLHOUNDS:
                return config.hellhoundPrayer();
            case HILL_GIANTS:
                return config.hillGiantPrayer();
            case ICE_GIANTS:
                return config.iceGiantPrayer();
            case ICE_WARRIORS:
                return config.iceWarriorPrayer();
            case JELLIES:
                return config.jelliesPrayer();
            case LAVA_DRAGONS:
                return config.lavaDragonPrayer();
            case LESSER_DEMONS:
                return config.lesserDemonPrayer();
            case MAGIC_AXES:
                return config.magicAxesPrayer();
            case MAMMOTHS:
                return config.mammothPrayer();
            case MOSS_GIANTS:
                return config.mossGiantPrayer();
            case NECHRYAELS:
                return config.nechryaelPrayer();
            case PIRATES:
                return config.piratePrayer();
            case REVENANTS:
                return config.revenantsPrayer();
            case ROGUES:
                return config.roguesPrayer();
            case SCORPIONS:
                return config.scorpionsPrayer();
            case SKELETONS:
                return config.skeletonsPrayer();
            case SPIDERS:
                return config.spidersPrayer();
            case SPIRITUAL_CREATURES:
                return config.spiritualCreaturesPrayer();
            case ZOMBIES:
                return config.zombiesPrayer();
            default:
                System.out.println("No specific prayer configured for " + monster.getNpcName());
                return DefensivePrayerType.NONE;
        }
    }


    public boolean isReadyToSlay() {
        if (currentMonster == null) {
            log.warn("No current monster task set.");
            return false;
        }

        WorldArea monsterArea = currentMonster.getNpcWorldArea();
        boolean isPlayerInMonsterArea = PolarAPI.isPlayerInArea(monsterArea);

        // Fetch the attack type for the current monster and get the required gear string.
        AttackType gearType = getRequiredGear(currentMonster);  // Fetch the correct AttackType based on the monster
        String requiredGear = getRequiredGear(gearType);  // Fetch the gear string from config based on the attack type

        // Check if the correct gear is equipped
        List<String> missingGear = getMissingGear(requiredGear);

        if (!missingGear.isEmpty() && !isPlayerInMonsterArea && PolarAPI.runEnergy() >= 60) {
            log.info("Not ready: Missing gear items - {}", String.join(", ", missingGear));
            String message = "Not ready: Missing gear items - " + String.join(", ", missingGear);
            PolarAPI.sendClientMessage(message);
            return false;
        }

        // Check if slayer items are available in the inventory (if required)
        if (currentMonster.getRequiredSlayerItems() != null) {
            for (int itemId : currentMonster.getRequiredSlayerItems()) {
                if (!InventoryUtil.hasItem(itemId)) {
                    log.info("Not ready: Missing required slayer item ID {}", itemId);
                    return false;
                }
            }
        }
        // Retrieve the configured ammo type and required gear
        AmmoSelect selectedAmmo = config.AmmoType();
        boolean requiresAmmo = requiredGear.contains("bow") && !requiredGear.contains("fae") &&
                !requiredGear.contains("craw") && !requiredGear.contains("venator") &&
                !requiredGear.contains("weaver");

        if (requiresAmmo) {
            boolean hasAmmo = EquipmentUtil.hasItem(selectedAmmo.getDisplayName()) || InventoryUtil.hasItem(selectedAmmo.getDisplayName());
            if (!hasAmmo && PolarAPI.runEnergy() >= 60) {
                log.info("Not ready: Insufficient ammo.");
                PolarAPI.sendClientMessage("Not ready: No Ammo");
                if (PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {  // Ensure this method checks if the player is in the monster area
                    Optional<TileObject> cannon = TileObjects.search()
                            .nameContainsNoCase("dwarf multicannon")
                            .withinDistancetoPoint(PolarAPI.getPlayerLoc(), 7)
                            .nearestToPlayer();
                    if (cannon.isPresent()) {
                        if (Inventory.getEmptySlots() < 4) {
                            PolarAPI.dropItem(config.foodItemName(), 4);
                        } else if (Inventory.getEmptySlots() >= 4) {
                            TileObjectInteraction.interact(cannon.get(), "Pick-up");
                        }
                        PolarAPI.sendClientMessage("Picked up cannon");
                    } else {
                        log.info("No cannon found, teleporting for restock.");
                        teleForRestock();
                    }
                }
                return false;
            }
        }


        // Check for sufficient prayer potions
        PrayerOptions selectedPrayerPotion = config.PRAYER_OPTIONS();
        boolean hasPrayerPotions = false;
        for (int potionId : selectedPrayerPotion.getItemIds()) {
            if (InventoryUtil.getItemAmount(potionId) > 0) {
                hasPrayerPotions = true;
                break;
            }
        }
        if (!hasPrayerPotions && PolarAPI.runEnergy() >= 60) {
            log.info("Not ready: Insufficient prayer potions.");
            PolarAPI.sendClientMessage("Not ready: No prayer pots");
            if (isPlayerInMonsterArea) {
                Optional<TileObject> cannon = TileObjects.search()
                        .nameContainsNoCase("dwarf multicannon")
                        .withinDistancetoPoint(PolarAPI.getPlayerLoc(), 7)
                        .nearestToPlayer();
                if (cannon.isPresent()) {
                    if (Inventory.getEmptySlots() < 4) {
                        PolarAPI.dropItem(config.foodItemName(), 4);
                    } else if (Inventory.getEmptySlots() >= 4) {
                        TileObjectInteraction.interact(cannon.get(), "Pick-up");
                    }
                    PolarAPI.sendClientMessage("Picked up cannon");
                } else {
                    log.info("No cannon found, teleporting for restock.");
                    teleForRestock();
                }
            }
            return false;
        }

        // Check food amount and handle cannon retrieval if necessary
        String foodItemName = config.foodItemName();
        int foodCount = InventoryUtil.getItemAmount(foodItemName, false);

        if (foodCount <= 4 && currentMonster != null && shouldUseCannon(currentMonster) && PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea()) && PolarAPI.runEnergy() >= 60) {
            log.info("Low food detected: only {} {} left. Preparing to restock.", foodCount, foodItemName);
            PolarAPI.sendClientMessage("Low food with cannon, prepping for restock.");
            // Drop all remaining food to make space
            PolarAPI.dropItem(foodItemName, 4);

            // Try to pick up the cannon if nearby
            Optional<TileObject> cannon = TileObjects.search()
                    .nameContainsNoCase("dwarf multicannon")
                    .withinDistance(7)
                    .nearestToPlayer();

            if (cannon.isPresent()) {
                if (Inventory.getEmptySlots() >= 4) {
                    TileObjectInteraction.interact(cannon.get(), "Pick-up");
                    PolarAPI.sendClientMessage("Cannon picked up.");
                } else {
                    log.info("Not enough inventory space to pick up the cannon.");
                    // Additional logic to handle inventory space issue
                }
            } else {
                log.info("No cannon found nearby.");
            }
            teleForRestock();
            return false;
        }

        boolean hasFood = Inventory.search().nameContainsNoCase(config.foodItemName().toLowerCase()).first().isPresent();
        if (!hasFood && PolarAPI.runEnergy() >= 60) {
            log.info("Not Ready: Insufficient food amount. Required: >1");
            PolarAPI.sendClientMessage("Not Ready: Insufficient food amount. Required: >1");
            if (isPlayerInMonsterArea) {
                Optional<TileObject> cannon = TileObjects.search()
                        .nameContainsNoCase("dwarf multicannon")
                        .withinDistancetoPoint(PolarAPI.getPlayerLoc(), 7)
                        .nearestToPlayer();
                if (cannon.isPresent()) {
                    if (Inventory.getEmptySlots() < 4) {
                        PolarAPI.dropItem(config.foodItemName(), 4);
                    } else if (Inventory.getEmptySlots() >= 4) {
                        TileObjectInteraction.interact(cannon.get(), "Pick-up");
                    }
                    PolarAPI.sendClientMessage("Picked up cannon");
                } else {
                    log.info("No cannon found, teleporting for restock.");
                    teleForRestock();
                }
            }
            return false;
        }
        return true;
    }


    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void enableRun() {
        Widget widget = client.getWidget(10485787);
        MousePackets.queueWidgetClickPacket(widget);
        WidgetPackets.queueWidgetActionPacket(1, widget.getId(), -1, -1);
    }

    /*    private boolean isPlayerInCombatArea() {
            return !Players.search().withinWorldArea(config.vyreType().getWorldArea()).notLocalPlayer().isEmpty();
        }*/
    public boolean isBankPinOpen() {
        return (client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null);
    }

    private void teleForRestock() {
        if (wildyLvl <= 30) {
            if (config.TELEPORT_OPTIONS() == TeleportOptions.AMULET_OF_GLORY) {
                Optional<Widget> glory = Inventory.search().nameContains("glory(").first();
                if (glory.isPresent()) {
                    InventoryInteraction.useItem(glory.get(), "Equip", "Wear", "Wield");
                    log.info("Equipping glory to go restock.");
                } else {
                    log.info("No Amulet of Glory with charges found in inventory.");
                    performTeleport();
                    if (PolarAPI.isInWilderness() && getSlayerTaskSize() < 1 && !PolarAPI.isPlayerInArea(safeTeleWildy)) {
                        walkSafety();
                        performTeleport();
                    }
                }
            }

        // Check for conditions to teleport or walk to safety after potentially equipping the Amulet of Glory
        if (wildyLvl <= 30) {
            teleportToEdge();
        } else if (!PolarAPI.isPlayerInArea(safeTeleWildy) && !PolarAPI.isPlayerInArea(edge) && PolarAPI.isInWilderness()) {
            walkSafety();
            log.info("Walking to safety!");
        }

        if (needsSafeTeleport() && !PolarAPI.isPlayerInArea(edge)) {
            performTeleport();
            log.info("Safe! Teleporting now!");
        }
    }
}

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (!pluginRunning) {
            return;
        }
        String message = event.getMessage();
        if (message == null) {
            return;
        }
        ChatMessageType chatMessageType = event.getType();

        if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
            return;
        }
        if (config.useChatOnLevel()) {
            if (message.contains("just advanced your Slayer level. You are now level")) {
                String[] responses = config.messageToSend().split(",");
                if (responses.length > 0) {
                    String response = responses[PolarAPI.random(0, responses.length - 1)].trim(); // Pick a random response
                    PolarAPI.sendChat(response); // Send the random response
                }
            }
        }
    }


    private void goBreak() {
        breakHandler.startBreak(this);
        System.out.println("Breaking!");
    }

    public String getStatus() {
        if (state == null) {
            return "Ready to start.";
        }
        switch (state) {
            case IDLE:
                return "Idle";
            case BREAKING:
                return "Taking a break";
            case GOING_TO_BANK:
                return "Heading to the bank";
            case OPEN_BANK:
                return "Opening the bank";
            case DEPOSIT:
                return "Depositing items";
            case WITHDRAW:
                return "Withdrawing items";
            case EQUIP:
                return "Equipping gear";
            case TIMEOUT:
                return "Experiencing a timeout";
            case CHECK_RESTOCK:
                return "Checking and restocking gear";
            case GO_NULODION:
                return "Going to Nulodion";
            case RETRIEVE_CANNON:
                return "Retrieving cannon";
            case TRAVERSE:
                return "Traversing to the destination";
            case TELE_TO_AREA:
                return "Teleporting to the task area";
            case HOP_DITCH:
                return "Hopping the wilderness ditch";
            case GO_DITCH:
                return "Approaching the wilderness ditch";
            case ATTACKING:
                return "Target acquired, attacking";
            case GO_SLAYER_MASTER:
                return "Going to the Slayer Master";
            case GETTING_TASK:
                return "Getting a new Slayer task";
            case LOOTING:
                return "Looting from dead bodies";
            case RETURNING:
                return "Returning to Edgeville";
            default:
                return "Biggs is amazing!";
        }
    }

    private void sendKey(int key) {
        keyEvent(KeyEvent.KEY_PRESSED, key);
        keyEvent(KeyEvent.KEY_RELEASED, key);
    }

    private void keyEvent(int id, int key) {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, key, KeyEvent.CHAR_UNDEFINED
        );

        client.getCanvas().dispatchEvent(e);
    }

    public boolean isStarted() {
        return pluginRunning;
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (!"exe".equals(event.getGroup())) {
            return;
        }

        if (event.getKey().equals("copyGear")) {
            clientThread.invoke(() -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (Item item : this.client.getItemContainer(InventoryID.EQUIPMENT).getItems()) {
                    if (item != null && item.getId() != -1
                            && item.getId() != 6512) {
                        ItemComposition itemComposition = client.getItemDefinition(item.getId());
                        stringBuilder.append(itemComposition.getName()).append(",");
                    }
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                StringSelection selection = new StringSelection(stringBuilder.toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            });
        }
    }

    private void useTraversalItem(String itemId) {
        switch (itemId) {
            case "Burning amulet(":  // Assuming 21166 is the ID for a specific teleport like Burning Amulet
                useBurningAmulet(itemId);
                log.info("Attempting to call useBurningAmulet");
                break;
            case "Games necklace(":
                useGamesNecklace(itemId);
                break;
            case "Revenant cave teleport":
                useRevCave();
            default:
                log.warn("No handling method for item ID: {}", itemId);
                break;
        }
    }

    private void useRevCave() {
        PolarAPI.useItem("Revenant cave teleport", "Teleport");
        exitCaveIfPossible();
    }

    private void useGamesNecklace(String itemId) {
        if (PolarAPI.isPlayerInArea(edgeBanky) && isReadyToSlay() && currentMonster != null) {
            Inventory.search().nameContains("Games necklace(").first().ifPresent(item -> {
                log.info("Using Games Necklace for teleport.");
                InventoryInteraction.useItem(item, "Rub");
                timeout = 3;
                Widgets.search()
                        .withId(14352385)
                        .hiddenState(false)
                        .first()
                        .ifPresentOrElse(
                                widget -> {
                                    MousePackets.queueClickPacket();
                                    WidgetPackets.queueResumePause(widget.getId(), 3);
                                    timeout = 3;
                                },
                                () -> log.warn("Failed to find widget for 'Corporeal beast'")
                        );
            });
        }
    }

    public boolean shouldUseChaosTempleWidget() {
        List<String> chaosTempleMonsters = Arrays.asList("Monster1", "Monster2");
        return currentMonster != null && chaosTempleMonsters.contains(currentMonster.getNpcName());
    }

    public boolean shouldUseBanditCampWidget() {
        List<String> banditCampMonsters = Arrays.asList("Bandit", "Grizzly bear", "Revenant goblin", "Bloodveld", "Avisansie", "Deadly red spider", "Spiritual warrior");
        return currentMonster != null && banditCampMonsters.contains(currentMonster.getNpcName());
    }

    public boolean shouldUseLavaMazeWidget() {
        List<String> lavaMazeMonsters = Arrays.asList("Ice warrior", "Black knight", "Hill giant");
        // boolean isBlackDragonForKbd = currentMonster != null && "Black dragon".equals(currentMonster.getNpcName()) && config.doKBD();
        return currentMonster != null && lavaMazeMonsters.contains(currentMonster.getNpcName());// || isBlackDragonForKbd;
    }

    private void useBurningAmulet(String itemId) {
        log.info("Calling useBurningAmulet, should be Rubbing amulet..");
        if (PolarAPI.isPlayerInArea(edgeBanky) && isReadyToSlay() && currentMonster != null) {
            timeout = 3;
            Inventory.search().containsNoCase("burning amulet(").first().ifPresent(item -> {
                log.info("Using Burning Amulet for teleport.");
                InventoryInteraction.useItem(item, "Rub");
                timeout = 3;

                // Decide which widget action to queue based on currentMonster
                if (shouldUseChaosTempleWidget()) {
                    queueWidgetAction(1, "Failed to find widget for 'Chaos Temple'");
                } else if (shouldUseBanditCampWidget()) {
                    queueWidgetAction(2, "Failed to find widget for 'Bandit Camp'");
                } else if (shouldUseLavaMazeWidget()) {
                    queueWidgetAction(3, "Failed to find widget for 'Lava Maze'");
                }
            });
        }
    }
    private void useLever() {
        if (TileObjects.search().withId(1816).first().isPresent()) {
            TileObjectInteraction.interact(1816, "Private");
            exitCaveIfPossible();
            log.info("Attempting to pull KBD Lever!");
        }
    }
    private void findWebToCut() {
        if (TileObjects.search().withName("Web").first().isPresent()) {
            TileObjectInteraction.interact("Web", "Slash");
        }
    }

    private void queueWidgetAction(int actionIndex, String errorMessage) {
        Widgets.search()
                .withId(14352385) // Adjust the widget ID based on actual game UI
                .hiddenState(false)
                .first()
                .ifPresentOrElse(
                        widget -> {
                            MousePackets.queueClickPacket();
                            WidgetPackets.queueResumePause(widget.getId(), actionIndex);
                            timeout = 3;
                        },
                        () -> log.warn(errorMessage)
                );
    }


    private void useGlory() {
        Inventory.search().nameContains("glory(").first().ifPresent(item -> {
            log.info("Using Glory Amulet for teleport.");
            InventoryInteraction.useItem(item, "Rub");
            timeout = 3;
            Widgets.search()
                    .withId(14352385)
                    .hiddenState(false)
                    .first()
                    .ifPresentOrElse(
                            widget -> {
                                MousePackets.queueClickPacket();
                                WidgetPackets.queueResumePause(widget.getId(), 1);
                                timeout = 3;
                            },
                            () -> log.warn("Failed to find widget for 'Edgeville'")
                    );
        });
    }

    private void updateTaskDetails() {
        boolean dwarfMulticannon = ObjectUtil.getNearest("dwarf multicannon", false).isPresent();

        int taskId = client.getVarpValue(VarPlayer.SLAYER_TASK_CREATURE);
        String npcName = client.getEnum(EnumID.SLAYER_TASK_CREATURE).getStringValue(taskId);

        slayerTasksComplete = client.getVarbitValue(5617);
        slayerPoints = client.getVarbitValue(Varbits.SLAYER_POINTS);

        if (getSlayerTaskSize() < 1) {
            log.warn("NPC name for task ID {} is null or empty.", taskId);
            currentMonster = null;
            if (!PolarAPI.isPlayerInArea(edge) && wildyLvl <= 30) {
                if (shouldUseCannon(currentMonster) && dwarfMulticannon) {
                    if (Inventory.getEmptySlots() >= 4) {
                        pickupCannon();
                        PolarAPI.sendClientMessage("Picking up cannon, attempting...");
                    } else {
                        PolarAPI.dropItem(config.foodItemName(), 4);
                        log.info("Eating food to make space for cannon.");
                        PolarAPI.sendClientMessage("Dropping food to make space for cannon.");
                        pickupCannon();
                        PolarAPI.sendClientMessage("Picking up cannon after making space...");
                    }
                } else {
                    // Teleport for restock if no cannon present or should not use a cannon
                    teleForRestock();
                    log.info("Teleporting for restock due to no active task and conditions for cannon not met.");
                    PolarAPI.sendClientMessage("Teleporting for restock due to no active task and conditions for cannon not met.");
                }
            }
        }

        try {
            // Normalize the NPC name to fit Java enum naming conventions.
            String enumName = npcName.toUpperCase().replaceAll("\\s+", "_").replaceAll("[^A-Z0-9_]", "");
            currentMonster = MonsterRules.valueOf(enumName);
            //  log.info("Current monster task updated to: {}", currentMonster);
        } catch (IllegalArgumentException e) {
            // log.warn("No corresponding enum entry for current task: '{}'. Error: {}", npcName, e.getMessage());
            currentMonster = null; // Handle the case where no enum entry matches
        }
    }

    private void shutTheFuckUp() {
        Widgets.search()
                .withTextContains("Okay, teleport to")
                .hiddenState(false)
                .first()
                .ifPresentOrElse(
                        widget -> {
                            // Widget is present
                            MousePackets.queueClickPacket();
                            WidgetPackets.queueResumePause(widget.getId(), 1);
                        },
                        () -> {
                            // Widget is not present
                            //   System.out.println("Couldn't click Widget: Okay, teleport to.");
                        }
                );
    } // this is the lvl 41 widget

    private void updateSlayerPoints() {
        slayerPoints = client.getVarbitValue(Varbits.SLAYER_POINTS);
    }

    public boolean hasTraversingMethodItems() {
        if (currentMonster.getTraversalItemName() != null) {
            return InventoryUtil.hasItem(currentMonster.getTraversalItemName());
        }
        return true; // Return true if no traversing item is required.
    }

    public boolean hasSlayerItems() {
        int[] requiredItems = currentMonster.getRequiredSlayerItems();
        if (requiredItems != null && requiredItems.length > 0) {
            for (int itemId : requiredItems) {
                if (!InventoryUtil.hasItem(itemId)) {
                    return false;
                }
            }
            return true;
        }
        return true; // Return true if no slayer items are required.
    }

    private void ensureBankIsOpen() {
        if (!isBankOpen()) {
            PolarAPI.openNearestBank();
            log.info("Opening bank.");
        }
    }

    private void drinkPotion() {
        if (config.PRAYER_OPTIONS() == PrayerOptions.PRAYER_POTION) {
            Inventory.search().nameContainsNoCase("prayer potion").first().ifPresent(item -> InventoryInteraction.useItem(item, "Drink"));
        }
        if (config.PRAYER_OPTIONS() == PrayerOptions.SUPER_RESTORE) {
            Inventory.search().nameContainsNoCase("super restore").first().ifPresent(item -> InventoryInteraction.useItem(item, "Drink"));
        }
        if (config.PRAYER_OPTIONS() == PrayerOptions.BLIGHTED_SUPER_RESTORE) {
            Inventory.search().nameContainsNoCase("blighted super restore").first().ifPresent(item -> InventoryInteraction.useItem(item, "Drink"));
        }
    }

    private void drinkBoostPotion() {
        if (currentMonster != null && PolarAPI.isPlayerInArea(currentMonster.getNpcWorldArea())) {
            Inventory.search()
                    .nameContainsNoCase("attack")
                    .withAction("Drink")
                    .first()
                    .ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                        log.info("Drinking attack potion: {}", item.getName());
                    });

            Inventory.search()
                    .nameContainsNoCase("strength")
                    .withAction("Drink")
                    .first()
                    .ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                        log.info("Drinking strength potion: {}", item.getName());
                    });
            // Attempt to drink a combat potion
            Inventory.search()
                    .nameContainsNoCase("combat")
                    .withAction("Drink")
                    .first()
                    .ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                        log.info("Drinking combat potion: {}", item.getName());
                    });

            // Attempt to drink a magic potion
            Inventory.search()
                    .nameContainsNoCase("magic")
                    .withAction("Drink")
                    .first()
                    .ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                        log.info("Drinking magic potion: {}", item.getName());
                    });

            // Attempt to drink a ranging potion
            Inventory.search()
                    .nameContainsNoCase("ranging")
                    .withAction("Drink")
                    .first()
                    .ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                        log.info("Drinking ranging potion: {}", item.getName());
                    });

            Inventory.search()
                    .nameContainsNoCase("bastion")
                    .withAction("Drink")
                    .first()
                    .ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                        log.info("Drinking bastion potion: {}", item.getName());
                    });

            Inventory.search()
                    .nameContainsNoCase("divine")
                    .withAction("Drink")
                    .first()
                    .ifPresent(item -> {
                        InventoryInteraction.useItem(item, "Drink");
                        log.info("Drinking divine potion: {}", item.getName());
                    });
        }
    }


    private List<Integer> getPrayerPotionIds() {
        return Arrays.stream(config.PRAYER_OPTIONS().getItemIds())
                .boxed()
                .collect(Collectors.toList());
    }

    public int getSlayerTaskSize() {
        return client.getVarpValue(394);  // 394 is the VARP index for Slayer task size
    }

    private boolean shouldDrinkPrayer() {
        int prayer = getPrayer();
        return (prayer <= config.prayerLow());
    }

    private boolean shouldDrinkBoostPot() {
        AttackType attackType = getRequiredGear(currentMonster);  // Assuming this method gives you the current attack mode
        int currentBoostedLevel;
        int threshold;

        switch (attackType) {
            case RANGED:
                currentBoostedLevel = client.getBoostedSkillLevel(Skill.RANGED);
                threshold = client.getRealSkillLevel(Skill.RANGED) + config.whenToBoostR();
                break;
            case MAGIC:
                currentBoostedLevel = client.getBoostedSkillLevel(Skill.MAGIC);
                threshold = client.getRealSkillLevel(Skill.MAGIC) + config.whenToBoostM();
                break;
            case MELEE:  // Assuming you use Strength for MELEE; adjust if different
                currentBoostedLevel = client.getBoostedSkillLevel(Skill.STRENGTH);
                threshold = client.getRealSkillLevel(Skill.STRENGTH) + config.whenToBoost();
                break;
            default:
                return false;  // No boost needed for other types or not specified
        }

        return currentBoostedLevel < threshold;
    }


    private void logDetails() {
        log.info("Slayer Task Creature: {}", currentMonster);
        log.info("Slayer Points: {}", slayerPoints);
        log.info("Slayer Task Streak: {}", slayerTaskStreak);
    }

   /* private void handleArtio() {
        PolarAPI.openInventory();
        avoidDangerTiles();

        NPC artio = NpcUtils.getNearestNpc("Artio");
        if (artio != null && !artio.isDead()) {
            if (PolarAPI.targetMoving(artio)) {
                PolarAPI.attackNPC("Artio");
            }*//* else {
                regularAttack(artio);
            }*//*

            // Handle prayers based on Artio's current attack type
            togglePrayersBasedOnAttack(artio);
        }
    }*/

   /* private void togglePrayersBasedOnAttack(NPC artio) {
        int attackAnimation = artio.getAnimation();
        if (attackAnimation == 10012) { // Assuming this is the melee attack animation
            PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
        }
        // Add other conditions as needed
    }
    public void avoidDangerTiles() {
        if (isInDangerTile()) {
            WorldPoint safeTile = findSafestTile();
            if (safeTile != null) {
                log.info("Moving to a safe tile at {}", safeTile);
                PolarAPI.move(safeTile);
            } else {
                log.warn("No safe tiles found. Player remains in a dangerous position.");
            }
        }
    }


    public boolean isInDangerTile() {
        WorldPoint playerPosition = PolarAPI.playerPosition();
        return bearTraps.keySet().stream().anyMatch(tile -> tile.distanceTo(playerPosition) <= 1);
    }

    public WorldPoint findSafestTile() {
        WorldPoint playerLocation = PolarAPI.playerPosition();
        List<WorldPoint> potentialSafeTiles = PolarAPI.getSurroundingTiles(playerLocation, 10);  // Get surrounding tiles within a radius of 10 tiles
        potentialSafeTiles.removeIf(tile -> isTileDangerous(tile));  // Remove dangerous tiles

        // Return the closest safe tile
        return potentialSafeTiles.stream()
                .min(Comparator.comparingInt(playerLocation::distanceTo2D))
                .orElse(null);
    }
    private boolean isTileDangerous(WorldPoint tile) {
        return bearTraps.containsKey(tile);
    }
    private int countNearbyHazards(WorldPoint tile) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                WorldPoint nearbyTile = new WorldPoint(tile.getX() + dx, tile.getY() + dy, tile.getPlane());
                if (bearTraps.containsKey(nearbyTile)) {
                    count++;
                }
            }
        }
        return count;
    }
    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        GraphicsObject graphics = event.getGraphicsObject();
        if (graphics.getId() == 2343) {  // Assuming 47146 is the ID for bear traps
            bearTraps.put(WorldPoint.fromLocal(client, graphics.getLocation()), client.getTickCount() + 100);  // Assuming traps last for 100 ticks
        }
    }

    // Subscribing to projectile movements to set appropriate prayers
    @Subscribe
    public void onProjectileMoved(ProjectileMoved event) {
        final Projectile projectile = event.getProjectile();
        switch (projectile.getId()) {
            case 133:
                toPray = Prayer.PROTECT_FROM_MAGIC;
                break;
            case 2350:
                toPray = Prayer.PROTECT_FROM_MISSILES;
                break;
            default:
                toPray = Prayer.PROTECT_FROM_MISSILES;  // Default to missiles if unsure
                break;
        }
    }
    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        final Actor actor = event.getActor();
        if (actor instanceof NPC && "Artio".equals(actor.getName()) && actor.getAnimation() == 10012) {
            toPray = Prayer.PROTECT_FROM_MELEE;
        }
    }


    private WorldPoint calculateSafeTile(WorldPoint playerLocation, List<WorldArea> dangerousAreas) {
        // Define a range to check around the player
        int checkRange = 10;  // Define the range of tiles to check around the player
        List<WorldPoint> potentialSafeTiles = new ArrayList<>();

        // Check tiles around the player within the defined range
        for (int dx = -checkRange; dx <= checkRange; dx++) {
            for (int dy = -checkRange; dy <= checkRange; dy++) {
                WorldPoint tileToCheck = new WorldPoint(playerLocation.getX() + dx, playerLocation.getY() + dy, playerLocation.getPlane());
                if (isTileSafe(tileToCheck, dangerousAreas)) {
                    potentialSafeTiles.add(tileToCheck);
                }
            }
        }

        // Return the closest safe tile to the player, or null if no safe tiles are found
        return potentialSafeTiles.stream()
                .min(Comparator.comparingInt(tile -> tile.distanceTo2D(playerLocation)))
                .orElse(null);
    }

    // Helper method to determine if a given tile is safe
    private boolean isTileSafe(WorldPoint tile, List<WorldArea> dangerousAreas) {
        for (WorldArea area : dangerousAreas) {
            if (area.contains(tile)) {
                return false;  // Tile is not safe if it's within a dangerous area
            }
        }
        return true;  // Tile is safe if it's not contained in any dangerous area
    }*/
   private String getBoostPotion(AttackType attackType) {
       switch (attackType) {
           case MELEE:
               return config.meleeBoostSelect();
           case RANGED:
               return config.rangedBoostSelect();
           case ALTERNATE_RANGED:
               return config.ranged2BoostSelect();
           case MAGIC:
               return config.mageBoostSelect();
           case SPELL:
               return config.spellBoostSelect();
           case BARRAGE:
               return config.barrageBoostSelect();  // Assuming you have a specific potion for Barrage
           case DRAGON:
               return config.dragBoostSelect();   // Assuming you have a specific potion for Dragon attacks
           case CUSTOM:
               return config.customBoostSelect();   // Assuming you have custom potions
           case CUSTOM2:
               return config.custom2BoostSelect();  // Assuming you have second type of custom potions
           default:
               log.warn("No boost potion configured for attack type: {}", attackType);
               return "";
       }
   }

    private AttackType getRequiredGear(MonsterRules monster) {
        switch (monster) {
            case ABYSSAL_DEMONS:
                return config.abyssalDemonsGear();
            case ANKOU:
                return config.ankouGear();
            case AVIANSIE:
                return config.aviansieGear();
            case BANDITS:
                return config.banditGear();
            case BEARS:
            case BEARS_TURAEL:
                return config.bearGear();
            case BLACK_DEMONS:
                return config.blackDemonGear();
            case BLACK_DRAGONS:
                return config.blackDragonGear();
            case BLACK_KNIGHTS:
                return config.blackKnightGear();
            case BLOODVELD:
                return config.bloodveldGear();
            case CHAOS_DRUIDS:
                return config.chaosDruidGear();
            case DARK_WARRIORS:
                return config.darkWarriorGear();
            case DUST_DEVILS:
                return config.dustDevilGear();
            case EARTH_WARRIORS:
                return config.earthWarriorGear();
            case ENTS:
                return config.entGear();
            case FIRE_GIANTS:
                return config.fireGiantGear();
            case GREATER_DEMONS:
                return config.greaterDemonGear();
            case GREEN_DRAGONS:
                return config.greenDragonGear();
            case HELLHOUNDS:
                return config.hellhoundGear();
            case HILL_GIANTS:
                return config.hillGiantGear();
            case ICE_GIANTS:
                return config.iceGiantGear();
            case ICE_WARRIORS:
                return config.iceWarriorGear();
            case JELLIES:
                return config.jelliesGear();
            case LAVA_DRAGONS:
                return config.lavaDragonGear();
            case LESSER_DEMONS:
                return config.lesserDemonGear();
            case MAGIC_AXES:
                return config.magicAxesGear();
            case MAMMOTHS:
                return config.mammothGear();
            case MOSS_GIANTS:
                return config.mossGiantGear();
            case NECHRYAELS:
                return config.nechryaelGear();
            case PIRATES:
                return config.pirateGear();
            case REVENANTS:
                return config.revenantsGear();
            case ROGUES:
                return config.roguesGear();
            case SCORPIONS:
                return config.scorpionsGear();
            case SKELETONS:
                return config.skeletonsGear();
            case SPIDERS:
                return config.spidersGear();
            case SPIRITUAL_CREATURES:
                return config.spiritualCreaturesGear();
            case ZOMBIES:
                return config.zombiesGear();
            default:
                System.out.println("No specific gear configured for " + monster.getNpcName());
                return AttackType.MELEE;
        }
    }
    private int lastCameraAdjustTick = 0;
    private int nextTickThreshold = 30;
    public void updateCameraYawEveryRandomTicks() {
        int currentTick = PolarAPI.getClient().getTickCount();
        if (currentTick - lastCameraAdjustTick >= nextTickThreshold) {
            nextTickThreshold = PolarAPI.random(30, 60);
            PolarAPI.getClient().setCameraYawTarget(PolarAPI.random(0, 150));
            lastCameraAdjustTick = currentTick;
            log.info("Camera yaw adjusted at tick: " + currentTick);
        }
    }
    private int lastTabChangeTick = 0;
    private int nextTabChangeThreshold = 0;

    public void openRandomTabAtRandomIntervals() {
        int currentTick = PolarAPI.getClient().getTickCount();
        if (currentTick >= lastTabChangeTick + nextTabChangeThreshold) {
            nextTabChangeThreshold = PolarAPI.random(121, 652);
            switch (PolarAPI.random(0, 2)) {
                case 0:
                    TabUtil.Open(TabType.Prayer);
                    break;
                case 1:
                    TabUtil.Open(TabType.Inventory);
                    break;
                case 2:
                    TabUtil.Open(TabType.SpellBook);
                    break;
            }
            log.info("Tab changed at tick: " + currentTick);
            lastTabChangeTick = currentTick;
        }
    }
    private int nextCameraChangerTick = 0;  // Initialize when the next camera change should happen
    private int lastCameraChangerTick = 0;  // Initialize a counter to keep track of the last tick when a tab was changed.
    public void randomCameraDirection() {
        int currentTick = PolarAPI.getClient().getTickCount();  // Get current game tick
        if (currentTick >= lastTabChangeTick + nextTabChangeThreshold) {
            // Determine the next time to change the camera direction
            nextCameraChangerTick = currentTick + PolarAPI.random(78, 500);
            switch (PolarAPI.random(0, 3)) {
                case 0:
                    PolarAPI.setCameraNorth();
                    break;
                case 1:
                    PolarAPI.setCameraEast();
                    break;
                case 2:
                    PolarAPI.setCameraSouth();
                    break;
                case 3:
                    PolarAPI.setCameraWest();
                    break;
            }
        }
    }
public void handleLootingBag() {
    if (LBWidgetVisible() && isBankOpen()) {
        BankInventoryInteraction.useItem("Looting bag", "View");
    }
    if (LBDepositWidgetVisible() && isBankOpen()) {
        depositLootIfPossible();
    }
    if (LBCloseWidgetVisible() && isBankOpen()) {
        closeLB();
    }
}
    public boolean shouldUseCannon(MonsterRules currentMonster) {
            if (currentMonster == null) {
                log.warn("No current monster task set.");
                return false;
            }

            String npcName = currentMonster.getNpcName().toLowerCase(); // Normalize the name
            switch (npcName) {
                case "abyssal demon":
                    return config.useCannonAbyssalDemons();
                case "ankou":
                    return config.useCannonAnkou();
                case "aviansie":
                    return config.useCannonAviansie();
                case "bandit":
                    return config.useCannonBandits();
                case "grizzly bear":
                case "black bear":
                    return config.useCannonBears();
                case "black demon":
                    return config.useCannonBlackDemons();
                case "black dragon":
                    return config.useCannonBlackDragons();
                case "black knight":
                    return config.useCannonBlackKnights();
                case "bloodveld":
                    return config.useCannonBloodveld();
                case "chaos druid":
                    return config.useCannonChaosDruids();
                case "dark warrior":
                    return config.useCannonDarkWarriors();
                case "dust devil":
                    return config.useCannonDustDevils();
                case "earth warrior":
                    return config.useCannonEarthWarriors();
                case "ent":
                    return config.useCannonEnts();
                case "fire giant":
                    return config.useCannonFireGiants();
                case "greater demon":
                    return config.useCannonGreaterDemons();
                case "green dragon":
                    return config.useCannonGreenDragons();
                case "hellhound":
                    return config.useCannonHellhounds();
                case "hill giant":
                    return config.useCannonHillGiants();
                case "ice giant":
                    return config.useCannonIceGiants();
                case "ice warrior":
                    return config.useCannonIceWarriors();
                case "jelly":
                    return config.useCannonJellies();
                case "lava dragon":
                    return config.useCannonLavaDragons();
                case "lesser demon":
                    return config.useCannonLesserDemons();
                case "magic axe":
                    return config.useCannonMagicAxes();
                case "mammoth":
                    return config.useCannonMammoths();
                case "moss giant":
                    return config.useCannonMossGiants();
                case "greater nechryael":
                    return config.useCannonNechryael();
                case "pirate":
                    return config.useCannonPirates();
                case "rogue":
                    return config.useCannonRogue();
                case "scorpion":
                    return config.useCannonScorpion();
                case "skeleton":
                    return config.useCannonSkeleton();
                case "deadly red spider":
                    return config.useCannonSpiders();
                case "spiritual warrior":
                    return config.useCannonSpiritualCreatures();
                case "zombie":
                    return config.useCannonZombies();
                default:
                    log.info("No specific cannon config found for " + npcName);
                    return false;
            }
        }
    @Subscribe
    private void onPlayerDespawned(PlayerDespawned playerDespawned) {
        Player localPlayer = client.getLocalPlayer();
        Player despawnedPlayer = playerDespawned.getPlayer();
        if (despawnedPlayer == null) {
            return;
        }
        if (playerDespawned.getPlayer() == localPlayer) {
            return;
        }
        if (lastAttackedPlayer == despawnedPlayer) {
            lastAttackedPlayer = null;
            escape = false;
            logout = false;
        }
        if (pkQueue.contains(despawnedPlayer)) {
            pkQueue.remove(despawnedPlayer);
            escape = false;
            logout = false;
            log.info("Removed: {} from the pkQueue!", despawnedPlayer.getName());
        }
    }
    @Subscribe
    private void onPlayerSpawned(PlayerSpawned playerSpawned) {

        Player localPlayer = client.getLocalPlayer();
        Player spawnedPlayer = playerSpawned.getPlayer();
        if (localPlayer == null || !PolarAPI.isInWilderness()) {
            return;
        }
        if (playerSpawned.getPlayer() == localPlayer) {
            return;
        }
        int playerCombatLevel = spawnedPlayer.getCombatLevel();
        if (playerCombatLevel >= minWildernessLevel && playerCombatLevel <= maxWildernessLevel && isPkerUsingWeaponForEscape(spawnedPlayer) && lastAttackedPlayer == null) {
            pkQueue.add(spawnedPlayer);
            lastAttackedPlayer = spawnedPlayer;
            escape = true;
            log.info("Added: {} to the pkQueue!", spawnedPlayer.getName());
        }
    }
    public boolean isPkerUsingWeaponForEscape(Player player) {
        if (player == null || player.getPlayerComposition() == null) {
            log.warn("Player or player composition is null.");
            return false;
        }
        int pkerWeaponID = player.getPlayerComposition().getEquipmentId(KitType.WEAPON);
        ItemComposition pkerWeaponComp = client.getItemDefinition(pkerWeaponID);
        if (pkerWeaponComp == null) {
            log.warn("Weapon composition could not be retrieved.");
            return false;
        }
        String weaponName = pkerWeaponComp.getName().toLowerCase();
        return weaponName.contains("maul") || weaponName.contains("dragon mace") || weaponName.contains("dagger")
                || weaponName.contains("claws") || weaponName.contains("godsword") || weaponName.contains("scimitar")
                || weaponName.contains("knife") || weaponName.contains("ballista")
                || weaponName.contains("blowpipe")
                || weaponName.contains("dart") || weaponName.contains("voidwaker") || weaponName.contains("sword")
                || weaponName.contains("rapier") || weaponName.contains("whip") || weaponName.contains("bludgeon")
                || weaponName.contains("halberd") || weaponName.contains("hasta") || weaponName.contains("scythe")
                || weaponName.contains("sceptre") || weaponName.contains("wand") || weaponName.contains("staff")
                || weaponName.contains("battlestaff") || weaponName.contains("elder maul")
                || weaponName.contains("warhammer") || weaponName.contains("crossbow") || weaponName.contains("spear") || weaponName.contains("faerdhinen")
                || weaponName.contains("dharok") || weaponName.contains("nunchaku") || weaponName.contains("dual") || weaponName.contains("atlatl")
                || weaponName.contains("noxious halberd");
    }


}