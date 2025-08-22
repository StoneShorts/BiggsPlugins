package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import com.google.inject.Inject;
import net.runelite.api.*;

import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.inDevelopment.biggs.BMacro.overlay.PolarMacroOverlay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigHandler {
    private static final Logger log = LoggerFactory.getLogger(ConfigHandler.class);

    private final ClientThread clientThread;
    private final PolarMacroConfig config;
    private final PolarMacroOverlay overlay;
    private final Client client;
    private final ConfigManager configManager;
    private final BankLoadoutHandler bankLoadoutHandler;
    private final ComboHandler comboHandler;

    @Inject
    public ConfigHandler(
            ClientThread clientThread,
            PolarMacroConfig config,
            PolarMacroOverlay overlay,
            Client client,
            ConfigManager configManager,
            BankLoadoutHandler bankLoadoutHandler,
            ComboHandler comboHandler
    ) {
        this.clientThread = clientThread;
        this.config = config;
        this.overlay = overlay;
        this.client = client;
        this.configManager = configManager;
        this.bankLoadoutHandler = bankLoadoutHandler;
        this.comboHandler = comboHandler;
    }

    public void onConfigChanged(ConfigChanged event) {
        if (event.getKey().equals("copyGear")) {
            clientThread.invoke(() -> {
                StringBuilder sb = new StringBuilder();
                for (Item item : client.getItemContainer(InventoryID.EQUIPMENT).getItems()) {
                    if (item != null && item.getId() != -1 && item.getId() != 6512) {
                        ItemComposition def = client.getItemDefinition(item.getId());
                        sb.append(def.getName()).append(",");
                    }
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                StringSelection sel = new StringSelection(sb.toString());
                Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
                cb.setContents(sel, sel);
            });
        }

        if (event.getGroup().equals("PolarMacro") && event.getKey().equals("resetCurrentTarget")) {
            comboHandler.setLastTargetedPlayer(null);
            overlay.setTarget(null);
            log.info("❌ Current target reset via config toggle");
            configManager.setConfiguration("PolarMacro", "resetCurrentTarget", "false");
        }

        if (event.getKey().equals("editQuickBank")) {
            boolean enabled = config.editQuickBank();
            if (enabled) {
                bankLoadoutHandler.openEditDialog();
                log.info("Quick Bank editor opened.");
            } else {
                log.info("Quick Bank disabled.");
            }
        }
        if (event.getKey().equals("hideOthers") && !config.hideOthers()) {
            for (Player p : client.getPlayers()) {
                if (p == null) continue;
                try {
                    java.lang.reflect.Method setHiddenMethod = p.getClass().getDeclaredMethod("setHidden", boolean.class);
                    setHiddenMethod.setAccessible(true);
                    setHiddenMethod.invoke(p, false);
                } catch (Exception ignored) { } // No spam
            }
        }
    }
}
