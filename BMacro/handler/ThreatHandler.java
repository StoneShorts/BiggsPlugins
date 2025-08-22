package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.AntiSpecIDS;
import net.runelite.client.live.inDevelopment.biggs.BMacro.overlay.ExpDamageOverlay;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreatHandler {
    private static final Logger log = LoggerFactory.getLogger(ThreatHandler.class);

    private final ClientThread clientThread;
    private final PolarMacroConfig config;
    private final ComboHandler comboHandler;
    private final ExpDamageOverlay expOverlay;
    private final Client client;

    private int lastTargetAnim = -1;

    @Inject
    public ThreatHandler(ClientThread clientThread, PolarMacroConfig config, ComboHandler comboHandler, ExpDamageOverlay expOverlay, Client client) {
        this.clientThread = clientThread;
        this.config = config;
        this.comboHandler = comboHandler;
        this.expOverlay = expOverlay;
        this.client = client;
    }

    public void onGameTick(GameTick event) {
        if (PolarAPI.notLoggedIn()) return;

        Player target = comboHandler.getLastTargetedPlayer();
        if (target == null || target.isDead()) return;

        int hp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        AntiSpecIDS spec = AntiSpecIDS.getById(lastTargetAnim);

        if (spec != null) {
            log.debug("Detected potential spec: {}", spec.name());
        }

        if (config.tripleEatOnThreat()
                && spec != null
                && hp <= config.minimumTripleEat()) {

            clientThread.invoke(() -> {
                log.info("🛡️ Triple Eat triggered due to {} spec at {} HP", spec.name(), hp);

                PolarAPI.eatItemContains(config.mainFood());
                PolarAPI.drinkPotionFromLowestDose("Saradomin brew");
                PolarAPI.eatItemContains(config.tickFood());

                expOverlay.flashTripleEat();
            });
        }
    }





    public void onAnimationChanged(AnimationChanged event) {
        if (!(event.getActor() instanceof Player)) return;

        Player p = (Player) event.getActor();
        Player target = comboHandler.getLastTargetedPlayer();

        if (target != null && p.equals(target)) {
            lastTargetAnim = p.getAnimation();
            log.debug("🎞️ Target animation updated: {}", lastTargetAnim);
        }
    }
}
