package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.TileObjectInteraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles injecting “Quick Bank → [Preset]” menu entries upon right‐clicking a
 * bank chest or bank booth. Each enabled preset appears (no limit).
 *
 * Uses TileObjectInteraction.interact(...) to open the nearest “Bank chest” or “Bank booth.”
 * Once bank is open, deposits on successive ticks, then hands off to BankHandler.
 */
@Singleton
public class MenuHandler {
    private static final Logger log = LoggerFactory.getLogger(MenuHandler.class);

    private final Client client;
    private final ClientThread clientThread;
    private final BankLoadoutHandler bankLoadoutHandler;
    private final BankHandler bankHandler;

    @Inject
    public MenuHandler(Client client,
                       ClientThread clientThread,
                       BankLoadoutHandler bankLoadoutHandler,
                       BankHandler bankHandler) {
        this.client = client;
        this.clientThread = clientThread;
        this.bankLoadoutHandler = bankLoadoutHandler;
        this.bankHandler = bankHandler;
    }

    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        String option    = event.getOption();
        String targetRaw = event.getTarget();
        String target    = targetRaw.toLowerCase();

        // If user right‐clicked “Use” on a bank chest, or “Bank” on a bank booth:
        if (("Use".equals(option) && target.contains("bank chest"))
                || ("Bank".equals(option) && target.contains("bank booth"))) {
            // Fetch all presets, but only those marked “enabled”
            List<BankLoadoutHandler.BankPreset> all = bankLoadoutHandler.getAllPresets();
            List<BankLoadoutHandler.BankPreset> enabledPresets = new ArrayList<>();
            for (BankLoadoutHandler.BankPreset p : all) {
                if (p != null && p.isEnabled()) {
                    enabledPresets.add(p);
                }
            }

            final String[] COLORS = {
                    "#FF0000", "#FF7F00", "#FFFF00", "#00FF00", "#0000FF",
                    "#4B0082", "#9400D3", "#00CED1", "#FF1493", "#FFD700",
                    "#A52A2A", "#7FFF00", "#DC143C", "#00008B", "#008B8B",
                    "#B8860B", "#006400", "#8B008B", "#556B2F", "#FF8C00",
                    "#9932CC", "#8B0000", "#E9967A", "#8FBC8F", "#483D8B",
                    "#2F4F4F", "#00BFFF", "#696969", "#FF69B4", "#CD5C5C"
            };

            List<MenuEntry> bankEntries = new ArrayList<>();
            for (int i = 0; i < enabledPresets.size(); i++) {
                BankLoadoutHandler.BankPreset preset = enabledPresets.get(i);
                String name = preset.getName();
                String hex  = COLORS[i % COLORS.length].substring(1);
                String colored = "<col=" + hex + ">" + name + "</col>";

                MenuEntry e = client.createMenuEntry(0)
                        .setOption(colored)
                        .setTarget(event.getTarget())
                        .setParam0(event.getActionParam0())
                        .setParam1(event.getActionParam1())
                        .setIdentifier(event.getIdentifier())
                        .setType(MenuAction.RUNELITE)
                        .onClick(menuEntry -> {
                            clientThread.invoke(() -> runBankPreset(preset));
                        });
                bankEntries.add(e);
            }

            if (!bankEntries.isEmpty()) {
                MenuEntry[] existing = client.getMenuEntries();
                MenuEntry[] combined = new MenuEntry[bankEntries.size() + existing.length];
                for (int i = 0; i < bankEntries.size(); i++) {
                    combined[i] = bankEntries.get(i);
                }
                System.arraycopy(existing, 0, combined, bankEntries.size(), existing.length);
                client.setMenuEntries(combined);
            }
        }
    }

    /**
     * Kick off the bank‐preset process:
     *   1) Immediately open nearest bank.
     *   2) On the next game tick, begin waiting for the bank to open.
     */
    private void runBankPreset(BankLoadoutHandler.BankPreset preset) {
        log.info("Running quick‐bank preset: {}", preset.getName());

        // 1) Open nearest bank on the client thread
        clientThread.invoke(this::openNearestBank);

        // 2) On the next tick, begin checking for bank open / deposit
        clientThread.invokeLater(() -> waitForBankOpenTick(preset));
    }

    /**
     * Called on the tick immediately after we first tried to open the bank.
     * Now we only poll each tick until PolarAPI.isBankOpen() is true.
     * Once open, we deposit everything and move on.
     */

    private void waitForBankOpenTick(BankLoadoutHandler.BankPreset preset) {
        // If the bank still isn’t open, just schedule ourselves for one more tick
        if (!PolarAPI.isBankOpen()) {
            clientThread.invokeLater(() -> waitForBankOpenTick(preset));
            return;
        }

        // Once the bank is confirmed open, deposit all inventory at once.
        clientThread.invoke(() -> {
            log.info("-> Bank is open. Depositing all inventory and equipment.");
            PolarAPI.depositAll();
        });

        // Delay 5–8 ticks before depositing worn items and handing off to BankHandler
        int delayTicks = ThreadLocalRandom.current().nextInt(5, 9);
        scheduleDepositWornAndHandoff(preset, delayTicks);
    }


    /**
     * Counts down `ticksRemaining` each game tick. Once zero, deposit worn items and call BankHandler.
     */
    private void scheduleDepositWornAndHandoff(BankLoadoutHandler.BankPreset preset, int ticksRemaining) {
        if (ticksRemaining <= 0) {
            clientThread.invoke(() ->
            {
                log.info("-> Depositing worn items and handing off to BankHandler.");
                PolarAPI.depositWornItems();
                bankHandler.withdrawAndEquipGear(preset);
            });
        }
        else {
            clientThread.invokeLater(() -> scheduleDepositWornAndHandoff(preset, ticksRemaining - 1));
        }
    }

    /**
     * Attempts to open the nearest Bank chest first; if that fails, attempts Bank booth.
     */
    private void openNearestBank() {
        boolean chestResult = TileObjectInteraction.interact("Bank chest", "Use");
        log.info("Attempted Bank chest interact: {}", chestResult);
        if (chestResult) {
            return;
        }

        boolean boothResult = TileObjectInteraction.interact("Bank booth", "Bank");
        log.info("Attempted Bank booth interact: {}", boothResult);
        if (!boothResult) {
            log.info("No Bank chest or booth interaction succeeded.");
        }
    }
}
