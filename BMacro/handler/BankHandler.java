package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.BankSpd;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.Inventory;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.BankInventoryInteraction;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.TileObjectInteraction;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.inDevelopment.biggs.BMacro.handler.BankLoadoutHandler.BankPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class BankHandler {
    private static final Logger log = LoggerFactory.getLogger(BankHandler.class);

    private final Client client;
    private final ClientThread clientThread;
    private final PolarMacroConfig config;

    private enum Mode {
        IDLE,
        WITHDRAW_ALL_GEAR,
        EQUIP_ALL_GEAR,
        WITHDRAW_INV,
        WAIT_NEXT,
        OPEN_BANK,
        PREPARE
    }

    private Mode mode = Mode.IDLE;
    private BankPreset currentPreset;
    private int gearIndex = 0, invIndex = 0, waitTicks = 0;

    private List<String> gearList;
    private List<String> invList;

    @Inject
    public BankHandler(Client client, ClientThread clientThread, PolarMacroConfig config) {
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
    }

    public void withdrawAndEquipGear(BankPreset preset) {
        if (mode != Mode.IDLE) {
            log.warn("Already processing a preset. Ignoring '{}'.", preset.getName());
            return;
        }
        this.currentPreset = preset;
        this.gearList = preset.getGearItems();
        this.invList = preset.getInventoryItems();
        this.gearIndex = 0;
        this.invIndex = 0;
        this.mode = Mode.PREPARE;
        log.info("Starting full gear withdrawal for preset '{}'.", preset.getName());
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (mode == Mode.IDLE) return;

        if (mode != Mode.PREPARE && (mode == Mode.WITHDRAW_ALL_GEAR || mode == Mode.EQUIP_ALL_GEAR || mode == Mode.WITHDRAW_INV)) {
            if (!PolarAPI.isBankOpen() && !PolarAPI.isAnimating() && !PolarAPI.isBankPinOpen()) {
                log.info("Waiting — skipping bank action until PREPARE step is done.");
                return;
            }
        }

        if (waitTicks-- > 0) return;

        int batchSize = getBatchSize();
        int delay = getNextDelay();

        switch (mode) {
            case WITHDRAW_ALL_GEAR: {
                int end = Math.min(gearIndex + batchSize, gearList.size());
                for (; gearIndex < end; gearIndex++) {
                    String item = gearList.get(gearIndex);
                    String rawName = item.replaceAll("\\s*\\[\\d+\\]$", "").trim();
                    int count = extractCount(item);
                    log.info("Withdrawing {}x '{}'", count, rawName);
                    PolarAPI.withdrawContainsNoCaseX(rawName, count);
                }
                if (gearIndex >= gearList.size()) {
                    gearIndex = 0;
                    mode = Mode.EQUIP_ALL_GEAR;
                    log.info("All gear withdrawn. Moving to equip phase.");
                } else {
                    waitTicks = delay;
                }
                break;
            }

            case EQUIP_ALL_GEAR: {
                int end = Math.min(gearIndex + batchSize, gearList.size());
                for (; gearIndex < end; gearIndex++) {
                    String item = gearList.get(gearIndex);
                    String raw = item.replaceAll("\\s*\\[\\d+\\]$", "").trim();
                    int count = extractCount(item);
                    for (int i = 1; i <= count; i++) {
                        log.info("Equipping '{}' attempt {}", raw, i);
                        BankInventoryInteraction.equipFromInventoryContains(raw);
                    }
                }
                if (gearIndex >= gearList.size()) {
                    gearIndex = 0;
                    mode = Mode.WITHDRAW_INV;
                    log.info("Equipping complete. Moving to inventory phase.");
                } else {
                    waitTicks = delay;
                }
                break;
            }

            case WITHDRAW_INV: {
                int end = Math.min(invIndex + batchSize, invList.size());
                for (; invIndex < end; invIndex++) {
                    String entry = invList.get(invIndex);
                    String name = entry.replaceAll("\\s*\\[\\d+\\]$", "").trim();
                    int count = extractCount(entry);
                    log.info("Withdrawing {}x '{}' (invIndex={})", count, name, invIndex);
                    PolarAPI.withdrawContainsNoCaseX(name, count);
                }
                if (invIndex >= invList.size()) {
                    clientThread.invoke(() -> PolarAPI.sendGameMessage("<col=f40404>Bank preset</col> <col=ffffff>" + currentPreset.getName() + "</col> <col=f40404>complete."));
                    log.info("Inventory phase complete.");
                    PolarAPI.closeAmountInterface();
                    client.runScript(101, -1);
                    mode = Mode.IDLE;
                } else {
                    waitTicks = delay;
                }
                break;
            }
            case OPEN_BANK: {
                if (PolarAPI.isBankOpen() || PolarAPI.isBankPinOpen()) {
                    mode = Mode.WITHDRAW_ALL_GEAR;
                    log.info("Bank opened. Proceeding to withdraw gear.");
                } else if (!PolarAPI.isAnimating()) {
                    log.info("Opening bank...");
                    if (!TileObjectInteraction.interact("Bank chest", "Use")) {
                        TileObjectInteraction.interact("Bank booth", "Bank");
                    }
                }
                waitTicks = PolarAPI.random(1,10); // add slight delay so it doesn’t spam
                PolarAPI.sendGameMessage("Waiting for bank to open. Delaying for " + waitTicks + " ticks.");
                break;
            }
            case PREPARE: {
                int realHp = client.getRealSkillLevel(Skill.HITPOINTS);
                int boostedHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
                int realPrayer = client.getRealSkillLevel(Skill.PRAYER);
                int boostedPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
                int spec = PolarAPI.getSpecAmnt();

                boolean needPool = boostedHp < realHp || boostedPrayer < realPrayer || spec < 100;

                if (needPool) {
                    boolean usedPool = TileObjectInteraction.interact("Pool of Refreshment", "Drink");
                    if (usedPool) {
                        log.info("Using Pool of Refreshment before banking.");
                        waitTicks = PolarAPI.random(2, 5);
                        return;
                    }
                }

                if (Inventory.search().nameContains("anglerfish").first().isPresent()) {
                    log.info("Eating Anglerfish for max HP boost.");
                    PolarAPI.eatItemContains("anglerfish");
                    waitTicks = PolarAPI.random(3, 5);
                    return;
                } else {
                    log.info("No Anglerfish found. Skipping pre-heal.");
                    waitTicks = PolarAPI.random(1, 3);
                }

                log.info("Prep complete. Proceeding to open bank.");
                mode = Mode.OPEN_BANK; // ⬅️ KEY CHANGE HERE
                return;
            }
        }
    }

    private int extractCount(String entry) {
        Matcher m = Pattern.compile("\\[(\\d+)\\]$").matcher(entry);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException ignore) {}
        }
        return 1;
    }

    private int getBatchSize() {
        switch (config.bankSpeed()) {
            case SPED: return 1;
            case SLOW: return 1;
            case NORMAL: return 1;
            case MEDIUM: return 1;
            case FAST: return 2 + (int)(Math.random() * 2); // 2-3
            case FASTER: return 3 + (int)(Math.random() * 3); // 3-5
            case FAST_AS_FUCK: return 6 + (int)(Math.random() * 3); // 6-8
            default: return 1;
        }
    }

    private int getNextDelay() {
        switch (config.bankSpeed()) {
            case SPED: return 3 + (int)(Math.random() * 2); // 3-4
            case SLOW: return 1 + (int)(Math.random() * 2); // 1-2
            case NORMAL: return Math.random() < 0.3 ? 1 : 0; // 30% chance to delay 1
            case MEDIUM: return Math.random() < 0.15 ? 1 : 0; // 15% chance
            case FAST: return 0;
            case FASTER: return 0;
            case FAST_AS_FUCK: return 0;
            default: return 0;
        }
    }
}
