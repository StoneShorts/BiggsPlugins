package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import com.google.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.inDevelopment.biggs.BMacro.handler.ComboHandler;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.Inventory;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtils.API.PrayerUtil;
import net.runelite.client.live.inDevelopment.biggs.BMacro.overlay.ExpDamageOverlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrayerHandler {
    private static final Logger log = LoggerFactory.getLogger(PrayerHandler.class);

    private final Client client;
    private final ClientThread clientThread;
    private final PolarMacroConfig config;
    private final ComboHandler comboHandler;
    private final ExpDamageOverlay expOverlay;

    private static final WorldArea BOUNTY_HUNTER_BANK = new WorldArea(3416, 4056, 16, 16, 0);
    private static final WorldArea BOUNTY_BANK = new WorldArea(3413, 4054, 21, 20, 0);

    @Getter
    private boolean prayerModeEnabled = false;
    private Player lastAutoTarget = null;

    @Inject
    public PrayerHandler(
            Client client,
            ClientThread clientThread,
            PolarMacroConfig config,
            ComboHandler comboHandler,
            ExpDamageOverlay expOverlay
    ) {
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
        this.comboHandler = comboHandler;
        this.expOverlay = expOverlay;
    }

    public void togglePrayerMode() {
        prayerModeEnabled = !prayerModeEnabled;
        log.info("Prayer Mode toggled: {}", prayerModeEnabled);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (PolarAPI.notLoggedIn())
            return;

        Player local = client.getLocalPlayer();
        if (local == null)
            return;

        boolean inBank = BOUNTY_BANK.contains(local.getWorldLocation());

        // 🧼 Turn off all offensive prayers only once when in bank
        if (inBank && prayerModeEnabled) {
            for (Prayer prayer : Prayer.values()) {
                if (prayer != Prayer.PROTECT_ITEM && PrayerUtil.isPrayerActive(prayer)) {
                    PrayerUtil.togglePrayer(prayer);
                }
            }
            prayerModeEnabled = false;
            log.info("Prayer mode auto-disabled inside BH bank.");
            return; // Exit early
        }

        // 🧼 Make sure offensive prayers are disabled when not in prayer mode
        if (!prayerModeEnabled) {
            for (Prayer prayer : Prayer.values()) {
                if (prayer != Prayer.PROTECT_ITEM && PrayerUtil.isPrayerActive(prayer)) {
                    PrayerUtil.togglePrayer(prayer);
                }
            }
            return;
        }

        //Auto prayer-safety restoration
        if (config.prayerSafety() && client.getBoostedSkillLevel(Skill.PRAYER) <= config.prayerSafetyLevel()) {
            boolean drank = false;
            if (Inventory.search().nameContains("super restore").first().isPresent()) {
                PolarAPI.drinkPotionFromLowestDose("super restore");
                drank = true;
            } else if (Inventory.search().nameContains("Prayer potion").first().isPresent()) {
                PolarAPI.drinkPotionFromLowestDose("Prayer potion");
                drank = true;
            }
            if (drank) {
                expOverlay.flashPrayerSafety();
                log.info("🔋 Prayer safety triggered ({} prayer)", client.getBoostedSkillLevel(Skill.PRAYER));
            }
        }

        // 💍 Equip recoil if needed
        if (config.equipRecoil() && !inBank) {
            if (!PolarAPI.isPlayerWearingContains("Ring of recoil") &&
                    Inventory.search().nameContains("Ring of recoil").first().isPresent()) {
                PolarAPI.swapGearmenuAction("Ring of recoil");
                log.info("💍 Equipping Ring of recoil");
            }
        }

        // 🎯 Auto-target
        if (local.getInteracting() instanceof Player) {
            Player p = (Player) local.getInteracting();
            if (lastAutoTarget == null || !p.equals(lastAutoTarget)) {
                lastAutoTarget = p;
                comboHandler.setLastTargetedPlayer(p);
                log.info("🔍 Auto-target set to {}", p.getName());
            }
        }

        // ✝️ Auto-enable correct prayers based on gear
        if (!inBank && prayerModeEnabled) {
            if (config.swapPrayer() && PolarAPI.isPlayerWearingContains(config.equipRanged())) {
                switch (config.rangedPrayer()) {
                    case HAWK_EYE: enablePrayerIfInactive(Prayer.HAWK_EYE); break;
                    case EAGLE_EYE: enablePrayerIfInactive(Prayer.EAGLE_EYE); break;
                    case RIGOUR: enablePrayerIfInactive(Prayer.RIGOUR); break;
                }
            }

            if (config.meleePrayer() != net.runelite.client.live.inDevelopment.biggs.BMacro.config.MeleePrayer.NONE &&
                    PolarAPI.isPlayerWearingContains(config.equipMelee())) {
                switch (config.meleePrayer()) {
                    case CHIVALRY: enablePrayerIfInactive(Prayer.CHIVALRY); break;
                    case PIETY: enablePrayerIfInactive(Prayer.PIETY); break;
                    case ULTIMATE_STRENGTH: enablePrayerIfInactive(Prayer.ULTIMATE_STRENGTH); break;
                    case ULTSTR_REFLEX:
                        enablePrayerIfInactive(Prayer.ULTIMATE_STRENGTH);
                        enablePrayerIfInactive(Prayer.INCREDIBLE_REFLEXES);
                        break;
                    case ULTSTR_REFLEX_STEEL:
                        enablePrayerIfInactive(Prayer.ULTIMATE_STRENGTH);
                        enablePrayerIfInactive(Prayer.INCREDIBLE_REFLEXES);
                        enablePrayerIfInactive(Prayer.STEEL_SKIN);
                        break;
                }
            }
        }
    }


    public void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("PolarMacro") && e.getKey().equals("togglePrayerHotkey")) {
            togglePrayerMode();
        }
    }

    public static void enablePrayerIfInactive(Prayer prayer) {
        if (!PrayerUtil.isPrayerActive(prayer)) {
            PrayerUtil.togglePrayer(prayer);
        }
    }
}
