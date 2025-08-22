package net.runelite.client.live.inDevelopment.biggs.BiggsPhosani;

import com.google.inject.Provides;
import io.reactivex.rxjava3.annotations.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.live.breakhandler.PolarBreakHandler.PolarBreakHandler;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.Equipment;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.Inventory;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.NPCs;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.TileObjects;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.query.EquipmentItemQuery;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.query.NPCQuery;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.GraphicsObjects;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtility.Util.PrayerUtil;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.InventoryInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.NPCInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.TileObjectInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.Packets.WidgetPackets;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@PluginDescriptor(
        name = "BiggsPhosani",
        description = "Automates Phosani's Nightmare",
        enabledByDefault = false,
        tags = {"phosani", "combat"}
)
public class BiggsPhosaniPlugin extends Plugin {

    public static final String GROUP_NAME = "phosani";

    @Inject
    private Client client;

    @Inject
    private BiggsPhosaniConfig config;

    @Inject
    private BiggsPhosaniOverlay overlay;

    @Inject
    private DebugOverlay debugOverlay;

    @Inject
    private PolarBreakHandler breakHandler;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ClientThread clientThread;

    @Getter
    public boolean pluginRunning;
    @Setter
    @Getter
    public State currentState;
    public boolean restockComplete = false;
    @Getter
    private Set<LocalPoint> currentSafeQuadrant; // Store the current safe quadrant as LocalPoints
    public int chargeWaitTimer;
    private static final Set<WorldPoint> flowerSafeArea = new HashSet<>();
    @Setter
    @Getter
    public NPC targetedTotem;
    public int sleepwalkerCount = 0;
    public int sleepwalkersSpawnedStorage = 0;
    public int timeout = 0;
    public int nextAttackTick = 0;
    private int previousRangedExp = 0;
    private int previousStrengthExp = 0;
    private int previousAttackExp = 0;
    private int previousDefenceExp = 0;
    public boolean fightingPhosani;

    public int killCount;
    @Getter
    public Instant fightStartTime;
    public Duration bestFightTime = null; // Null means no best time yet
    public Duration currentFightTime = Duration.ZERO; // Reset after each fight
    public WorldArea TOB_BANK_AREA = new WorldArea(3643, 3204, 41, 30, 0);
    public WorldArea TOB_BANK = new WorldArea(3647, 3204, 7, 8, 0);
    public WorldArea NIGHTMARE_PRE_LOBBY = new WorldArea(3717, 9685, 128, 82, 1);
    public WorldArea NIGHTMARE_LOBBY = new WorldArea(3799, 9769, 19, 19, 1);
    public WorldArea NIGHTMARE_COMBAT_AREA = new WorldArea(3861, 9940, 23, 23, 3);
    private final LocalPoint MIDDLE_LOCATION = new LocalPoint(6208, 8128);
    public WorldPoint customMiddle = new WorldPoint(3871, 9951, 3);

    private final ExecutorService pathfindingExecutor = Executors.newSingleThreadExecutor();

    @Setter
    @Getter
    private LocalPoint currentSafeTile;

    public static final int SOUTH_WEST_PILLAR = 9434;

    public WorldArea SLEPE_STAIRS = new WorldArea(3719, 3294, 19, 18, 0);

    public WorldPoint tobBank = new WorldPoint(3650, 3210,0);
    public boolean waitingForWalkers = false;
    private Set<WorldPoint> cachedBadTiles = new HashSet<>();
    private final Set<Integer> attackedSleepwalkers = new HashSet<>();
    private boolean badTilesDirty = true; // Tracks if the cache needs updating

    public WorldPoint slepeStairs = new WorldPoint(3728,3303,0);
    public WorldPoint nightmareLobby = new WorldPoint(3808, 9778, 1);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> currentTask = null; // Track the current pathfinding task
    // Totem
   // private static final List<Integer> INACTIVE_TOTEMS = Arrays.asList(9435, 9438, 9441, 9444);
    private static final List<Integer> KILLABLE_TOTEMS = Arrays.asList(9438, 9435, 9441, 9444);

    private static final int SOUTHEAST_TOTEM = 9438;
    private static final int SOUTHWEST_TOTEM = 9435;
    private static final int NORTHWEST_TOTEM = 9441;
    private static final int NORTHEAST_TOTEM = 9444;

    private static final int REGION_ID = 15515;

    private final WorldPoint SOUTH_EAST_PILLAR = WorldPoint.fromRegion(REGION_ID, 40, 23, 3);
    private final WorldPoint NORTH_EAST_PILLAR = WorldPoint.fromRegion(REGION_ID, 40, 39, 3);
    private final WorldPoint NORTH_WEST_PILLAR = WorldPoint.fromRegion(REGION_ID, 24, 39, 3);
    private final WorldPoint SOUTH_WEST_TOTEM = WorldPoint.fromRegion(REGION_ID, 24, 23, 3);

    private final List<WorldPoint> PILLARS = Arrays.asList(
            SOUTH_EAST_PILLAR,
            NORTH_EAST_PILLAR,
            NORTH_WEST_PILLAR,
            SOUTH_WEST_TOTEM
    );

    // Nightmare NPC States
    private static final int NIGHTMARE_ALIVE = 9417; // Nightmare is alive
    private static final int NIGHTMARE_DEAD = 9423; // Nightmare is dead

    // Nightmare Animations
    private static final int ANIM_MELEE_ATTACK = 8594; // Melee attack animation
    private static final int ANIM_MAGIC_ATTACK = 8595; // Magic attack animation
    private static final int ANIM_RANGED_ATTACK = 8596; // Ranged attack animation
    private static final int ANIM_DARK_HOLE_PHASE = 8598; // Dark Hole phase animation
    private static final int ANIM_HUSK_PHASE = 8605; // Husk phase animation
    private static final int CHARGE_ANIMATION_ID = 8609; // Charge
    private static final int CHARGEATTACK_ANIMATION_ID = 8597; // Charge
    private static final int ANIM_FINISHED_SLEEPWALKERS = 8604;

    // Game Objects
    private static final int BAD_FLOWER = 37741; //bad game object
    private static final int BAD_FLOWER_2 = 37742; //bad game object

    private static final int GOOD_FLOWER = 37744;
    private static final int GOOD_FLOWER_2 = 37745;

    private static final int MUSHROOM = 37739;
    private static final int PRE_SHROOM = 37738;

    public boolean isMovingToTile = false;
    // Graphics Objects
    private static final int GRAPHICS_DODGE_PORTAL = 1767; // Bad tile graphics object

    // Husk NPC
    private static final String HUSK_NPC_NAME = "Husk";
    private static final int HUSK_RANGED = 9467;
    private static final int HUSK_MAGIC = 9466;

    // Parasite
    private static final String PARASITE_NPC_NAME = "Parasite";
    private static final int NIGHTMARE_PARASITE_TOSS = 8606;

    // Sleepwalkers
    private static final int SLEEPWALKER_PLAYER_BLOWPIPE_ANIM = 5061;
    private static final int SLEEPWALKER_PLAYER_HAS_YEETED_ANIM = 401;




    public boolean isInChargePhase = false;
    private final Map<WorldPoint, Integer> dodgePortals = new HashMap<>();
    public boolean backwardsPrayers = false;
    public final Set<WorldPoint> chargeTiles = new HashSet<>();
    public boolean pregnant;
    public NPC closestSleepwalker = getClosestSleepwalker();
    @Getter
    @Setter
    private String currentQuadrantName = "None";
    @Getter
    @Setter
    private List<LocalPoint> currentPath = new ArrayList<>();
    @Getter
    @Setter
    private Map<LocalPoint, Integer> pathScores = new HashMap<>();
    public final Map<Integer, NPC> activeSleepwalkers = new HashMap<>();
    public final Map<NPC, Boolean> sleepwalkers = new HashMap<>();
    public int totalSleepwalkersKilled = 0; // Total Sleepwalkers killed
    @Setter
    @Getter
    public int Stage = 0;        // Current stage (1-4)
    private boolean hasMovedToSafeTile;
    private int ticksSinceChargePhase;
    private boolean isSpecialAttackEnabled;

    public int getRemainingSleepwalkers() {
        return (int) activeSleepwalkers.values().stream()
                .filter(npc -> !npc.isDead())
                .count(); //Remaining sleepwalkers
    }

    @Setter
    @Getter
    private Rectangle currentSafeQuadrantBounds;

    public NPC getNightmare() {
        return NPCs.search().withName("Phosani's Nightmare").first().orElse(null);
    }

    public NPC getTotems() {
        return NPCs.search().nameContains("Totem").first().orElse(null);
    }
    public int sleepwalkersAttacked = 0;
    public int bankProgress = 0; // Tracks the progress of restocking
    public int attackCounter = 0; // Tracks the number of attacks
    @Getter
    public int traversalProgress = 0;
    private boolean enablePlugin = false;
    private WorldPoint safeTile;
    @Getter
    private Instant startTime;
    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    @Override
    protected void startUp() {
        pluginRunning = false;
        overlayManager.add(overlay);
        startTime = Instant.now();
        chargeTiles.clear();
        resetSleepwalkerTracker();
        resetSleepwalkerCount();
        overlayManager.add(debugOverlay);
        keyManager.registerKeyListener(pluginToggle);
        log.info("BiggsPhosani Plugin started.");
    }

    @Override
    protected void shutDown() {
        executor.shutdown();
        pluginRunning = false;
        chargeWaitTimer = 0;
        fightingPhosani = false;
        fightStartTime = null;
        currentFightTime = null;
        bestFightTime = null;
        Stage = 0;
        waitingForWalkers = false;
        pregnant = false;
        totalSleepwalkersKilled = 0;
        resetSleepwalkerTracker();
        resetSleepwalkerCount();
        bankProgress = 0;
        nextAttackTick = 0;
        timeout = 0;
        attackCounter = 0;
        currentQuadrantName = "None";
        currentPath = null;
        pathScores = null;
        chargeWaitTimer = 0;
        chargeTiles.clear();
        activeSleepwalkers.clear();
        sleepwalkers.clear();
        currentSafeQuadrant = null;
        currentSafeQuadrantBounds = null;
        safeTile = null;
        isInChargePhase = false;
        badTilesDirty = false;
        cachedBadTiles = null;
        traversalProgress = 0;
        backwardsPrayers = false;
        restockComplete = false;
        overlayManager.remove(overlay);
        overlayManager.remove(debugOverlay);
        keyManager.unregisterKeyListener(pluginToggle);
        log.info("BiggsPhosani Plugin stopped.");
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!enablePlugin || breakHandler.isBreakActive(this) || PolarAPI.notLoggedIn()) {
            return;
        }
        if (timeout > 0) {
            timeout--;
        }
        badTilesDirty = true;
        State nextState = getNextState();
        if (nextState != currentState) {
            log.info("Transitioning to state: {}", nextState);
            currentState = nextState;
        }
        if (chargeWaitTimer > 3) {
            chargeWaitTimer = 0;
        }
        dodgePortals.entrySet().removeIf(entry -> {
            int remainingTicks = entry.getValue() - 1;
            if (remainingTicks <= 0) {
            // log.info("Removing expired bad tile at {}", entry.getKey());
                return true;
            }
            entry.setValue(remainingTicks);
            return false;
        });
        handleState(currentState);
    }

    public void togglePlugin() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        enablePlugin = !enablePlugin;
        if (!enablePlugin) {
            log.info("BiggsPhosani disabled.");
            pluginRunning = false;
        } else {
            log.info("BiggsPhosani enabled.");
            pluginRunning = true;
        }
    }
    public List<NPC> getHusks() {
        return NPCs.search().withName("Husk").result();
    }
    public boolean isParasites() {
        return NPCs.search().withName("Parasite").first().isPresent();
    }

    public List<NPC> getParasites() {
        return NPCs.search().withName("Parasite").result();
    }
    @Provides
    BiggsPhosaniConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BiggsPhosaniConfig.class);
    }
    /** CODE BELOW **/

    private void handleState(State state) {
        switch (state) {
            case TOB_BANK:
              //log.info("State: TOB_BANK");
                handleBanking();
                break;

            case RESTOCKING:
             //log.info("State: RESTOCKING");
                restock();
                break;

            case NIGHTMARE_TRAVERSAL:
              //  log.info("State: NIGHTMARE_TRAVERSAL");
                handleNightmareTraversal();
                break;

            case NIGHTMARE_LOBBY:
             //   log.info("State: NIGHTMARE_LOBBY");
                enterNightmare();
                break;

            case NIGHTMARE_COMBAT:
                NPC nightmare = getNightmare();
              //  log.info("State: NIGHTMARE_COMBAT");
                handleNightmareCombat(nightmare);
                break;

            case POH_HEALING:
             //   log.info("State: POH_HEALING");
                healPool();
                break;

            case DRAKANS_RETURN:
                log.info("State: DRAKANS_RETURN");
              //  useDrakansTOB();
                break;

            case TIMEOUT:
                log.info("State: TIMEOUT");
                break;

            default:
                log.warn("Unhandled state: {}", state);
                break;
        }
    }

    private State getNextState() {
        if (isInTOBBankArea() && needsRestock()) {
            return State.RESTOCKING;
        }

        if (timeout > 0) {
            return State.TIMEOUT;
        }

        if (isInNightmare() && !needsRestock()) {
            return State.NIGHTMARE_COMBAT;
        }

        if (!isInNightmare() && !isInNightmareLobby() && !needsRestock()) {
            return State.NIGHTMARE_TRAVERSAL;
        }

        if (isInNightmareLobby() && !needsRestock()) {
            return State.NIGHTMARE_LOBBY;
        }

        if (needsRestock() && config.healAtPOH()) {
            return State.POH_HEALING;
        }

        if (config.healAtPOH() && isInPOH() && needsRestock() && isHealed() || isInNightmare() && needsRestock() && !config.healAtPOH() && !isFinalPhase(getNightmare()) || client.getBoostedSkillLevel(Skill.HITPOINTS) < 10 && Inventory.getItemAmount(config.foodType()) <= 2 && !isFinalPhase(getNightmare())) {
            return State.DRAKANS_RETURN;
        }

        return State.TIMEOUT;
    }

    /** METHODS **/

    private void handleNightmareTraversal() {
       //log.info("Handling Nightmare traversal... Progress: {}", traversalProgress);

        switch (traversalProgress) {
            case 0: // Step 0: Teleport to Slepe or walk to stairs
                if (config.useSlepe() && !PolarAPI.isPlayerInArea(SLEPE_STAIRS)) {
                    useSlepeTeleport();
                    traversalProgress++; // Move to the next step
                } else if (!PolarAPI.isPlayerInArea(SLEPE_STAIRS)) {
                    if (!PolarAPI.isMoving()) {
                        PolarAPI.walkRandom(slepeStairs);
                    }
                }

                if (PolarAPI.isPlayerInArea(SLEPE_STAIRS)) {
                    traversalProgress++; // Move to the next step once at Slepe stairs
                }
                break;

            case 1: // Step 1: Climb down the staircase
                if (PolarAPI.isPlayerInArea(SLEPE_STAIRS)) {
                    log.info("Climbing down the staircase.");
                    climbDownStairs();
                    if (PolarAPI.isPlayerInArea(NIGHTMARE_PRE_LOBBY)) {
                        traversalProgress++; // Move to the next step
                    }
                }
                break;

            case 2: // Step 2: Move to Nightmare lobby
                if (PolarAPI.isPlayerInArea(NIGHTMARE_PRE_LOBBY)) {
                    log.info("Moving to Nightmare Lobby.");
                        PolarAPI.moveRandom(nightmareLobby);
                }

                if (isInNightmareLobby() && PolarAPI.isPlayerNear(nightmareLobby, 5)) {
                    log.info("Successfully entered Nightmare Lobby.");
                }
                break;

            default: // Handle unexpected progression values
                log.warn("Unexpected traversal progress: {}", traversalProgress);
                traversalProgress = 0; // Reset progression for safety
                break;
        }
    }


    private void useSlepeTeleport() {
       // log.info("Using Slepe Tablet to teleport.");
        Inventory.search().nameContains("Drakan's medallion").first().ifPresent(item -> {
            InventoryInteraction.useItem(item, "Slepe");
        });
    }

    private void enterNightmare() {
      //  log.info("Entering Nightmare...");
        TileObjects.search()
                .withName("Pool of Nightmares")
                .nearestToPlayer()
                .ifPresent(tileObject -> TileObjectInteraction.interact(tileObject, "Drink-from"));
    }
    private void moveToSafeTileNearNightmare(WorldPoint playerLocation) {
        Set<LocalPoint> safeTiles = getSafeTiles();
        if (safeTiles.isEmpty()) {
            log.warn("No safe tiles available near the Nightmare.");
            return;
        }

        //Use the custom middle point instead of MIDDLE_LOCATION.
        WorldPoint customMiddle = new WorldPoint(3871, 9951, 3);
        LocalPoint middleLocalPoint = LocalPoint.fromWorld(client, customMiddle);
        if (middleLocalPoint == null) {
            log.warn("Custom middle local point is null.");
            return;
        }

        //Check if the player is already on a safe tile
        LocalPoint currentLocal = LocalPoint.fromWorld(client, playerLocation);
        if (currentLocal != null && safeTiles.contains(currentLocal)) {
            return;
        }

        //DefineD threshold in local units (e.g., 300 units ~ 3 tiles; adjust as needed)
        final int threshold = 300;

        // Find the closest safe tile that is within the threshold of the custom middle point.
        LocalPoint closestSafeTile = safeTiles.stream()
                .filter(tile -> tile.distanceTo(middleLocalPoint) <= threshold)
                .filter(tile -> !isBadTileLocal(tile)) // ensure it's not a bad tile
                .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocal)))
                .orElse(null);

        if (closestSafeTile != null) {
            log.info("Moving to closest safe tile near the Nightmare: {}", closestSafeTile);
            PolarAPI.move(WorldPoint.fromLocal(client, closestSafeTile));
        } else {
            log.warn("No safe tiles found within {} units of the custom middle point.", threshold);
        }
    }

    private void handleNightmareCombat(NPC nightmare) {
        if (nightmare == null) {
            return;
        }
        if (!isChargePhase(nightmare)) {
            chargeTiles.clear();
            isInChargePhase = false; //Reset the flag!!!!!!!!!!!!!!!!!!!!!!!
            hasMovedToSafeTile = false;
        }
        if (nightmare.isDead() || !isInNightmare()) {
            resetSleepwalkerCount();
            PrayerUtil.TurnOffProtection();
        }


// Check if player should move briefly around the middle location
        if (client != null && client.getLocalPlayer() != null && nightmare != null) {
            WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

            // Check for proximity to specified objects
            boolean isNearSpecifiedObject = TileObjects.search()
                    .withIds(20980, 27122) // Specified object IDs
                    .result()
                    .stream()
                    .anyMatch(tileObject -> tileObject.getWorldLocation().distanceTo(playerLocation) <= 3);

            // Check for proximity to any "Totem" NPC
            boolean isNearTotem = NPCs.search()
                    .withName("Totem")
                    .result()
                    .stream()
                    .anyMatch(npc -> npc.getWorldLocation().distanceTo(playerLocation) <= 3);

            // Only proceed if not in Flower Phase, near a totem or specified object, and avoid certain phases
            if (!isFlowerPhase(nightmare) && (isNearTotem || isNearSpecifiedObject) &&
                    !holeSpawned() && !isParasitePhase(nightmare) && !isHuskPhase(nightmare) && !isMushroomPhase(nightmare)) {

                if (isNearTotem) {
                    // Handle logic when near a totem
                    log.info("Player is near a Totem. Moving to a safe tile further away.");
                    NPC closestTotem = NPCs.search().withName("Totem").nearestToPlayer().orElse(null);
                    if (closestTotem != null) {
                        moveToSafeTileNearNightmare(playerLocation);
                    }
                } else {
                    // Handle logic when near a specified object
                    log.info("Player is near a specified object. Moving to a safe tile.");
                    moveToSafeTileNearNightmare(playerLocation);
                }
            }
        }


        if (waitingForWalkers) {
            PolarAPI.swapGear(config.sleepwalkerGear());
            log.info("Waiting for Walkers...");
            return;
        }

        if (needsToEat() && hasFood()) {
            eatFood();
        }

        if (needsToRestorePrayer() && hasPrayerRestore()) {
            drinkPrayerRestore();
        }

        int realStrengthLevel = client.getRealSkillLevel(Skill.STRENGTH);
        int boostedStrengthLevel = client.getBoostedSkillLevel(Skill.STRENGTH);

        if (boostedStrengthLevel <= realStrengthLevel && Inventory.search().nameContains(config.boostType()).first().isPresent() || boostedStrengthLevel == realStrengthLevel + config.boostWhen() && Inventory.search().nameContains(config.boostType()).first().isPresent()) {
            PolarAPI.drinkPotionFromLowestDose(config.boostType());
            log.info("Boosted Strength level is low. Drinking {}.", config.boostType());
        }


        if (!isFlowerPhase(nightmare)) {
            setCurrentSafeQuadrant(null); // Clear the overlay
            setCurrentSafeQuadrantBounds(null);
        }
        if (isSleepwalkerPhase(nightmare)) {
        //    log.info("Detected Sleepwalker Phase.");
            handleSleepwalkerPhase();
            return;
        }
        if (!isSleepwalkerPhase(nightmare)) { // Only handle the rest of the phases if the Sleepwalker Phase is not active
            resetSleepwalkerCount();
        }
        boolean isPietyActive = client.isPrayerActive(Prayer.PIETY);
        if (isPietyActive && !isHuskPhase(nightmare) && !isParasitePhase(nightmare)) {
            PrayerUtil.TogglePrayer(Prayer.PIETY, false);
        }
        // Handle phases in priority order
        if (isHuskPhase(nightmare)) {
          //  log.info("Detected Husk Phase.");
            PolarAPI.swapGear(config.huskGear());
            handleHuskPhase(nightmare);
            handleAttackPhase(nightmare);
            return; // Exit to prioritize Husk Phase
        }

        if (isParasitePhase(nightmare)) {
            //log.info("Detected Parasite Phase.");
            handleParasitePhase(nightmare);
            handleAttackPhase(nightmare);
            return; // Exit to prioritize Parasite Phase
        }

        if (isTotemPhase(nightmare)) {
        //    log.info("Detected Totem Phase.");
            handleTotemPhase(nightmare);
            handleAttackPhase(nightmare);
            return; // Exit to prioritize Totem Phase
        }

        if (isFlowerPhase(nightmare)) {
        //    log.info("Detected Flower Phase.");
            handleFlowerPhase(nightmare);
            return; // Exit to handle Flower Phase
        }

        if (isChargePhase(nightmare)) {
          //  log.info("Detected Charge Phase.");
                log.info("Charge Phase detected. Resetting tick counter.");
                ticksSinceChargePhase = 0; //
            handleChargePhase(nightmare);
            return;
        }

        if (isDarkHolePhase(nightmare)) {
         //   log.info("Detected Dark Hole Phase. Dodging bad tiles...");
            avoidBadTiles();
            return; // Exit to ensure Dark Hole handling
        }

        if (isMushroomPhase(nightmare)) {
         //   log.info("Detected Mushroom Phase.");
            avoidBadTiles();
            return; // Exit to ensure Mushroom handling
        }

        if (isFinalPhase(nightmare)) {
       //     log.info("Detected Final Phase.");
            handleFinalPhase();
            return; // Exit to prioritize Final Phase
        }

        // Handle attack phases (e.g., pray melee, magic, range)
        if (!isFinalPhase(nightmare)) {
            handleAttackPhase(nightmare);
        }

        // Handle Thralls (if applicable)
        if (config.useThralls()) {
            castThralls();
        }

        // Handle Pregnancy
        if (isPregnant()) {
            PolarAPI.drinkPotionFromLowestDose(config.cureType());
            log.info("Pregnant, using {} potion.", config.cureType());
        }

        // Default to attacking Nightmare if no phase action is required
        List<WorldPoint> safeBorderTiles = getSafeBorderTilesAroundPhosani();
        boolean isInSafeTile = currentSafeTile != null && !isBadTileLocal(currentSafeTile) &&
                safeBorderTiles.contains(WorldPoint.fromLocal(client, currentSafeTile));
        if (!isFlowerPhase(nightmare) && !isMushroomPhase(nightmare) &&
                !isChargePhase(nightmare) && !isHuskPhase(nightmare) &&
                !isDarkHolePhase(nightmare) && !isParasitePhase(nightmare) || (isFlowerPhase(nightmare) || isMushroomPhase(nightmare) || isDarkHolePhase(nightmare) && isInSafeTile)) {
         //   log.info("Not in a restricted phase. Safe to attack Nightmare.");
        } else {
            log.info("Current conditions prevent attacking the Nightmare.");
            return; // Do not proceed to attack if conditions aren't met
        }

        // Check if Nightmare is alive before proceeding
        if (nightmare == null || isNightmareDead(nightmare)) {
      //      log.warn("Nightmare is not alive. Exiting attack logic.");
            return;
        }

        // Handle gear swaps and attack logic
        if (!isTotemPhase(nightmare) && !isParasitePhase(nightmare) && !isHuskPhase(nightmare) && !isFinalPhase(nightmare)) {
        //    log.info("Swapping to main gear for attacking Nightmare.");
            PolarAPI.swapGear(config.mainGear());
        }

        if (!isChargePhase(nightmare) && nightmare.getAnimation() != CHARGE_ANIMATION_ID && !isInChargePhase && getNightmare().getAnimation() != ANIM_FINISHED_SLEEPWALKERS) {
            attackNightmare();
        }
    }
    // Method to attack the Nightmare
    private void attackNightmare() {
        if (chargeWaitTimer > 0) {
            log.info("Charge wait timer is active ({} ticks remaining). Skipping attack.", chargeWaitTimer);
            return;
        }
        NPCInteraction.interact("Phosani's Nightmare", "Attack");
    }

    private void handleHuskPhase(NPC nightmare) {
        if (nightmare == null) {
            log.warn("Nightmare NPC is null. Exiting attack phase handler.");
            return;
        }

        List<NPC> husks = NPCs.search().withIds(HUSK_RANGED, HUSK_MAGIC).result();
        if (husks.isEmpty()) {
            log.info("No Husk NPCs found. Exiting Husk Phase handler.");
            return;
        }
        boolean isPietyActive = client.isPrayerActive(Prayer.PIETY);
        if (!isPietyActive) {
            PrayerUtil.TogglePrayer(Prayer.PIETY, true);
        }
        // Determine current prayer and prioritize attack
        boolean isPrayingMagic = client.isPrayerActive(Prayer.PROTECT_FROM_MAGIC);
        boolean isPrayingRanged = client.isPrayerActive(Prayer.PROTECT_FROM_MISSILES);

        NPC prioritizedHusk = null;
        NPC secondaryHusk = null;

        for (NPC husk : husks) {
            if (husk.getId() == HUSK_MAGIC && isPrayingRanged) {
                prioritizedHusk = husk; // Attack Magic Husk first if praying Ranged
            } else if (husk.getId() == HUSK_RANGED && isPrayingMagic) {
                prioritizedHusk = husk; // Attack Ranged Husk first if praying Magic
            } else {
                secondaryHusk = husk; // Fallback to the other Husk
            }
        }

        // Swap gear and attack the prioritized Husk
        if (prioritizedHusk != null) {
            log.info("Attacking prioritized Husk: {}", prioritizedHusk.getName());
            PolarAPI.swapGear(config.huskGear());
            NPCInteraction.interact(prioritizedHusk, "Attack");
        } else if (secondaryHusk != null) {
            log.info("No prioritized Husk found. Attacking secondary Husk: {}", secondaryHusk.getName());
            PolarAPI.swapGear(config.huskGear());
            NPCInteraction.interact(secondaryHusk, "Attack");
        } else {
            log.warn("No valid Husk NPCs found to attack.");
        }
    }



    private void handleParasitePhase(NPC nightmare) {
        if (nightmare == null) {
            log.warn("Nightmare NPC is null. Exiting attack phase handler.");
            return;
        }
        boolean isPietyActive = client.isPrayerActive(Prayer.PIETY);
        if (!isPietyActive) {
            PrayerUtil.TogglePrayer(Prayer.PIETY, true);
        }
        PolarAPI.swapGear(config.parasiteGear());
        if (NPCs.search().withName(PARASITE_NPC_NAME).first().isPresent() && !isFlowerPhase(nightmare)) {
            int specialAttackPercent = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
            log.info("Special attack percent: {}", specialAttackPercent);
            if (specialAttackPercent >= config.specPercent() * 10 && config.specFaggot()) {
                doSpec();
                log.info("Special attack handleparasitephase. Specing!");
            }
            if (getGoodFlowers().isEmpty()) {
                NPCInteraction.interact(PARASITE_NPC_NAME, "Attack");
            }
        }
        avoidBadTiles();
        if (pregnant) {
            PolarAPI.drinkPotionFromLowestDose(config.cureType());
        }
    }


    private void handleAttackPhase(NPC nightmare) {
        if (nightmare == null) {
            log.warn("Nightmare NPC is null. Exiting attack phase handler.");
            return;
        }
        Prayer prayer = determinePrayer(nightmare);
        if (prayer == null) {
            log.debug("No valid prayer determined. Skipping toggling.");
            return;
        }
        int animation = nightmare.getAnimation();
       String prayerMode = backwardsPrayers ? "[Backwards Prayers]" : "[Regular Prayers]";
     //   log.info("{} Nightmare doing animation {}. Using prayer: {}", prayerMode, animation, prayer);
        // Toggle the prayer if it's not already active
        if (!client.isPrayerActive(prayer)) {
            log.debug("Toggling prayer: {}", prayer);
            PrayerUtil.Toggle(prayer);
        }
    }


    @Nullable
    private Prayer determinePrayer(NPC nightmare) {
        if (nightmare == null) {
            log.warn("Nightmare NPC is null. Cannot determine prayer.");
            return null;
        }

        int animation = nightmare.getAnimation();

        // Determine the correct prayer based on the animation and whether we're cursed
        switch (animation) {
            case ANIM_MELEE_ATTACK:
                return backwardsPrayers ? Prayer.PROTECT_FROM_MISSILES : Prayer.PROTECT_FROM_MELEE;

            case ANIM_MAGIC_ATTACK:
                return backwardsPrayers ? Prayer.PROTECT_FROM_MELEE : Prayer.PROTECT_FROM_MAGIC;

            case ANIM_RANGED_ATTACK:
                return backwardsPrayers ? Prayer.PROTECT_FROM_MAGIC : Prayer.PROTECT_FROM_MISSILES;

            default:
               // log.info("Unknown attack animation. Monitoring...");
                if (nightmare.getAnimation() == -1 && getHusks().isEmpty()) {
                    PrayerUtil.TurnOffProtection();
                }
                return null;
        }
    }

    private void useDrakansTOB() {
        log.info("Teleporting to TOB bank via Drakan's medallion...");
        Inventory.search().nameContains("Drakan's medallion").first().ifPresent(item -> {
            InventoryInteraction.useItem(item, "Ver sinhaza");
        });
    }
    private void castThralls() {
        log.info("Casting Thralls...");
        // Logic to cast Thralls
    }
    private void healPool() {
        log.info("Starting healing process...");

        // Step 1: Use "Teleport to house" if not already in the PoH
        if (!isInPOH()) {
            log.info("Not in PoH. Using 'Teleport to house'.");
            InventoryInteraction.useItem("Teleport to house", "Inside");
            return; // Wait for teleport to complete before proceeding
        }

        // Step 2: Use the pool to heal if in PoH
        if (isInPOH() && !isHealed()) {
            log.info("In PoH. Using pool to heal...");
            usePool();
            return; // Wait for healing to complete before proceeding
        }

        // Step 3: If healed, return to TOB
        if (isHealed()) {
            log.info("Healed successfully. Returning to TOB.");
            useDrakansTOB();
        }
    }

    private void usePool() {
        log.info("Using pool to heal...");
        TileObjects.search()
                .withName("Ornate pool of Rejuvenation")
                .nearestToPlayer()
                .ifPresent(tileObject -> TileObjectInteraction.interact(tileObject, "Drink"));
    }

    private void handleBanking() {
        log.info("Handling TOB bank area...");
        if (needsRestock()) {
            restock();
        }
    }

    private void restock() {
        log.info("Restocking supplies... Progress: {}", bankProgress);

        switch (bankProgress) {
            case 0: // Open Bank
                restockComplete = false; // Reset restocking completion flag
                if (PolarAPI.isPlayerInArea(TOB_BANK) && !PolarAPI.isBankOpen()) {
                    NPCInteraction.interact("Banker", "Bank");
                    return; // Wait for bank to open
                } else if (PolarAPI.isPlayerInArea(TOB_BANK_AREA) && !PolarAPI.isBankOpen()) {
                    log.info("Walking to bank.");
                    PolarAPI.walkRandom(tobBank);
                    return;
                }
                if (PolarAPI.isBankOpen()) {
                    bankProgress++; // Move to the next step
                }
                break;

            case 1: // Handle Stamina Potion
                if (config.useStamina() && client.getEnergy() < 70 * 100) {
                    log.info("Using Stamina Potion...");
                    log.info("Withdrawing Stamina Potion.");
                    PolarAPI.withdrawHighestPotionDoseContains("Stamina potion", 1);
                    InventoryInteraction.useItemContains("Stamina potion", "Drink");
                } else {
                    log.info("Opening bank.");
                    if (!PolarAPI.isBankOpen()) {
                    NPCInteraction.interact("Banker", "Bank");
                    bankProgress++;
                } else {
                        bankProgress++;
                    }
                }
                return; // Wait for stamina consumption before moving on

            case 2: // Deposit all but required gear
                if (PolarAPI.isPlayerInArea(TOB_BANK) && !PolarAPI.isBankOpen()) {
                    NPCInteraction.interact("Banker", "Bank");
                }
                if (PolarAPI.isBankOpen()) {
                    depositAllButConfigGear();
                }
                bankProgress++;
                break;
            case 3: // Withdraw Food
                if (needsMoreFood()) {
                    int foodToWithdraw = config.foodAmount() - Inventory.getItemAmount(config.foodType());
                    log.info("Withdrawing {}x {}", foodToWithdraw, config.foodType());
                    PolarAPI.withdraw(config.foodType(), foodToWithdraw);
                }
                bankProgress++;
                break;

            case 4: // Withdraw Prayer Restores
                if (needsMorePrayerRestores()) {
                    int prayerToWithdraw = config.prayerPotionAmount() - Inventory.getItemAmount(config.prayerRestoreType());
                    log.info("Withdrawing {}x {}", prayerToWithdraw, config.prayerRestoreType());
                    PolarAPI.withdrawHighestPotionDoseContains(config.prayerRestoreType(), prayerToWithdraw);
                }
                bankProgress++;
                break;

            case 5: // Withdraw Boost Potions
                if (needsMoreBoostPotions()) {
                    int boostToWithdraw = config.boostPotionAmount() - Inventory.getItemAmount(config.boostType());
                    log.info("Withdrawing {}x {}", boostToWithdraw, config.boostType());
                    PolarAPI.withdrawHighestPotionDoseContains(config.boostType(), boostToWithdraw);
                }
                bankProgress++;
                break;

            case 6: // Withdraw Cure Potions
                if (needsMoreCures()) {
                    int cureToWithdraw = config.curePotionAmount() - Inventory.getItemAmount(config.cureType());
                    log.info("Withdrawing {}x {}", cureToWithdraw, config.cureType());
                    PolarAPI.withdrawHighestPotionDoseContains(config.cureType(), cureToWithdraw);
                }
                bankProgress++;
                break;

            case 7: // Withdraw Drakan's Medallion
                if (!hasDrakansMedallion()) {
                    log.info("Withdrawing Drakan's Medallion.");
                    PolarAPI.withdraw("Drakan's medallion", 1);
                }
                bankProgress++;
                break;

            case 8: // Withdraw Rune Pouch
                if (!hasRunePouch() && config.useRunePouch()) {
                    log.info("Withdrawing Rune Pouch.");
                    PolarAPI.withdraw("Rune pouch", 1);
                }
                bankProgress++;
                client.runScript(101, -1); // Final bank refresh
                break;

            case 9: // Withdraw House Teleport if required
                if (config.healAtPOH() && Inventory.search().nameContains("house").first().isEmpty()) {
                    log.info("Withdrawing House teleport.");
                    PolarAPI.withdrawContainsNoCase("Teleport to house", 1);
                }
                bankProgress++;
                client.runScript(101, -1); // Final bank refresh
                break;

            case 10: // Complete Restocking
                PolarAPI.closeShop();
                client.runScript(101, -1); // Final bank refresh
                restockComplete = true; // Mark restocking as complete
                bankProgress = 0; // Reset progress
                log.info("Restocking complete.");
                break;

            default:
                log.warn("Unexpected bank progress state: {}", bankProgress);
                PolarAPI.closeShop();
                restockComplete = true; // Ensure proper state transition
                client.runScript(101, -1); // Final bank refresh
                bankProgress = 0;
                break;
        }
    }

    private void depositAllButConfigGear() {
        // Combine all gear configurations into a single list
        List<String> gearToKeep = new ArrayList<>();
        gearToKeep.addAll(Arrays.asList(config.huskGear().split(",")));
        gearToKeep.addAll(Arrays.asList(config.specGear().split(",")));
        gearToKeep.addAll(Arrays.asList(config.sleepwalkerGear().split(",")));
        gearToKeep.addAll(Arrays.asList(config.parasiteGear().split(",")));
        gearToKeep.addAll(Arrays.asList(config.totemGear().split(",")));
        gearToKeep.addAll(Arrays.asList(config.mainGear().split(",")));

        // Remove any unnecessary whitespace
        gearToKeep = gearToKeep.stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        // Deposit all except the gear to keep
        PolarAPI.depositAllBut(Arrays.toString(gearToKeep.toArray(new String[0])));
        log.info("Depositing all items except: {}", String.join(", ", gearToKeep));
    }
    private boolean isWearingMainGear() {
        // Get the list of gear names from config
        List<String> mainGear = Arrays.stream(config.mainGear().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Fetch the current equipped items
        EquipmentItemQuery equippedItems = Equipment.search();

        // Check if all items in mainGear are equipped
        return mainGear.stream().allMatch(item -> equippedItems.withName(item).first().isPresent());
    }


    private void climbDownStairs() {
      //  log.info("Climbing down stairs...");
        TileObjects.search().withName("Stairs").nearestToPlayer().ifPresent(tileObject -> {
            TileObjectInteraction.interact(tileObject, "Climb-down");
        });
    }

    private boolean needsRestock() {
        // Determine if we’re in final phase.
        boolean finalPhase = false;
        NPC nightmare = getNightmare();
        if (nightmare != null) {
            finalPhase = isFinalPhase(nightmare);
        }

        boolean needsRestock;
        if (finalPhase) {
            // In final phase, we only care about having:
            // - A Drakan's medallion,
            // - A Rune pouch (if config.useRunePouch() is true), and
            // - A house teleport (if config.healAtPOH() is true)
            needsRestock = !hasDrakansMedallion()
                    || (config.useRunePouch() && !hasRunePouch())
                    || needsHouseTele();;
        } else {
            // Otherwise, we check everything
            needsRestock = !hasDrakansMedallion()
                    || needsHouseTele()
                    || (config.useRunePouch() && !hasRunePouch())
                    || needsMoreFood()
                    || needsMorePrayerRestores()
                    || needsMoreBoostPotions()
                    || needsMoreCures();
        }

        if (needsRestock) {
            log.info("Missing:");
            if (!hasDrakansMedallion()) {
                log.info("  - Drakan medallion");
            }
            if (config.useRunePouch() && !hasRunePouch()) {
                log.info("  - Rune pouch");
            }
            if (finalPhase) {
                if (needsHouseTele()) {
                    log.info("  - House teleport");
                }
            } else {
                if (needsMoreFood()) {
                    log.info("  - Food");
                }
                if (needsMorePrayerRestores()) {
                    log.info("  - Prayer restores");
                }
                if (needsMoreBoostPotions()) {
                    log.info("  - Boost potions");
                }
                if (needsMoreCures()) {
                    log.info("  - Cures");
                }
                if (config.healAtPOH() && needsHouseTele()) {
                    log.info("  - House teleport");
                }
            }
        }

        return needsRestock;
    }

    private boolean needsToEat() {
        return client.getBoostedSkillLevel(Skill.HITPOINTS) < config.hpAmount() && !isSleepwalkerPhase(getNightmare());
    }

    private boolean hasFood() {
        return Inventory.search().nameContains(config.foodType()).first().isPresent();
    }

    private void eatFood() {
        InventoryInteraction.useItem(Inventory.search().nameContains(config.foodType()).first().get(), "Eat");
    }

    private boolean needsToRestorePrayer() {
        return client.getBoostedSkillLevel(Skill.PRAYER) < config.prayerAmount() && !isSleepwalkerPhase(getNightmare());
    }

    private boolean hasPrayerRestore() {
        return Inventory.search().nameContains(config.prayerRestoreType()).first().isPresent();
    }

    private void drinkPrayerRestore() {
        PolarAPI.drinkPotionFromLowestDose(config.prayerRestoreType());
    }
    public boolean needsMoreFood() {
        return Inventory.getItemAmount(config.foodType()) < config.minFoodToRestock();
    }

    public boolean needsHouseTele() {
        return Inventory.search().nameContains("house").first().isEmpty() && config.healAtPOH();
    }

    public boolean needsMorePrayerRestores() {
        return Inventory.getItemAmountNameContains(config.prayerRestoreType()) < config.minPrayerRestoreToRestock();
    }

    public boolean needsMoreBoostPotions() {
        return Inventory.getItemAmountNameContains(config.boostType()) < config.minBoostToRestock();
    }

    public boolean needsMoreCures() {
        return Inventory.getItemAmountNameContains(config.cureType()) < config.minCureToRestock();
    }
    public boolean isInTOBBankArea() {
        return PolarAPI.isPlayerInArea(TOB_BANK_AREA);
    }

    public boolean isInNightmareLobby() {
        return PolarAPI.isPlayerInArea(NIGHTMARE_LOBBY);
    }

    public boolean isInNightmare() {
        if (client.isInInstancedRegion()) {
            return true;
        }
        int regionId = client.getLocalPlayer().getWorldLocation().getRegionID();
        if (regionId == 15515) {
            return true;
        }
        if (NPCs.search().withId(SOUTH_WEST_PILLAR).first().isPresent()) {
            return true;
        }
        return false;
    }


    public boolean isInPOH() {
        return TileObjects.search().nameContains("ornate").first().isPresent();
    }
    public boolean hasDrakansMedallion() {
        return Inventory.search().nameContains("Drakan's medallion").first().isPresent();
    }
    public boolean hasRunePouch() {
        return Inventory.search().nameContains("Rune pouch").first().isPresent() || Inventory.search().nameContains("Divine rune pouch").first().isPresent();
    }

    public boolean isHealed() {
        int realHP = client.getRealSkillLevel(Skill.HITPOINTS);
        int boostedHP = client.getBoostedSkillLevel(Skill.HITPOINTS);

        int realPrayer = client.getRealSkillLevel(Skill.PRAYER);
        int boostedPrayer = client.getBoostedSkillLevel(Skill.PRAYER);

        return boostedHP >= realHP && boostedPrayer >= realPrayer;
    }
    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
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
    /** NIGHTMARE STATES **/
    private boolean isNightmareAlive(NPC nightmare) {
        return nightmare != null && nightmare.getId() == NIGHTMARE_ALIVE;
    }
    private boolean isNightmareDead(NPC nightmare) {
        return nightmare != null && nightmare.getId() == NIGHTMARE_DEAD;
    }
    /** NIGHTMARE ATTACKS **/
    private boolean isMeleeAttack(NPC nightmare) {
        return nightmare != null && nightmare.getAnimation() == ANIM_MELEE_ATTACK;
    }

    private boolean isMagicAttack(NPC nightmare) {
        return nightmare != null && nightmare.getAnimation() == ANIM_MAGIC_ATTACK;
    }

    private boolean isRangedAttack(NPC nightmare) {
        return nightmare != null && nightmare.getAnimation() == ANIM_RANGED_ATTACK;
    }
    /** NIGHTMARE PHASES **/
    public boolean isDarkHolePhase(NPC nightmare) {
        return nightmare != null && GraphicsObjects.search().withId(GRAPHICS_DODGE_PORTAL).first().isPresent() || nightmare != null && nightmare.getAnimation() == ANIM_DARK_HOLE_PHASE || holeSpawned();
    }

    public boolean isParasitePhase(NPC nightmare) {
        return nightmare != null && NPCs.search().withName(PARASITE_NPC_NAME).first().isPresent();
    }

    public boolean isHuskPhase(NPC nightmare) {
        return nightmare != null && NPCs.search().withName(HUSK_NPC_NAME).first().isPresent();
    }

    public boolean isChargePhase(NPC nightmare) {
        return nightmare != null && nightmare.getAnimation() == CHARGE_ANIMATION_ID;
    }

    public boolean isSleepwalkerPhase(NPC nightmare) {
        return NPCs.search().nameContains("Sleepwalker").withAction("Attack").first().isPresent() && !isFinalPhase(nightmare);
    }

    public boolean isFinalPhase(NPC nightmare) {
        return totalSleepwalkersKilled >= 10;
    }

    public boolean isFlowerPhase(NPC nightmare) {
        return nightmare != null &&
                (TileObjects.search().withId(GOOD_FLOWER).first().isPresent() ||
                        TileObjects.search().withId(GOOD_FLOWER_2).first().isPresent());
    }
    public boolean isTotemPhase(NPC nightmare) {
        for (int totemId : KILLABLE_TOTEMS) {
            if (!NPCs.search().withIds(totemId).result().isEmpty()) {
                return true; // Return true if any active totem is found
            }
        }

        return false; // No active totems found
    }


    public boolean isMushroomPhase(NPC nightmare) {
        return nightmare != null &&
                (!TileObjects.search().withId(MUSHROOM).result().isEmpty() || !TileObjects.search().withId(PRE_SHROOM).result().isEmpty());
    }
    private void resetSleepwalkerCount() {
        sleepwalkerCount = 0;
        sleepwalkersAttacked = 0;
        activeSleepwalkers.clear();
    }

    private void handleSleepwalkerPhase() {
        if (activeSleepwalkers.isEmpty()) {
            log.info("No active Sleepwalkers. Exiting Sleepwalker Phase.");
            resetSleepwalkerCount();
            sleepwalkersSpawnedStorage = 0;
            attackedSleepwalkers.clear();
            return;
        }

        // Store the number of spawned Sleepwalkers if not already stored
        if (sleepwalkersSpawnedStorage == 0) {
            sleepwalkersSpawnedStorage = activeSleepwalkers.size();
            log.info("Stored number of spawned Sleepwalkers: {}", sleepwalkersSpawnedStorage);
        }

        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        if (playerLocation == null) {
            log.warn("Player location is null. Cannot determine closest Sleepwalker.");
            return;
        }

        // Attack the closest Sleepwalker immediately if no attacks have been made
        if (sleepwalkersAttacked == 0) {
            NPC closestSleepwalker = activeSleepwalkers.values().stream()
                    .filter(npc -> !npc.isDead())
                    .filter(npc -> !attackedSleepwalkers.contains(npc.getIndex())) // Exclude already-attacked Sleepwalkers
                    .min(Comparator.comparingInt(npc -> npc.getWorldLocation().distanceTo(playerLocation))) // Compare by distance
                    .orElse(null);

            if (closestSleepwalker != null && !isFinalPhase(getNightmare())) {
                log.info("Swapping to Sleepwalker Gear for the first attack.");
                PolarAPI.swapGear(config.sleepwalkerGear());
                attackClosestSleepwalker(closestSleepwalker);
                attackedSleepwalkers.add(closestSleepwalker.getIndex());
                sleepwalkersAttacked++;
            }
        }

        // Exit if all spawned Sleepwalkers have been attacked
        if (sleepwalkersAttacked >= sleepwalkersSpawnedStorage) {
            log.info("All originally spawned Sleepwalkers attacked. Exiting Sleepwalker Phase.");
            sleepwalkersSpawnedStorage = 0;
            attackedSleepwalkers.clear();
        }
    }

    public void attackClosestSleepwalker(NPC sleepwalker) {
        if (sleepwalker == null) {
            log.info("No active Sleepwalker found to attack.");
            return;
        }

        log.info("Attacking Sleepwalker at: {}", sleepwalker.getWorldLocation());
        NPCs.interact(sleepwalker, "Attack");
    }



    private void handleFinalPhase() {
        if (!client.isPrayerActive(Prayer.REDEMPTION)) {
            PrayerUtil.TogglePrayer(Prayer.REDEMPTION, true);
        }
        finalPhase();
    }

    public void handleFlowerPhase(NPC nightmare) {
        if (!isFlowerPhase(nightmare)) {
            log.info("Not in Flower Phase. Skipping...");
            setCurrentSafeQuadrant(null); // Clear the overlay
            setCurrentSafeQuadrantBounds(null);
            return;
        }

        log.info("Handling Flower Phase...");

        Set<WorldPoint> flowers = getGoodFlowers();
        if (flowers.isEmpty()) {
            log.warn("No good flowers found. Staying in place.");
            return;
        }

        // Determine the safe quadrant bounds
        Rectangle safeQuadrantBounds = determineSafeQuadrantBounds(flowers);
        if (safeQuadrantBounds == null) {
            log.warn("Unable to determine safe quadrant bounds.");
            setCurrentSafeQuadrantBounds(null);
            return;
        }

        // Move to a safe tile within the quadrant
        setCurrentSafeQuadrantBounds(safeQuadrantBounds);
        moveToSafeTileInQuadrant(safeQuadrantBounds);
    }


    private void moveToSafeTileInQuadrant(Rectangle quadrantBounds) {
        LocalPoint currentLocation = client.getLocalPlayer().getLocalLocation();

        if (currentLocation == null) {
            log.warn("Player location is null. Cannot move to a safe tile.");
            return;
        }

        WorldPoint currentWorldPoint = client.getLocalPlayer().getWorldLocation();

        // Ensure the player is inside the safe quadrant
        if (currentWorldPoint != null
                && quadrantBounds.contains(currentWorldPoint.getX(), currentWorldPoint.getY())
                && !isBadTileLocal(currentLocation)) {
            log.info("Already on a safe tile within the quadrant and no bad tiles present. No movement needed.");

            // Check if the player is on a Nightmare border tile and attack if applicable
            List<WorldPoint> safeBorderTiles = getSafeBorderTilesAroundPhosani();
            if (safeBorderTiles.contains(currentWorldPoint) && isNightmareAttackable() && !isTotemPhase(getNightmare())) {
                log.info("On a safe border tile within the quadrant. Attacking Nightmare.");
                attackNightmare();
                log.info("Attacking nightmare in MoveToSafeTileInQuadrant.");
            }
            return;
        }

        // Handle Totem Phase logic
        if (isTotemPhase(getNightmare())) {
            NPC closestTotem = getClosestTotem();
            if (closestTotem != null) {
                WorldPoint totemWorldPoint = closestTotem.getWorldLocation();
                LocalPoint totemLocalPoint = LocalPoint.fromWorld(client, totemWorldPoint);

                // Find the closest safe tile near the totem and within the quadrant
                LocalPoint closestSafeTileNearTotem = getSafeTiles().stream()
                        .filter(tile -> quadrantBounds.contains(
                                WorldPoint.fromLocal(client, tile).getX(),
                                WorldPoint.fromLocal(client, tile).getY()))
                        .filter(tile -> tile.distanceTo(totemLocalPoint) <= 1152) // Only consider tiles near the totem
                        .filter(tile -> !isBadTileLocal(tile)) // Avoid bad tiles
                        .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocation)))
                        .orElse(null);

                if (closestSafeTileNearTotem != null && getGoodFlowers().isEmpty()) {
                    log.info("Moving to closest safe tile near totem within the quadrant: {}", closestSafeTileNearTotem);
                    PolarAPI.move(WorldPoint.fromLocal(client, closestSafeTileNearTotem));
                    return;
                }
            }
        }

        // Get safe border tiles within the quadrant
        List<WorldPoint> safeBorderTiles = getSafeBorderTilesAroundPhosani().stream()
                .filter(tile -> quadrantBounds.contains(tile.getX(), tile.getY()))
                .collect(Collectors.toList());

        // Find the closest safe border tile
        LocalPoint closestBorderTile = safeBorderTiles.stream()
                .map(tile -> LocalPoint.fromWorld(client, tile))
                .filter(Objects::nonNull)
                .filter(tile -> !isBadTileLocal(tile))
                .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocation)))
                .orElse(null);

        // If a suitable border tile exists, prioritize it
        if (closestBorderTile != null) {
            log.info("Moving to closest safe border tile within the quadrant: {}", closestBorderTile);
            PolarAPI.move(WorldPoint.fromLocal(client, closestBorderTile));
            return;
        }

        // Find the closest regular safe tile explicitly within the quadrant
        LocalPoint closestSafeTile = null;
        int closestDistance = Integer.MAX_VALUE;

        for (int x = quadrantBounds.x; x < quadrantBounds.x + quadrantBounds.width; x++) {
            for (int y = quadrantBounds.y; y < quadrantBounds.y + quadrantBounds.height; y++) {
                WorldPoint worldPoint = new WorldPoint(x, y, client.getPlane());
                LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);

                // Validate the tile is within the quadrant and not a bad tile
                if (localPoint != null && quadrantBounds.contains(worldPoint.getX(), worldPoint.getY())
                        && !isBadTileLocal(localPoint)) {
                    int distance = localPoint.distanceTo(currentLocation);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestSafeTile = localPoint;
                    }
                }
            }
        }

        if (closestSafeTile != null) {
            log.info("Moving to closest safe tile explicitly within the quadrant: {}", closestSafeTile);
            PolarAPI.move(WorldPoint.fromLocal(client, closestSafeTile));
        } else {
            log.warn("No safe tiles found explicitly within the quadrant.");
        }
    }


    private boolean isNightmareAttackable() {
        return !isParasites() && !isHuskPhase(getNightmare()) && !isChargePhase(getNightmare()) && !isInChargePhase;
    }

    @Subscribe
    public void onStatChanged(StatChanged event) {
        if (!isSleepwalkerPhase(getNightmare())) {
            return; // Ensure this logic only executes during the Sleepwalker Phase
        }

        Skill skill = event.getSkill();
        int experience = event.getXp();

        log.info("Experience gained in skill {}: {} - checking for attack condition", skill.getName(), experience);

        // Check for Ranged or Defence experience drops
        boolean expIncreased = false;
        if ((skill == Skill.RANGED && experience > previousRangedExp)) {
            log.info("Detected an exp drop in Ranged during Sleepwalker Phase. - We should attack the next Sleepwalker.");
            previousRangedExp = experience;
            expIncreased = true;
        } else if (skill == Skill.DEFENCE && experience > previousDefenceExp) {
            log.info("Detected an exp drop in Defence during Sleepwalker Phase. - We should attack the next Sleepwalker.");
            previousDefenceExp = experience;
            expIncreased = true;
        }

        // Attack the next Sleepwalker only if either Ranged or Defence exp increased
        if (expIncreased && !isFinalPhase(getNightmare())) {
            attackNextSleepwalker();
        }
    }


    private void attackNextSleepwalker() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

        if (playerLocation == null) {
            log.warn("Player location is null. Cannot determine closest Sleepwalker.");
            return;
        }

        //Find the closest Sleepwalker by WorldPoint distance
        NPC closestSleepwalker = activeSleepwalkers.values().stream()
                .filter(npc -> !npc.isDead())
                .filter(npc -> !attackedSleepwalkers.contains(npc.getIndex())) // Exclude already-attacked Sleepwalkers
                .min(Comparator.comparingInt(npc -> npc.getWorldLocation().distanceTo(playerLocation))) // Compare by distance
                .orElse(null);

        if (closestSleepwalker != null) {
            //Use huskGear only if attacking the last Sleepwalker and more than 2 spawned
            if (sleepwalkersAttacked == sleepwalkersSpawnedStorage - 1 && sleepwalkersSpawnedStorage > 2) {
                log.info("Swapping to Husk Gear for the last Sleepwalker.");
                PolarAPI.swapGear(config.huskGear());
            } else {
                log.info("Swapping to Sleepwalker Gear for Sleepwalker {}", sleepwalkersAttacked + 1);
                PolarAPI.swapGear(config.sleepwalkerGear());
            }

            //Attack the closest Sleepwalker
            log.info("Attacking closest Sleepwalker: {} at {}", closestSleepwalker.getIndex(), closestSleepwalker.getWorldLocation());
            attackClosestSleepwalker(closestSleepwalker);

            //Mark this Sleepwalker as attacked
            attackedSleepwalkers.add(closestSleepwalker.getIndex());
            sleepwalkersAttacked++;
        } else {
            log.warn("No un-attacked Sleepwalkers available.");
        }
    }

    public void handleTotemPhase(NPC nightmare) {
        if (!isTotemPhase(nightmare)) {
            log.info("Not in Totem Phase. Skipping...");
            setCurrentSafeQuadrant(null);
            setCurrentSafeQuadrantBounds(null);
            return;
        }

        if (isPregnant()) {
            PolarAPI.drinkPotionFromLowestDose(config.cureType());
        }

        NPC closestTotem = getClosestTotem();
        if (closestTotem == null) {
            log.info("No active totems found.");
            setTargetedTotem(null);
            return;
        }

        if (!isParasitePhase(nightmare) && !isHuskPhase(nightmare)) {
            PolarAPI.swapGear(config.totemGear());
        }

        if (isChargePhase(nightmare) || nightmare.getAnimation() == 8597) {
            calculateChargeDangerZone(nightmare); //Update danger zone
            avoidBadTiles();
            ticksSinceChargePhase = 0;
        }

        if (!isChargePhase(nightmare)) {
            chargeTiles.clear();
        }

        setTargetedTotem(closestTotem); //Set targeted totem

        WorldPoint totemWorldPoint = closestTotem.getWorldLocation();
        if (totemWorldPoint == null) {
            log.warn("Could not determine the WorldPoint for the closest totem.");
            return;
        }

        Rectangle safeQuadrantBounds = null;

        //Only determine safe quadrant bounds if in Flower Phase
        if (isFlowerPhase(nightmare)) {
            log.info("Flower Phase detected. Determining safe quadrant bounds...");
            Set<WorldPoint> flowers = getGoodFlowers();
            if (flowers.isEmpty()) {
                log.warn("No good flowers found. Skipping quadrant calculation.");
            } else {
                safeQuadrantBounds = determineSafeQuadrantBounds(flowers);
                if (safeQuadrantBounds == null) {
                    log.warn("Unable to determine safe quadrant bounds.");
                } else {
                    setCurrentSafeQuadrantBounds(safeQuadrantBounds);
                }
            }
        }

        LocalPoint playerLocalPoint = client.getLocalPlayer().getLocalLocation();
        if (playerLocalPoint == null) {
            log.warn("Player location is null.");
            return;
        }

        //Check if the player is in the quadrant during Flower + Totem Phase
        if (isFlowerPhase(nightmare) && safeQuadrantBounds != null &&
                !safeQuadrantBounds.contains(WorldPoint.fromLocal(client, playerLocalPoint).getX(),
                        WorldPoint.fromLocal(client, playerLocalPoint).getY())) {
            log.info("Not inside safe quadrant during Flower and Totem Phase. Moving to quadrant.");
            moveToSafeTileInQuadrant(safeQuadrantBounds);
            return; //Exit to allow movement to the quadrant first
        }

        //If within interaction range and already in the quadrant
        LocalPoint totemLocalPoint = LocalPoint.fromWorld(client, totemWorldPoint);
        int distanceToTotem = playerLocalPoint.distanceTo(totemLocalPoint);

        if (!isBadTileLocal(playerLocalPoint)
                && distanceToTotem <= 1152
                && timeout == 0 // Ensure timeout is 0
                && (safeQuadrantBounds == null || safeQuadrantBounds.contains(
                WorldPoint.fromLocal(client, playerLocalPoint).getX(),
                WorldPoint.fromLocal(client, playerLocalPoint).getY()))) {
            log.info("Already on a safe tile near the totem and within the quadrant if required, and timeout is 0. Charging it.");
            NPCInteraction.interact(closestTotem, "Charge");
            return;
        }


        //Prioritize dodging bad tiles if needed
        if (isBadTileLocal(playerLocalPoint)) {
            if (isFlowerPhase(getNightmare())) {
                log.info("Player is on a bad tile during Flower Phase. Moving to a safe tile within the quadrant.");
                if (safeQuadrantBounds != null) {
                    moveToSafeTileInQuadrant(safeQuadrantBounds); //Prioritize moving to a safe tile in the quadrant
                } else {
                    log.warn("Safe quadrant bounds are not defined. Cannot move to a safe tile in the quadrant.");
                }
            } else {
                log.info("Player is on a bad tile during Totem Phase. Dodging...");
                moveToSafeTileNearTotem(totemWorldPoint, playerLocalPoint); //main handling logic during totem phase bud
            }
            return;
        }

        //Move closer to a safe tile near the totem if not in range
        if (distanceToTotem > 1152 && !isParasitePhase(nightmare) && !isHuskPhase(nightmare) &&
                getGoodFlowers().isEmpty() && !holeSpawned()) {
            log.info("Too far from totem ({} local distance). Moving to a safe tile near it.", distanceToTotem);
            moveToSafeTileNearTotem(totemWorldPoint, playerLocalPoint);
        }
    }

    private void moveToSafeTileNearTotem(WorldPoint totemWorldPoint, LocalPoint currentLocation) {
        Set<LocalPoint> safeTiles = getSafeTiles();
        if (safeTiles.isEmpty()) {
            log.warn("No safe tiles available near the totem.");
            return;
        }

        LocalPoint totemLocalPoint = LocalPoint.fromWorld(client, totemWorldPoint);

        //Check if the player is already on a safe tile near the totem
        if (!isBadTileLocal(currentLocation) && safeTiles.contains(currentLocation) &&
                currentLocation.distanceTo(totemLocalPoint) <= 1152) {
            //log.info("Already on a safe tile near the totem. No movement needed.");
            return;
        }

        //Find the closest safe tile near the totem
        LocalPoint closestSafeTile = safeTiles.stream()
                .filter(tile -> tile.distanceTo(totemLocalPoint) <= 1152) //Only consider tiles within 1152 local distance of the totem
                .filter(tile -> !isBadTileLocal(tile)) //Avoid bad tiles
                .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocation)))
                .orElse(null);

        if (closestSafeTile != null && !isFlowerPhase(getNightmare()) && !isHuskPhase(getNightmare())) {
            log.info("Moving to closest safe tile near totem: {}", closestSafeTile);
            PolarAPI.move(WorldPoint.fromLocal(client, closestSafeTile));
        } else {
            log.warn("No safe tiles found within range of the totem.");
        }
    }

    private LocalPoint getClosestNonBorderSafeTile(LocalPoint currentLocal) {
        //Get all safe tiles
        Set<LocalPoint> allSafeTiles = getSafeTiles();

        //convert the border safe tiles (from WorldPoint to LocalPoint)
        List<LocalPoint> borderSafeTiles = getSafeBorderTilesAroundPhosani().stream()
                .map(tile -> LocalPoint.fromWorld(client, tile))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        //Filter out the tiles that are in the border set.
        List<LocalPoint> nonBorderSafeTiles = allSafeTiles.stream()
                .filter(tile -> !borderSafeTiles.contains(tile))
                .collect(Collectors.toList());

        //Return the non-border safe tile closest to the current location.
        return nonBorderSafeTiles.stream()
                .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocal)))
                .orElse(null);
    }



    private void handleChargePhase(NPC nightmare) {
        if (!isChargePhase(nightmare)) {
            log.info("Not in Charge Phase. Skipping...");
            chargeTiles.clear();
            isInChargePhase = false;
            hasMovedToSafeTile = false;
            return;
        }

        log.info("Entering Charge Phase. Moving to a safe tile.");
        calculateChargeDangerZone(nightmare); // Update danger zone

        //Move to a safe tile only once
        if (!chargeTiles.isEmpty()) {
            avoidBadTiles(); //Move to a safe spot outside the danger zone
            hasMovedToSafeTile = true;
            isInChargePhase = true;
        }
        if (isFlowerPhase(nightmare)) {
            log.info("Charge Phase ended due to Flower Phase. Resetting.");
            isInChargePhase = false;
            hasMovedToSafeTile = false;
            chargeTiles.clear();
            return;
        }

        //No further movement during charge phase if already moved to a safe tile
        if (hasMovedToSafeTile) {
            log.info("Player already moved to a safe tile. No further movement.");
            return;
        }
    }




    public NPC getClosestSleepwalker() {
        NPCQuery sleepwalkers = NPCs.search().alive().withName("Sleepwalker");
        return sleepwalkers.nearestToPlayer().stream().findFirst().orElse(null);
    }
    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event) {
        GraphicsObject graphicsObject = event.getGraphicsObject();
        if (graphicsObject == null) {
            return;
        }

        int id = graphicsObject.getId();
        WorldPoint location = WorldPoint.fromLocal(client, graphicsObject.getLocation());

        switch (id) {
            case GRAPHICS_DODGE_PORTAL:
              //log.info("Detected bad tile graphics object (Dodge Portal) at {}", location);
                dodgePortals.put(location, 4); //Portal stays dangerous for 4 ticks
                break;
        }
    }
    public Set<WorldPoint> getGoodFlowers() {
        Set<WorldPoint> flowers = TileObjects.search()
                .withIds(GOOD_FLOWER, GOOD_FLOWER_2)
                .result()
                .stream()
                .map(TileObject::getWorldLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (isFlowerPhase(getNightmare())) {
          //log.info("Found {} good flowers: {}", flowers.size(), flowers);
        }
        return flowers;
    }

    /** PATHING SHIT **/
    public Set<WorldPoint> getBadTiles() {
        if (!badTilesDirty) {
            return cachedBadTiles; //Return the cached set if it's still valid
        }
        Set<WorldPoint> badTiles = new HashSet<>();
        //Add bad flower tiles
        badTiles.addAll(TileObjects.search()
                .withIds(BAD_FLOWER, BAD_FLOWER_2)
                .result()
                .stream()
                .map(TileObject::getWorldLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        NPCs.search()
                .withIds(9435, 9436, 9438, 9439, 9441, 9442, 9444, 9445)
                .result()
                .forEach(npc -> {
                    WorldArea npcArea = npc.getWorldArea();
                    if (npcArea != null) {
                        // Add the NPC's current WorldArea tiles
                        badTiles.addAll(npcArea.toWorldPointList());
                    }
                });


        //Walls fuck off
        badTiles.addAll(TileObjects.search()
                .withId(20980)
                .result()
                .stream()
                .flatMap(tileObject -> {
                    WorldPoint worldLocation = tileObject.getWorldLocation();
                    if (worldLocation == null) {
                        return Stream.empty();
                    }

                    //Generate a 3x3 area around the tile (including the tile itself)
                    Set<WorldPoint> surroundingTiles = new HashSet<>();
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            surroundingTiles.add(new WorldPoint(
                                    worldLocation.getX() + dx,
                                    worldLocation.getY() + dy,
                                    worldLocation.getPlane()
                            ));
                        }
                    }
                    return surroundingTiles.stream();
                })
                .collect(Collectors.toSet()));

        badTiles.addAll(TileObjects.search()
                .withId(27122)
                .result()
                .stream()
                .flatMap(tileObject -> {
                    WorldPoint worldLocation = tileObject.getWorldLocation();
                    if (worldLocation == null) {
                        return Stream.empty();
                    }

                    //Generate a 3x3 area around the tile (including the tile itself)
                    Set<WorldPoint> surroundingTiles = new HashSet<>();
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            surroundingTiles.add(new WorldPoint(
                                    worldLocation.getX() + dx,
                                    worldLocation.getY() + dy,
                                    worldLocation.getPlane()
                            ));
                        }
                    }
                    return surroundingTiles.stream();
                })
                .collect(Collectors.toSet()));


        //Add dodge portal graphics object tiles (faggot portals)
        client.getGraphicsObjects().forEach(graphics -> {
            if (graphics.getId() == GRAPHICS_DODGE_PORTAL) {
                WorldPoint location = WorldPoint.fromLocal(client, graphics.getLocation());
                //Only add if the portal is still “active” according to our timer.
                if (location != null && dodgePortals.getOrDefault(location, 0) > 0) {
                    badTiles.add(location);
                }
            }
        });


        //Add mushroom tiles and their 3x3 area
        TileObjects.search()
                .withIds(MUSHROOM, PRE_SHROOM)
                .result()
                .stream()
                .map(TileObject::getWorldLocation)
                .filter(Objects::nonNull)
                .forEach(mushroomLocation -> {
                    badTiles.add(mushroomLocation);

                    // Add 3x3 area around the mushroom
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            badTiles.add(new WorldPoint(
                                    mushroomLocation.getX() + dx,
                                    mushroomLocation.getY() + dy,
                                    mushroomLocation.getPlane()
                            ));
                        }
                    }
                });

        //Add tiles occupied by the Nightmare
        NPC nightmare = getNightmare();
        if (nightmare != null) {
            WorldArea nightmareArea = nightmare.getWorldArea();
            if (nightmareArea != null) {
                badTiles.addAll(nightmareArea.toWorldPointList());
            }
        }
        badTiles.addAll(chargeTiles);
        cachedBadTiles = badTiles; //Update the cached set
        badTilesDirty = false; //Mark the cache as valid
        return badTiles;
    }
    public void updateSleepwalkerStates() {
        activeSleepwalkers.entrySet().removeIf(entry -> entry.getValue().isDead());
    }


    private Rectangle determineSafeQuadrantBounds(Set<WorldPoint> flowers) {
        if (flowers.isEmpty()) {
            log.warn("No flower locations provided. Cannot determine safe bounds.");
            return null;
        }

        //Initialize min and max bounds based on flower positions
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (WorldPoint flower : flowers) {
            minX = Math.min(minX, flower.getX());
            maxX = Math.max(maxX, flower.getX());
            minY = Math.min(minY, flower.getY());
            maxY = Math.max(maxY, flower.getY());
        }

        //Extend bounds to create a 10x10 "L" shape
        int extendedMinX = Math.max(minX - 10, minX);
        int extendedMaxX = Math.min(maxX + 10, maxX);
        int extendedMinY = Math.max(minY - 10, minY);
        int extendedMaxY = Math.min(maxY + 10, maxY);

        //Ensure the extended bounds form an "L" shape
        int finalMinX = Math.min(minX, extendedMinX);
        int finalMaxX = Math.max(maxX, extendedMaxX);
        int finalMinY = Math.min(minY, extendedMinY);
        int finalMaxY = Math.max(maxY, extendedMaxY);

        log.debug("Safe quadrant bounds calculated: MinX={}, MaxX={}, MinY={}, MaxY={}",
                finalMinX, finalMaxX, finalMinY, finalMaxY);

        return new Rectangle(finalMinX, finalMinY, finalMaxX - finalMinX + 1, finalMaxY - finalMinY + 1);
    }

    public WorldPoint fromRegion(int regionId, int regionX, int regionY, int plane) {
        int worldX = (regionId >>> 8 << 6) + regionX; //Calculate the world X-coordinate
        int worldY = ((regionId & 255) << 6) + regionY; //Calculate the world Y-coordinate
        return new WorldPoint(worldX, worldY, plane);
    }
    private NPC getClosestTotem() {
        return client.getNpcs().stream()
                .filter(this::isActiveTotem) //Ensure it's an active totem
                .min(Comparator.comparingInt(npc -> {
                    WorldPoint npcWorldPoint = npc.getWorldLocation();
                    if (npcWorldPoint == null) return Integer.MAX_VALUE;
                    return npcWorldPoint.distanceTo(client.getLocalPlayer().getWorldLocation());
                }))
                .orElse(null);
    }

    private boolean isActiveTotem(NPC npc) {
        int npcId = npc.getId();
        return npcId == SOUTHEAST_TOTEM || npcId == SOUTHWEST_TOTEM ||
                npcId == NORTHWEST_TOTEM || npcId == NORTHEAST_TOTEM;
    }

    private boolean isSleepwalker(NPC npc) {
        String npcName = npc.getName();
        return Objects.equals(npcName, "Sleepwalker");
    }
    public WorldPoint getSafeTile() {
        return safeTile;
    }

    public void setSafeTile(WorldPoint safeTile) {
        this.safeTile = safeTile;
    }

    public void setCurrentSafeQuadrant(@Nullable Set<LocalPoint> quadrant) {
        this.currentSafeQuadrant = quadrant; //Update the safe quadrant
    }
    private boolean isBadChargeTile(WorldPoint tile) {
        return chargeTiles.contains(tile);
    }
    private boolean isSafeBorderTile(WorldPoint currentLocation, List<WorldPoint> safeBorderTiles) {
        return safeBorderTiles.contains(currentLocation);
    }

    private boolean isAtLeastOneTileAwayFromDanger(WorldPoint tile) {
        return chargeTiles.stream().allMatch(dangerTile -> tile.distanceTo(dangerTile) > 2);
    }

    private void avoidBadTiles() {
        WorldPoint currentLocation = client.getLocalPlayer().getWorldLocation();

        if (currentLocation == null) {
            log.warn("Player location is null. Cannot avoid bad tiles.");
            return;
        }

        LocalPoint currentLocal = LocalPoint.fromWorld(client, currentLocation);

        //Prioritize safe movement during Charge Phase
        if (isChargePhase(getNightmare())) {
            timeout = PolarAPI.random(3,5);
            handleChargePhaseAvoidance(currentLocal);
            return;
        }

        //Handle if already on a safe tile
        if (isOnSafeTile(currentLocal)) {
            handleSafeTileBehavior(currentLocation);
            return;
        }

        //Check for safe border tiles during general avoidance
        if (!isTotemPhase(getNightmare())) {
            handleBorderTileAvoidance(currentLocal);
        } else {
            handleTotemPhaseAvoidance(currentLocal, currentLocation);
        }
    }

    private void handleChargePhaseAvoidance(LocalPoint currentLocal) {
        log.info("Player is in Charge Phase. Prioritizing safe movement.");

        LocalPoint closestSafeTile = getClosestSafeChargeTile(currentLocal);

        if (closestSafeTile != null) {
            log.info("Moving to safe tile outside charge danger zone: {}", closestSafeTile);
            PolarAPI.move(WorldPoint.fromLocal(client, closestSafeTile));
        } else {
            log.warn("No safe tiles available outside the danger zone.");
        }
    }

    private boolean isOnSafeTile(LocalPoint currentLocal) {
        return currentLocal != null && !isBadTileLocal(currentLocal);
    }

    private void handleSafeTileBehavior(WorldPoint currentLocation) {
      //  log.info("Already on a safe tile.");

        if (!isParasites() && !isHuskPhase(getNightmare()) && !isFinalPhase(getNightmare())) {
            handleAttackLogic(currentLocation);
        }
    }

    private void handleAttackLogic(WorldPoint currentLocation) {
        if (isTotemPhase(getNightmare())) {
            attackNearestTotem(currentLocation);
        } else if (isFlowerPhase(getNightmare()) || isDarkHolePhase(getNightmare()) || isMushroomPhase(getNightmare())) {
            List<WorldPoint> safeBorderTiles = getSafeBorderTilesAroundPhosani();
            if (isSafeBorderTile(currentLocation, safeBorderTiles)) {
          //      log.info("On a safe border tile. Attacking Nightmare.");
                attackNightmare();
                log.info("Attacked Nightmare - handleAttackLogic.");
            } else {
                log.info("Not on a safe border tile. No attack allowed.");
            }
        }
    }

    private void handleBorderTileAvoidance(LocalPoint currentLocal) {
        //Define the threshold in local units (e.g., 3 tiles = 3*128 = 384 units) DO NOT CHANGE THIS DUMBASS
        final int threshold = 384;

        //Get the border safe tiles (convert from WorldPoint to LocalPoint)
        List<LocalPoint> safeBorderTiles = getSafeBorderTilesAroundPhosani().stream()
                .map(tile -> LocalPoint.fromWorld(client, tile))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        //Check if any border tile is within the threshold distance.
        Optional<LocalPoint> closeBorderTile = safeBorderTiles.stream()
                .filter(tile -> tile.distanceTo(currentLocal) <= threshold)
                .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocal)));

        if (closeBorderTile.isPresent()) {
            log.info("Border tile {} is within {} units. Moving there.", closeBorderTile.get(), threshold);
            PolarAPI.move(WorldPoint.fromLocal(client, closeBorderTile.get()));
            return;
        }

        //If no border tile is close enough, then try to get the safest overall non-border safe tile.
        LocalPoint safestNonBorderTile = getClosestNonBorderSafeTile(currentLocal);
        if (safestNonBorderTile != null) {
            log.info("No border tile within threshold. Defaulting to safest non-border tile: {}", safestNonBorderTile);
            PolarAPI.move(WorldPoint.fromLocal(client, safestNonBorderTile));
        } else {
            log.warn("No suitable safe tile found. Falling back to general avoidance.");
            avoidBadTiles();
        }
    }



    private void handleTotemPhaseAvoidance(LocalPoint currentLocal, WorldPoint currentLocation) {
        NPC closestTotem = getClosestTotem();
        if (closestTotem == null) {
            log.warn("No active totems found.");
            return;
        }

        handleTotemAttackAndMovement(currentLocal, currentLocation, closestTotem);
    }

    private void handleTotemAttackAndMovement(LocalPoint currentLocal, WorldPoint currentLocation, NPC closestTotem) {
        WorldPoint totemWorldPoint = closestTotem.getWorldLocation();
        if (totemWorldPoint == null) {
            log.warn("Could not determine WorldPoint for the closest totem.");
            return;
        }

        if (isOnSafeTileNearTotem(currentLocal, totemWorldPoint)) {
         //  log.info("Already on a safe tile near the totem. Attacking totem.");
            NPCInteraction.interact(closestTotem, "Charge");
        } else if (shouldMoveToSafeTileNearTotem()) {
            log.info("Handling Totem Phase. Moving closer to totem: {}", totemWorldPoint);
            moveToSafeTileNearTotem(totemWorldPoint, currentLocal);
        }
    }

    private boolean isOnSafeTileNearTotem(LocalPoint currentLocal, WorldPoint totemWorldPoint) {
        return !isBadTileLocal(currentLocal) && currentLocal.distanceTo(LocalPoint.fromWorld(client, totemWorldPoint)) <= 1152;
    }

    private boolean shouldMoveToSafeTileNearTotem() {
        return !isFlowerPhase(getNightmare()) && !isMushroomPhase(getNightmare()) && !isChargePhase(getNightmare()) && !isInChargePhase;
    }

    private LocalPoint getClosestSafeChargeTile(LocalPoint currentLocal) {
        Set<LocalPoint> safeTiles = getSafeTiles();
        return safeTiles.stream()
                .filter(tile -> isSafeChargeTile(tile))
                .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocal)))
                .orElse(null);
    }

    private boolean isSafeChargeTile(LocalPoint tile) {
        WorldPoint worldPoint = WorldPoint.fromLocal(client, tile);
        return !isBadChargeTile(worldPoint) && isAtLeastOneTileAwayFromDanger(worldPoint);
    }

    private LocalPoint getClosestSafeTileFromBorders(LocalPoint currentLocal, List<WorldPoint> safeBorderTiles) {
        return safeBorderTiles.stream()
                .map(tile -> LocalPoint.fromWorld(client, tile))
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocal)))
                .orElse(null);
    }


    private WorldPoint calculateWorldAreaCenter(WorldArea area) {
        int centerX = area.getX() + area.getWidth() / 2;
        int centerY = area.getY() + area.getHeight() / 2;
        return new WorldPoint(centerX, centerY, area.getPlane());
    }


    @Subscribe
    public void onNpcSpawned(NpcSpawned event) {
        NPC npc = event.getNpc();
        if (Objects.equals(npc.getName(), "Sleepwalker")) {
            waitingForWalkers = false;
            activeSleepwalkers.put(npc.getIndex(), npc);
            sleepwalkerCount++;
            log.info("Sleepwalker spawned: {}. Total: {}", npc.getWorldLocation(), sleepwalkerCount);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event) {
        NPC npc = event.getNpc();
        if (Objects.equals(npc.getName(), "Phosani's Nightmare")) {
            fightingPhosani = false; // End the fight
            currentFightTime = Duration.between(fightStartTime, Instant.now());
            log.info("Nightmare killed. Current fight time: {}", (currentFightTime));

            //Update best fight time if applicable
            if (bestFightTime == null || currentFightTime.compareTo(bestFightTime) < 0) {
                bestFightTime = currentFightTime;
                log.info("New best fight time: {}", (bestFightTime));
            }

            fightStartTime = null;
            currentFightTime = Duration.ZERO;
        }
        if (Objects.equals(npc.getName(), "Sleepwalker")) {
            activeSleepwalkers.remove(npc.getIndex());
            sleepwalkerCount--;

            //Increment total Sleepwalkers killed
            totalSleepwalkersKilled++;
            log.info("Sleepwalker despawned: {}. Remaining: {}. Total killed: {}", npc.getIndex(), activeSleepwalkers.size(), totalSleepwalkersKilled);

            updateSleepwalkerStage();
        }
    }


    //Update the current stage based on total Sleepwalkers killed
    private void updateSleepwalkerStage() {
        if (totalSleepwalkersKilled == 10) {
            Stage = 4; // Stage 4: 10 kills
        } else if (totalSleepwalkersKilled == 6) {
            Stage = 3; // Stage 3: 6 kills
        } else if (totalSleepwalkersKilled == 3) {
            Stage = 2; // Stage 2: 3 kills
        } else if (totalSleepwalkersKilled == 1) {
            Stage = 1; // Stage 1: 1 kills
        } else {
            Stage = 0; // No kills yet
        }

        log.info("Sleepwalker stage updated: Stage {}", Stage);
    }

    // Reset the tracker when Nightmare is dead
    public void resetSleepwalkerTracker() {
        totalSleepwalkersKilled = 0;
        sleepwalkersAttacked = 0;
        Stage = 0;
        activeSleepwalkers.clear();
        sleepwalkerCount = 0;
        log.info("Sleepwalker tracker reset. All counts cleared.");
    }

    private LocalPoint findClosestSafeTile(LocalPoint start, Set<LocalPoint> safeTiles) {
        return safeTiles.stream()
                .min(Comparator.comparingInt(tile -> tile.distanceTo(start)))
                .orElse(null);
    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        //Return early if waiting for Walkers or in Husk Phase
        if (waitingForWalkers && !isHuskPhase(getNightmare())) {
            return;
        }

        Projectile projectile = event.getProjectile();
        if (projectile.getId() == 1768) {
            log.info("Projectile detected. Moving to a safespot.");

            //Get a random corner tile using the Corner enum
            LocalPoint randomCornerTile = getRandomCornerTile();
            if (randomCornerTile == null) {
                log.warn("No valid corner safespots found.");
                return;
            }

            //Add slight randomness (dx, dy) to the corner tile position
            int tileSize = 128; // Size of a tile in local coordinates
            int dx = ThreadLocalRandom.current().nextInt(-1, 2); // Random offset between -1 and 1
            int dy = ThreadLocalRandom.current().nextInt(-1, 2);
            LocalPoint targetTile = new LocalPoint(
                    randomCornerTile.getX() + dx * tileSize,
                    randomCornerTile.getY() + dy * tileSize
            );

            if (!isBadTileLocal(targetTile) && getGoodFlowers().isEmpty() && !holeSpawned() && !isHuskPhase(getNightmare())) {
                log.info("Moving to randomized corner safespot: {}", WorldPoint.fromLocal(client, targetTile));
                PolarAPI.move(WorldPoint.fromLocal(client, targetTile));
                waitingForWalkers = true; // Prevent repeated calls
            } else {
                log.info("Conditions not met for moving to safespot.");
            }
        }
    }



    private LocalPoint getRandomCornerTile() {
        List<LocalPoint> validCorners = Arrays.stream(Corner.values())
                .map(Corner::getPoint)
                .filter(corner -> !isBadTileLocal(corner)) // Filter out bad tiles
                .collect(Collectors.toList());

        if (validCorners.isEmpty()) {
            log.warn("No valid corner tiles available.");
            return null;
        }

        return validCorners.get(ThreadLocalRandom.current().nextInt(validCorners.size()));
    }



    private void gotoCorner(Corner corner) {
        LocalPoint cornerTile = corner.getPoint();
        if (!isBadTileLocal(cornerTile)) {
            PolarAPI.move(WorldPoint.fromLocal(client, cornerTile));
            log.info("Moved to corner: {}", corner);
        } else {
            log.warn("Corner {} is a bad tile. Not moving.", corner);
        }
    }



    private List<LocalPoint> findPath(LocalPoint start, LocalPoint goal) {
        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingInt(PathNode::getScore));
        Map<LocalPoint, LocalPoint> cameFrom = new HashMap<>();
        Map<LocalPoint, Integer> gScore = new HashMap<>();
        Map<LocalPoint, Integer> fScore = new HashMap<>(); //Store F-scores for rendering
        Set<LocalPoint> visited = new HashSet<>();

        gScore.put(start, 0);
        fScore.put(start, heuristic(start, goal));
        openSet.add(new PathNode(start, fScore.get(start)));

        while (!openSet.isEmpty()) {
            PathNode currentNode = openSet.poll();
            LocalPoint current = currentNode.getPoint();

            if (current.equals(goal)) {
                setPathScores(fScore); // Store scores for overlay
                return reconstructPath(cameFrom, current);
            }

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            for (LocalPoint neighbor : getNeighborsLocal(current)) {
                if (isBadTileLocal(neighbor) || visited.contains(neighbor)) {
                    continue;
                }

                int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + current.distanceTo(neighbor);

                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    int fScoreValue = tentativeGScore + heuristic(neighbor, goal);
                    fScore.put(neighbor, fScoreValue);
                    openSet.add(new PathNode(neighbor, fScoreValue));
                }
            }
        }

        return null;
    }


    private List<LocalPoint> reconstructPath(Map<LocalPoint, LocalPoint> cameFrom, LocalPoint current) {
        List<LocalPoint> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }
    private void followPath(List<LocalPoint> path) {
        for (LocalPoint step : path) {
            WorldPoint worldPoint = WorldPoint.fromLocal(client, step);
            if (worldPoint != null) {
                log.info("Moving to: {}", worldPoint);
                PolarAPI.move(worldPoint);
                return; // Move one step at a time
            }
        }
    }

    public Duration getCurrentFightTime() {
        return currentFightTime;
    }

    public void setCurrentFightTime(Duration currentFightTime) {
        this.currentFightTime = currentFightTime;
    }

    public Duration getBestFightTime() {
        return bestFightTime;
    }

    public void setBestFightTime(Duration bestFightTime) {
        this.bestFightTime = bestFightTime;
    }

    private static class PathNode {
        private final LocalPoint point;
        private final int score;

        public PathNode(LocalPoint point, int score) {
            this.point = point;
            this.score = score;
        }

        public LocalPoint getPoint() {
            return point;
        }

        public int getScore() {
            return score;
        }
    }
    private int heuristic(LocalPoint a, LocalPoint b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    private Set<LocalPoint> getSafeTiles() {
        Set<LocalPoint> safeTiles = new HashSet<>();

        //Iterate over all accessible tiles in the game area
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                LocalPoint localPoint = LocalPoint.fromScene(x, y);
                if (localPoint != null && !isBadTileLocal(localPoint)) {
                    safeTiles.add(localPoint);
                }
            }
        }

      //log.info("Found {} safe tiles.", safeTiles.size());
        return safeTiles;
    }

    private void finalPhase() {
        WorldPoint currentLocation = client.getLocalPlayer().getWorldLocation();
        if (currentLocation == null) {
            log.warn("Player location is null. Cannot process Final Phase.");
            return;
        }

        // --- GEAR & SPECIAL ATTACK LOGIC ---
        int specEnabled = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED);
        int specPercent = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        int threshold = config.specPercent() * 10; // e.g. if config.specPercent() is 50, threshold is 500 (50%)

        if (specPercent >= threshold) {
            if (specEnabled != 1) {
                PolarAPI.swapGear(config.specGear());
                PolarAPI.enableSpec();
                log.info("Spec attack enabled ({}% >= {}%).", specPercent, threshold);
            }
        } else {
            PolarAPI.swapGear(config.mainGear());
            log.info("Spec percent too low ({}% < {}%), using main gear.", specPercent, threshold);
        }

        // --- ALWAYS ATTACK THE NIGHTMARE ---
        NPC nightmare = getNightmare();
        if (nightmare != null && !nightmare.isDead()) {
            PolarAPI.attackNPC(nightmare);
        } else {
            log.warn("Nightmare not found or dead.");
            return;
        }

        // --- SAFE BORDER TILE MOVEMENT ---
        LocalPoint currentLocal = LocalPoint.fromWorld(client, currentLocation);
        if (currentLocal == null) {
            log.warn("Unable to convert current location to LocalPoint.");
            return;
        }

        List<WorldPoint> safeBorderTiles = getSafeBorderTilesAroundPhosani();
        List<LocalPoint> validBorderTiles = safeBorderTiles.stream()
                .map(tile -> LocalPoint.fromWorld(client, tile))
                .filter(Objects::nonNull)
                .filter(tile -> !isBadTileLocal(tile))
                .collect(Collectors.toList());

        boolean onSafeBorder = false;
        //Check if we are already "on" (or very near) one of the valid safe border tiles.
        for (LocalPoint tile : validBorderTiles) {
            if (currentLocal.distanceTo(tile) < 10) { //Adjust threshold as needed ( dont CHANGE FAG)
                onSafeBorder = true;
                break;
            }
        }

        if (!onSafeBorder) {
            //Not on a safe border tile: find the closest valid safe border tile.
            LocalPoint closestBorderTile = validBorderTiles.stream()
                    .min(Comparator.comparingInt(tile -> tile.distanceTo(currentLocal)))
                    .orElse(null);

            if (closestBorderTile != null) {
                log.info("Moving to safe border tile: {}", closestBorderTile);
                PolarAPI.move(WorldPoint.fromLocal(client, closestBorderTile));
            } else {
                log.warn("No valid safe border tiles found. Falling back to general bad tile avoidance.");
                avoidBadTiles();
            }
        } else {
            //Already on a safe border tile, ensure we continue attacking.
            if (nightmare != null && !nightmare.isDead()) {
                PolarAPI.attackNPC(nightmare);
            }
        }
    }





    public List<WorldPoint> getSafeBorderTilesAroundPhosani() {
        NPC nightmare = getNightmare();
        if (nightmare == null) {
            return Collections.emptyList();
        }

        WorldArea nightmareArea = nightmare.getWorldArea();
        if (nightmareArea == null) {
            return Collections.emptyList();
        }

        //Expand the area by 1 tile in all directions
        int x = nightmareArea.getX();
        int y = nightmareArea.getY();
        int width = nightmareArea.getWidth();
        int height = nightmareArea.getHeight();
        int plane = nightmareArea.getPlane();

        WorldArea expandedArea = new WorldArea(x - 1, y - 1, width + 2, height + 2, plane);

        //Get the tiles surrounding the Nightmare
        List<WorldPoint> borderTiles = expandedArea.toWorldPointList();

        //Exclude the inner tiles (original 5x5 Nightmare area)
        List<WorldPoint> innerTiles = nightmareArea.toWorldPointList();
        borderTiles.removeAll(innerTiles);

        //Exclude diagonal corner tiles (O tiles)
        List<WorldPoint> excludedCorners = new ArrayList<>();
        excludedCorners.add(new WorldPoint(x - 1, y - 1, plane)); // Bottom-left corner
        excludedCorners.add(new WorldPoint(x + width, y - 1, plane)); // Bottom-right corner
        excludedCorners.add(new WorldPoint(x - 1, y + height, plane)); // Top-left corner
        excludedCorners.add(new WorldPoint(x + width, y + height, plane)); // Top-right corner

        return borderTiles.stream()
                .filter(tile -> !excludedCorners.contains(tile)) //Exclude O tiles
                .filter(tile -> !isBadTileLocal(LocalPoint.fromWorld(client, tile))) //Exclude bad tiles
                .collect(Collectors.toList());
    }

    private void attackNearestTotem(WorldPoint currentLocation) {
        NPC totem = getClosestTotem();
        if (totem == null) {
            log.info("No totems found nearby to attack.");
            return;
        }

        WorldArea totemArea = totem.getWorldArea();
        if (totemArea == null) {
            log.warn("Could not retrieve WorldArea for the totem. Skipping attack.");
            return;
        }

        //Calculate the center of the totem's WorldArea
        WorldPoint totemCenter = calculateWorldAreaCenter(totemArea);

        //Get the safe quadrant bounds if applicable
        Rectangle safeQuadrantBounds = getCurrentSafeQuadrantBounds();

        //Get the closest safe tile to the totem center
        LocalPoint closestSafeTile = findClosestSafeTileNearTotem(totemCenter, safeQuadrantBounds);

        if (closestSafeTile == null) {
            log.warn("No safe tiles found near the totem. Staying in place.");
            return;
        }

        //Check if the current location is already on the closest safe tile
        LocalPoint currentLocal = LocalPoint.fromWorld(client, currentLocation);
        if (currentLocal != null && currentLocal.equals(closestSafeTile)) {
            log.info("Already on the closest safe tile. Attacking the totem.");
            NPCInteraction.interact(totem, "Charge");
            return;
        }
    }
    private void calculateChargeDangerZone(NPC nightmare) {
        if (nightmare == null) {
            log.warn("Nightmare NPC is null. Cannot calculate danger zone.");
            return;
        }

        WorldPoint nightmarePosition = nightmare.getWorldLocation();
        if (nightmarePosition == null) {
            log.warn("Nightmare's world location is null. Cannot calculate danger zone.");
            return;
        }

        chargeTiles.clear(); // Clear previous danger tiles

        int centerX = nightmarePosition.getX();
        int centerY = nightmarePosition.getY();
        int plane = nightmarePosition.getPlane();

        int radius = 4; //3 tiles in each direction for a 6x6 strip
        int extension = 20; //Extend 12 tiles in each cardinal direction

        //Generate tiles for all 4 strips
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - extension; y <= centerY + extension; y++) {
                chargeTiles.add(new WorldPoint(x, y, plane));
            }
        }
        for (int y = centerY - radius; y <= centerY + radius; y++) {
            for (int x = centerX - extension; x <= centerX + extension; x++) {
                chargeTiles.add(new WorldPoint(x, y, plane));
            }
        }

        log.info("Charge tiles calculated: {}", chargeTiles.size());
    }


    private LocalPoint findClosestSafeTileNearTotem(WorldPoint totemCenter, Rectangle safeQuadrantBounds) {
        Set<LocalPoint> safeTiles = getSafeTiles();
        if (safeTiles.isEmpty()) {
            log.warn("No safe tiles available for pathfinding.");
            return null;
        }

        return safeTiles.stream()
                .filter(tile -> {
                    if (safeQuadrantBounds != null) {
                        WorldPoint worldTile = WorldPoint.fromLocal(client, tile);
                        return worldTile != null &&
                                safeQuadrantBounds.contains(worldTile.getX(), worldTile.getY());
                    }
                    return true; //Include all tiles if not restricted to the quadrant
                })
                .min(Comparator.comparingInt(tile -> tile.distanceTo(LocalPoint.fromWorld(client, totemCenter))))
                .orElse(null);
    }

    public int tickDelay() {
        return ThreadLocalRandom.current().nextInt(1 / 2, 1);
    }

    public boolean isPregnant() {
        return client.getVarbitValue(Varbits.PARASITE) == 1;
    }

    private boolean isBadTileLocal(LocalPoint tile) {
        WorldPoint worldPoint = WorldPoint.fromLocal(client, tile);
        return worldPoint != null && getBadTiles().contains(worldPoint);
    }

    private void doSpec() {
            WidgetPackets.queueWidgetActionPacket(1, 10485795, -1, -1);

            if (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1 && client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) >= config.specPercent() * 10) {
                attackNightmare();
                log.info("Attacking Nightmare - doSpec.");
            }

    }

    private List<LocalPoint> getNeighborsLocal(LocalPoint point) {
        return Arrays.asList(
                point.dx(128),          // 1 tile east
                point.dx(-128),         // 1 tile west
                point.dy(128),          // 1 tile north
                point.dy(-128),         // 1 tile south
                point.dx(128).dy(128),  // 1 tile northeast
                point.dx(128).dy(-128), // 1 tile southeast
                point.dx(-128).dy(128), // 1 tile northwest
                point.dx(-128).dy(-128) // 1 tile southwest
        ).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    public Prayer getCurrentPrayer() {
        for (Prayer prayer : Prayer.values()) {
            if (client.isPrayerActive(prayer)) {
                return prayer;
            }
        }
        return null;
    }

    @Subscribe
    public void onChatMessage(ChatMessage message) {
        if (message.getMessage().contains("The Nightmare has cursed you, shuffling your prayers!")) {
            backwardsPrayers = true;
            log.info("We're cursed. Backwards prayers.");
        }
        if ((message.getMessage().contains("The Nightmare will awaken in 5 seconds!") ||
                message.getMessage().contains("The Nightmare has awoken!"))) {
            if (!fightingPhosani) {
                log.info("Starting to fight Phosani.");
                fightingPhosani = true;
                fightStartTime = Instant.now(); // Start the timer
                totalSleepwalkersKilled = 0;
                sleepwalkerCount = 0;
                activeSleepwalkers.clear();
            }
        }
        if (message.getMessage().toLowerCase().contains("all four totems are fully charged.")) {
            waitingForWalkers = true;
        }
        if (message.getMessage().toLowerCase().contains("you feel the effects of the nightmare's curse wear off.")) {
            backwardsPrayers = false;
            log.info("We are not cursed. Regular prayers");
        }
        if (message.getMessage().contains("The parasite bursts out of you, weakened by your potion!")) {
            pregnant = false;
            log.info("Fuck off parasite. I got an abortion.");
        }
        if (message.getMessage().contains("The Nightmare's infection has worn off.")) {
            if (client.getVarpValue(173) == 0) {
                PolarAPI.enableRun();
            }
            log.info("The Nightmare's infection has worn off.");
        }
    }
    public boolean isRunEnabled()
    {
        return client.getVarpValue(173) == 1;
    }

    public boolean holeSpawned() {
        for (GraphicsObject graphics : client.getGraphicsObjects()) {
            if (graphics.getId() == GRAPHICS_DODGE_PORTAL) {
                return true;
            }
        }
        return false;
    }
}
