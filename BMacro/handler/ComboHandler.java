package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.ComboType;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.MainGearType;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.MeleePrayer;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.magic.Lunar;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtility.Util.PlayerUtil;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtils.API.PrayerUtil;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class ComboHandler
{
    public static final WorldArea BOUNTY_HUNTER = new WorldArea(3332, 3990, 178, 146, 0);

    private final Client client;
    private final ClientThread clientThread;
    private final PolarMacroConfig config;

    private Player lastTargetedPlayer = null;
    private boolean cachedHasBerserker = false;

    private ComboType lastComboType = null;
    private boolean queuedReturnToMainGear = false;
    private int mainGearDelayTicks = 0;

    private int initialSpecEnergy = -1;
    private int expectedSpecDrain = 0;
    private boolean waitingToWhack = false;
    private int whackDelayTicks = 0;

    @Inject
    public ComboHandler(Client client, ClientThread clientThread, PolarMacroConfig config)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
    }

    public void setLastTargetedPlayer(Player p)
    {
        this.lastTargetedPlayer = p;
    }

    public Player getLastTargetedPlayer()
    {
        return lastTargetedPlayer;
    }

    public void executeComboPrecheck(boolean hasBerserker)
    {
        this.cachedHasBerserker = hasBerserker;
        clientThread.invokeLater(this::executeCombo);
    }

    public void executeCombo()
    {
        ComboType combo = config.selectedCombo();
        lastComboType = combo;
        int currentSpec = PolarAPI.getSpecAmnt();
        log.info("Executing combo: {}", combo);

        if (currentSpec < combo.getSpecialAmountRequired()) {
            log.warn("❌ Not enough special energy ({} / {}) — skipping combo.", currentSpec, combo.getSpecialAmountRequired());
            return;
        }

        if (lastTargetedPlayer == null || lastTargetedPlayer.isDead()) {
            log.warn("❌ No valid lastTargetedPlayer; aborting combo.");
            return;
        }

        PlayerUtil.attack(lastTargetedPlayer);
        activateMeleePrayer();

        if (config.whackAfterSpecial()) {
            initialSpecEnergy = currentSpec;
            expectedSpecDrain = combo.getSpecialAmountRequired();
            waitingToWhack = true;
            whackDelayTicks = 0;
            log.info("🕒 Waiting to whack after {} spec energy used ({} → ...)", expectedSpecDrain, initialSpecEnergy);
        }

        switch (combo) {
            case VENG_VW_MAUL:
                PolarAPI.cast(Lunar.VENGEANCE);
                equip("Voidwaker, Avernic defender" + (cachedHasBerserker ? ", Berserker ring" : ""));
                spec();
                break;

            case VW_MAUL:
                if (!PolarAPI.isPlayerWearingContains("Voidwaker")) {
                    equip("Voidwaker, Avernic defender" + (cachedHasBerserker ? ", Berserker ring" : ""));
                }
                spec();
                break;

            case VENG_AGS_MAUL:
                PolarAPI.cast(Lunar.VENGEANCE);
                equip("Armadyl godsword" + (cachedHasBerserker ? ", Berserker ring" : ""));
                spec();
                break;

            case AGS_MAUL:
                if (!PolarAPI.isPlayerWearingContains("Armadyl godsword")) {
                    equip("Armadyl godsword" + (cachedHasBerserker ? ", Berserker ring" : ""));
                }
                spec();
                break;

            case DOUBLE_MAUL:
                equip("Granite maul" + (cachedHasBerserker ? ", Berserker ring" : ""));
                int specs = ThreadLocalRandom.current().nextInt(2, 4);
                expectedSpecDrain = specs * 25;
                for (int i = 0; i < specs; i++) {
                    spec();
                }
                break;

            case DDS_MAUL:
                if (!PolarAPI.isPlayerWearingContains("dragon dagger")) {
                    equip("dragon dagger" + (cachedHasBerserker ? ", Berserker ring" : ""));
                }
                spec();
                break;

            case MAUL_HALLY:
                equip("Granite maul" + (cachedHasBerserker ? ", Berserker ring" : ""));
                spec();
                expectedSpecDrain = 50;
                break;

            case DOUBLEMAUL_HALLY:
                equip("Granite maul" + (cachedHasBerserker ? ", Berserker ring" : ""));
                int doubleSpecs = 2;
                expectedSpecDrain = doubleSpecs * 25;
                for (int i = 0; i < doubleSpecs; i++) {
                    spec();
                }
                break;

            default:
                log.warn("❔ Unsupported combo type: {}", combo);
                waitingToWhack = false;
                break;
        }

        if (!config.whackAfterSpecial()) {
            queuedReturnToMainGear = true;
            mainGearDelayTicks = 0;
        }
    }

    private void spec()
    {
        PlayerUtil.attack(lastTargetedPlayer); // Force attack again to lock animation
        PolarAPI.enableSpec();
    }

    private void activateMeleePrayer()
    {
        MeleePrayer selected = config.meleePrayer();
        if (selected == null || selected == MeleePrayer.NONE) {
            return;
        }

        switch (selected) {
            case CHIVALRY:
                PrayerUtil.togglePrayer(Prayer.CHIVALRY);
                break;
            case PIETY:
                PrayerUtil.togglePrayer(Prayer.PIETY);
                break;
            case ULTIMATE_STRENGTH:
                PrayerUtil.togglePrayer(Prayer.ULTIMATE_STRENGTH);
                break;
            case ULTSTR_REFLEX:
                PrayerUtil.togglePrayer(Prayer.ULTIMATE_STRENGTH);
                PrayerUtil.togglePrayer(Prayer.INCREDIBLE_REFLEXES);
                break;
            case ULTSTR_REFLEX_STEEL:
                PrayerUtil.togglePrayer(Prayer.ULTIMATE_STRENGTH);
                PrayerUtil.togglePrayer(Prayer.INCREDIBLE_REFLEXES);
                PrayerUtil.togglePrayer(Prayer.STEEL_SKIN);
                break;
        }
    }

    private void equip(final String gear)
    {
        try {
            PolarAPI.swapGearmenuAction(gear);
            log.info("🎒 Equipped gear: {}", gear);
        }
        catch (Exception e) {
            log.error("Error equipping {}: {}", gear, e.getMessage());
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (waitingToWhack) {
            int currentSpec = PolarAPI.getSpecAmnt();
            int used = initialSpecEnergy - currentSpec;
            if (used >= expectedSpecDrain) {
                whackDelayTicks++;
                if (whackDelayTicks >= 1) {
                    waitingToWhack = false;
                    forceWhackNow();
                }
            }
        }

        if (queuedReturnToMainGear) {
            mainGearDelayTicks++;
            if (mainGearDelayTicks >= 2) {
                queuedReturnToMainGear = false;
                returnToMainGear();
            }
        }
    }

    public void forceWhackNow()
    {
        if (lastTargetedPlayer == null || lastTargetedPlayer.isDead()) {
            log.warn("❌ Target dead or invalid for forceWhackNow.");
            return;
        }

        log.info("⚔️ Forcing WHACK immediately after XP drop.");
        waitingToWhack = false;

        String whackGear = (lastComboType == ComboType.MAUL_HALLY || lastComboType == ComboType.DOUBLEMAUL_HALLY)
                ? "Noxious halberd"
                : config.equipWhack();

        if (whackGear == null || whackGear.trim().isEmpty()) {
            log.warn("❌ No Whack Weapon configured—cannot perform follow‐up.");
            return;
        }

        clientThread.invokeLater(() -> {
            activateMeleePrayer();
            equip(whackGear);
            PlayerUtil.attack(lastTargetedPlayer);
            log.info("🔨 WHACKED with {} + melee prayer.", whackGear);

            queuedReturnToMainGear = true;
            mainGearDelayTicks = 0;
        });
    }

    private void returnToMainGear()
    {
        MainGearType type = MainGearType.valueOf(config.mainGearType());
        if (type == MainGearType.NONE) {
            log.debug("MainGearType is NONE — skipping gear return.");
            return;
        }

        String gear = null;
        switch (type) {
            case RANGED:
                gear = config.equipRanged();
                break;
            case MELEE:
                gear = config.equipMelee();
                break;
            case MAGIC:
                gear = config.equipMage();
                break;
            default:
                log.warn("⚠️ Unsupported MainGearType: {}", type);
                return;
        }

        if (gear == null || gear.trim().isEmpty()) {
            log.warn("⚠️ Main gear for {} is empty in config, skipping return.", type);
            return;
        }

        final String finalGear = gear;
        clientThread.invokeLater(() -> PolarAPI.swapGearmenuAction(finalGear));
        log.info("🎯 Returned to main gear: {}", finalGear);
    }

    public void queueWhack()
    {
        this.waitingToWhack = true;
        this.whackDelayTicks = 0;
        log.info("📦 Whack queued after XP combo — waiting for spec to drain.");
    }
}
