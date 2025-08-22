package net.runelite.client.live.inDevelopment.biggs.BMacro;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.live.PolarPlugins.premium.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.PolarPlugins.premium.biggs.BMacro.discord.DiscordWebhookSender;
import net.runelite.client.live.PolarPlugins.premium.biggs.BMacro.handler.*;
import net.runelite.client.live.PolarPlugins.premium.biggs.BMacro.overlay.ExpDamageOverlay;
import net.runelite.client.live.PolarPlugins.premium.biggs.BMacro.overlay.PolarMacroOverlay;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.magic.Lunar;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtility.Util.PlayerUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@PluginDescriptor(
        name = "<html><font color='#E65A50'>..Biggs Feet</font></html>",
        description = "Sorry bud",
        tags = {"polar", "macro"}
)
public class PolarMacroPlugin extends Plugin {
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private KeyManager keyManager;
    @Inject private ConfigManager configManager;
    @Inject private OverlayManager overlayManager;
    @Inject private MouseManager mouseManager;

    @Inject private net.runelite.client.live.PolarPlugins.premium.biggs.BMacro.PolarMacroConfig config;

    @Inject private MagicHandler magicHandler;
    @Inject private ComboHandler comboHandler;
    @Inject private ThreatHandler threatHandler;
    @Inject private XPHandler xpHandler;
    @Inject private PrayerHandler prayerHandler;
    @Inject private MenuHandler menuHandler;
    @Inject private ConfigHandler configHandler;
    @Inject private BankLoadoutHandler bankLoadoutHandler;
    @Inject private BankHandler bankHandler;

    @Inject private Hooks hooks;

    private boolean lootSent = false;
    private long lastLootCheck = 0;



    private static final String KILL_WEBHOOK_URL = "https://discord.com/api/webhooks/1381043205331488768/RzxGmS-_JiaZBB8OcA0fSSuB1A7V-tmc7QNERKBscsYRKXF8HuR98DUXU9ZuEFUfNwW8";
    private static final String DEATH_WEBHOOK_URL = "https://discord.com/api/webhooks/1381043458386563223/Z_gINKIied2f4pbzDauB2yUe1Gge3bkTlepekrD40eEiIdiwTq52bqlaF3yUWTb27Stc";
    private static final String LOOT_WEBHOOK_URL = "https://discord.com/api/webhooks/1381051523290628277/CSdK0_vNPSMAKrlzcSJFRshGK9mJ40Gv-kCKBSHnULEC_VCVkr9goeKE_bk52gPojr-N";



    @Inject private PolarMacroOverlay overlay;
    @Inject private ExpDamageOverlay expOverlay;
    private int cachedAnimId = -1;

    private final HotkeyListener castVengHotkey = new HotkeyListener(() -> config.castVeng()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> PolarAPI.cast(Lunar.VENGEANCE));
        }
    };

    private final HotkeyListener whackHotkey = new HotkeyListener(() -> config.toggleWhackHotkey()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> {
                String whackGear = config.equipWhack();
                Player target = comboHandler.getLastTargetedPlayer();
                if (whackGear != null && !whackGear.isEmpty() && target != null && !target.isDead()) {
                    if (!PolarAPI.isPlayerWearingContains(whackGear)) {
                        PolarAPI.swapGearmenuAction(whackGear);
                    }
                    PlayerUtil.attack(target);
                }
            });
        }
    };

    private final HotkeyListener macroHotkey = new HotkeyListener(() -> config.macroHotkey()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> comboHandler.executeCombo());
        }
    };

    private final HotkeyListener tripleEatHotkey = new HotkeyListener(() -> config.tripleEat()) {
        @Override
        public void hotkeyPressed() {
            clientThread.invoke(() -> {
                PolarAPI.eatItemContains(config.mainFood());
                PolarAPI.drinkPotionFromLowestDose("Saradomin brew");
                PolarAPI.eatItemContains(config.tickFood());
            });
        }
    };

    private final HotkeyListener togglePrayerHotkey = new HotkeyListener(() -> config.togglePrayerHotkey()) {
        @Override
        public void hotkeyPressed() {
            prayerHandler.togglePrayerMode();
        }
    };
    private boolean lootWidgetPreviouslyVisible = false;

    @Provides
    net.runelite.client.live.PolarPlugins.premium.biggs.BMacro.PolarMacroConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(PolarMacroConfig.class);
    }

    @Override
    protected void startUp() throws Exception {

        String correctPassword = "###04aeFukU9";
        String entered = config.passwordNigga();

        if (!correctPassword.equals(entered)) {
            log.info("Fuck off pussy");
            PolarAPI.sendGameMessage("Fuck off fake nigga. Faggot.");
            clientThread.invokeLater(() -> {
                PolarAPI.stopPlugin(this);
            });
            return;
        }

        expOverlay.startup();
        keyManager.registerKeyListener(castVengHotkey);
        keyManager.registerKeyListener(macroHotkey);
        keyManager.registerKeyListener(tripleEatHotkey);
        keyManager.registerKeyListener(togglePrayerHotkey);
        keyManager.registerKeyListener(whackHotkey);

        xpHandler.resetXpTracking();
        hooks.registerRenderableDrawListener(drawListener);
        overlayManager.add(overlay);
        overlayManager.add(expOverlay);

    }

    @Override
    protected void shutDown() throws Exception {
        expOverlay.shutdown();
        keyManager.unregisterKeyListener(castVengHotkey);
        keyManager.unregisterKeyListener(macroHotkey);
        keyManager.unregisterKeyListener(tripleEatHotkey);
        keyManager.unregisterKeyListener(togglePrayerHotkey);
        keyManager.unregisterKeyListener(whackHotkey);

        hooks.unregisterRenderableDrawListener(drawListener);
        xpHandler.resetXpTracking();
        overlayManager.remove(overlay);
        overlayManager.remove(expOverlay);
        lootSent = false;
        lastLootCheck = 0;
        lootWidgetPreviouslyVisible = false;
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // ① Normal “threat” / “XP” / “bank” / “combo” logic:
        threatHandler.onGameTick(event);
        xpHandler.onGameTick(event);
        comboHandler.onGameTick(event);
        magicHandler.tryCastGreaterCorruption();
        overlay.setLastHit(xpHandler.getLastDropDamage(), xpHandler.getLastDropSkill());
        // ② Move “auto‐target” logic here (always lock onto whoever you’re attacking):
        Player me = client.getLocalPlayer();
        if (config.resetCurrentTarget()) {
            comboHandler.setLastTargetedPlayer(null);
        } else if (me != null && me.getInteracting() instanceof Player) {
            Player p = (Player) me.getInteracting();
            if (!p.equals(comboHandler.getLastTargetedPlayer())) {
                comboHandler.setLastTargetedPlayer(p);
                log.info("Auto‐target set to {}", p.getName());
            }
        }


        Player local = client.getLocalPlayer();

        if (enterAmountWidgetVisible()) {
            client.runScript(101, -1);
        }

        if (config.getAnimID()) {
            if (local != null) {
                int anim = local.getAnimation();
                if (anim != cachedAnimId) {
                    cachedAnimId = anim;
                    overlay.setLastAnimationId(anim);

                    String weaponName = "None";
                    Item weapon = client.getItemContainer(InventoryID.EQUIPMENT) != null
                            ? client.getItemContainer(InventoryID.EQUIPMENT).getItem(EquipmentInventorySlot.WEAPON.getSlotIdx())
                            : null;

                    if (weapon != null) {
                        weaponName = client.getItemDefinition(weapon.getId()).getName();
                    }

                    PolarAPI.sendGameMessage("Your weapon: " + weaponName + ", Animation ID: " + anim);
                }
            }
        }


        // === Loot Chest Webhook Detection ===
        // Detect when the loot chest widget appears (only fire once per appearance)
        Widget lootContainer = client.getWidget(742, 3); // 742.3 = Loot Chest items container
        Widget valueWidget = client.getWidget(48627718); // Shows "Value in chest: 105,432gp"

        if (lootContainer != null && !lootContainer.isHidden()) {
            if (!lootWidgetPreviouslyVisible) {
                lootWidgetPreviouslyVisible = true;
                log.info("📦 Loot chest widget became visible – preparing webhook.");

                clientThread.invokeLater(() ->
                {
                    StringBuilder lootText = new StringBuilder();
                    boolean foundValidItem = false;

                    // Go through 0–28 loot slots
                    for (int i = 0; i <= 28; i++) {
                        Widget itemWidget = lootContainer.getChild(i);
                        if (itemWidget == null) continue;

                        int quantity = itemWidget.getItemQuantity();
                        String rawName = itemWidget.getName(); // e.g. "<col=ff9040>Blood rune</col>"

                        // Strip HTML tags (color) to get clean item name
                        String itemName = rawName != null ? rawName.replaceAll("<[^>]+>", "") : "Unknown";

                        if (quantity > 0 && itemName != null && !itemName.equalsIgnoreCase("null")) {
                            lootText.append(quantity).append(" x ").append(itemName).append("\n");
                            foundValidItem = true;
                        }
                    }

                    if (!foundValidItem) {
                        log.info("📭 Loot chest open, but no valid items detected.");
                        return;
                    }

                    // Value of the chest
                    String chestValue = valueWidget != null ? valueWidget.getText().replace("Value in chest: ", "").replace("<br>", "") : "Unknown";

                    log.info("📤 Sending loot webhook – value: {}", chestValue);

                    PolarAPI.getDrawManager().requestNextFrameListener(image -> {
                        BufferedImage screenshot = (BufferedImage) image;

                        Map<String, String> embedData = Map.of(
                                "title", "Biggs PK Loot Key 🗝️",
                                "description", config.webhookFunName() + " just opened a loot key!\nTotal value: " + chestValue + "\n\n" + lootText.toString().trim(),
                                "color", "16766720" // Gold
                        );

                        String lootImageUrl = "https://.../loot_big_image.png";
                        String lootThumbUrl = "https://.../loot_small_icon.png";

                        DiscordWebhookSender.sendEmbedWithScreenshot(
                                LOOT_WEBHOOK_URL,
                                embedData,
                                screenshot,
                                lootImageUrl,
                                lootThumbUrl
                        );
                    });
                });
            }
        } else {
            lootWidgetPreviouslyVisible = false; // reset state so next opening triggers again
        }





        prayerHandler.onGameTick(event);
        bankHandler.onGameTick(event);
        overlay.setTarget(comboHandler.getLastTargetedPlayer());
        overlay.setPrayerMode(prayerHandler.isPrayerModeEnabled());
        expOverlay.onGameTick(event);
    }



    private boolean enterAmountWidgetVisible() {
        return client.getWidget(10616874) != null && !client.getWidget(10616874).isHidden();
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged ev) {
        threatHandler.onAnimationChanged(ev);

        Actor actor = ev.getActor();
        if (!(actor instanceof Player)) return;

        Player target = (Player) actor;
        Player local = client.getLocalPlayer();

        // === 1. Target died (your last targeted player did animation 836)
        if (comboHandler.getLastTargetedPlayer() != null &&
                target.equals(comboHandler.getLastTargetedPlayer()) &&
                target.getAnimation() == 836)
        {
            log.info("Detected target death animation (836) — sending PK Kill embed");

            PolarAPI.getDrawManager().requestNextFrameListener(image -> {
                BufferedImage screenshot = (BufferedImage) image;
                Map<String, String> embedData = Map.of(
                        "title", "Biggs PK KILL 💀",
                        "description", config.webhookFunName() + " just killed " + target.getName() + "!",
                        "color", "5763719" // Green-blue tone
                );

                String killImageUrl = "https://.../kill_big_image.png";
                String killThumbUrl = "https://.../kill_small_icon.png";

                DiscordWebhookSender.sendEmbedWithScreenshot(
                        KILL_WEBHOOK_URL,
                        embedData,
                        screenshot,
                        killImageUrl,
                        killThumbUrl
                );
            });
        }

        // === 2. We died (our own animation is 836)
        if (local != null && actor.equals(local) && local.getAnimation() == 836)
        {
            Player lastTarget = comboHandler.getLastTargetedPlayer();

            String[] randomDeathMsgs = new String[] {
                    "just got dropped LOL",
                    "got sent to lumby 😭",
                    "choked it bad lmao",
                    "died like a bot 💀",
                    "got spec stacked into next week",
                    "wasn’t even close tbh",
                    "eaten alive by " + (lastTarget != null ? lastTarget.getName() : "someone") + " 😂",
                    "got fuckin packed by " + (lastTarget != null ? lastTarget.getName() : "someone 🤡") + " lmfao 🤡",
                    "got dropped by " + (lastTarget != null ? lastTarget.getName() : "someone") + " l0l 🤡",
                    "definitely a LGBTQ bot lmfao",
                    "0 ticked into the dirt by " + (lastTarget != null ? lastTarget.getName() : "someone") + " LOOOL 🤡",
                    "tele’d into the void",
                    "was absolutely destroyed by " + (lastTarget != null ? lastTarget.getName() : "someone") + " lmfaoo00 🤡",
                    "just said 'gf' to " + (lastTarget != null ? lastTarget.getName() : "someone"),
                    "sat down hard, no chair",
                    "got folded like a lawn chair by " + (lastTarget != null ? lastTarget.getName() : "someone jajajajaj kek"),
                    "was food for " + (lastTarget != null ? lastTarget.getName() : "a fatty"),
                    "couldn’t even tick eat lmfao",
                    "fed 3k risk to " + (lastTarget != null ? lastTarget.getName() : "a rat"),
                    "got autotyped mid‐death 💀"
            };

            String randomMsg = randomDeathMsgs[ThreadLocalRandom.current().nextInt(randomDeathMsgs.length)];

            log.info("Local player death detected (836) — sending PK Death embed");

            PolarAPI.getDrawManager().requestNextFrameListener(image -> {
                BufferedImage screenshot = (BufferedImage) image;
                Map<String, String> embedData = Map.of(
                        "title", "Biggs PK Deaths ☠️",
                        "description", config.webhookFunName() + " " + randomMsg + " by " + (lastTarget != null ? lastTarget.getName() : "someone") + "!",
                        "color", "16711680" // Red
                );

                String deathImageUrl = "https://.../death_big_image.png";
                String deathThumbUrl = "https://.../death_small_icon.png";

                DiscordWebhookSender.sendEmbedWithScreenshot(
                        DEATH_WEBHOOK_URL,
                        embedData,
                        screenshot,
                        deathImageUrl,
                        deathThumbUrl
                );
            });
        }
    }


    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        menuHandler.onMenuEntryAdded(event);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        configHandler.onConfigChanged(event);
    }


    @Subscribe
    public void onFakeXpDrop(FakeXpDrop event) {
        xpHandler.onFakeXpDrop(event);
    }

    private final Hooks.RenderableDrawListener drawListener = (renderable, drawingUI) -> {
        if (!(renderable instanceof Player)) {
            return true;
        }

        Player local = client.getLocalPlayer();
        Player other = (Player) renderable;

        if (local == null || other == null || other == local) {
            return true;
        }

        if (!config.hideOthers()) {
            return true;
        }

        // 👇 Always render if it's our current target
        Player target = comboHandler.getLastTargetedPlayer();
        if (target != null && other.equals(target)) {
            return true;
        }

        int localLevel = local.getCombatLevel();
        int otherLevel = other.getCombatLevel();
        int diff = Math.abs(localLevel - otherLevel);

        return diff <= config.hideOthersStrictness().getValue();
    };




}
