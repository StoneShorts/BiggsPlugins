package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.FakeXpDrop;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.ExpType;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.StacksForCombo;
import net.runelite.client.live.inDevelopment.biggs.BMacro.overlay.ExpDamageOverlay;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.runelite.client.live.inDevelopment.biggs.BMacro.handler.ComboHandler.BOUNTY_HUNTER;

public class XPHandler {
    private static final Logger log = LoggerFactory.getLogger(XPHandler.class);
    private final LinkedList<Integer> recentXpHits = new LinkedList<>();

    private final Client client;
    private final ClientThread clientThread;
    private final ExpDamageOverlay expOverlay;
    private final PolarMacroConfig config;
    private final ComboHandler comboHandler;

    private int prevAttackXp = -1;
    private int prevStrengthXp = -1;
    private int prevDefenceXp = -1;
    private int prevRangedXp = -1;
    private boolean delayedCombo = false;

    @Getter
    private int lastDropDamage = 0;

    @Getter
    private Skill lastDropSkill = null;

    @Inject
    public XPHandler(Client client, ClientThread clientThread, ExpDamageOverlay expOverlay, PolarMacroConfig config, ComboHandler comboHandler) {
        this.client = client;
        this.clientThread = clientThread;
        this.expOverlay = expOverlay;
        this.config = config;
        this.comboHandler = comboHandler;
    }

    public void onGameTick(final GameTick event) {
        Player me = client.getLocalPlayer();

        if (delayedCombo) {
            delayedCombo = false;
            log.info("💥 Triggering delayed combo");
            boolean hasBerserker = Inventory.search().nameContains("Berserker ring").first().isPresent();
            comboHandler.executeComboPrecheck(hasBerserker);
        }

        if (me == null) {
            return;
        }

        if (client.getLocalPlayer() == null || BOUNTY_HUNTER.contains(client.getLocalPlayer().getWorldLocation()) || config.resetCurrentTarget()) {
            recentXpHits.clear(); // clear XP stack tracking in bank/safe zone
            return;
        }

        if (config.resetCurrentTarget()) {
            comboHandler.setLastTargetedPlayer(null);
            log.info("🧹 Target reset due to config setting.");
        }


        if (BOUNTY_HUNTER.contains(me.getWorldLocation())) {
            return;
        }

        int atkXp = client.getSkillExperience(Skill.ATTACK);
        int strXp = client.getSkillExperience(Skill.STRENGTH);
        int defXp = client.getSkillExperience(Skill.DEFENCE);
        int rngXp = client.getSkillExperience(Skill.RANGED);

        int deltaAtk = prevAttackXp == -1 ? 0 : atkXp - prevAttackXp;
        int deltaStr = prevStrengthXp == -1 ? 0 : strXp - prevStrengthXp;
        int deltaDef = prevDefenceXp == -1 ? 0 : defXp - prevDefenceXp;
        int deltaRng = prevRangedXp == -1 ? 0 : rngXp - prevRangedXp;

        prevAttackXp = atkXp;
        prevStrengthXp = strXp;
        prevDefenceXp = defXp;
        prevRangedXp = rngXp;

        ExpType type = config.expType();
        int xpThresh = config.specOverExp() * 4;

        Skill hitSkill = null;
        int hitXp = 0;

        if (type == ExpType.MELEE) {
            if (deltaAtk >= xpThresh && deltaAtk < 450) { hitSkill = Skill.ATTACK; hitXp = deltaAtk; }
            else if (deltaStr >= xpThresh && deltaStr < 450) { hitSkill = Skill.STRENGTH; hitXp = deltaStr; }
            else if (deltaDef >= xpThresh && deltaDef < 450) { hitSkill = Skill.DEFENCE; hitXp = deltaDef; }
        } else if (type == ExpType.RANGED) {
            if (deltaRng >= xpThresh && deltaRng < 450) { hitSkill = Skill.RANGED; hitXp = deltaRng; }
        }

        if (hitSkill != null) {
            if (hitSkill != null) {
                lastDropSkill = hitSkill;
                lastDropDamage = hitXp / 4;
                expOverlay.addPopup(String.valueOf(lastDropDamage), me.getWorldLocation());

                Player target = comboHandler.getLastTargetedPlayer();
                if (target != null && target.getHealthRatio() > 0 && target.getHealthScale() > 0) {
                    int hpPercent = (int)(((double)target.getHealthRatio() / target.getHealthScale()) * 100);
                    boolean hpCheck = config.useTargetHP()
                            ? hpPercent <= config.targetHPForCombo()
                            : hpPercent <= 90;

                    if (hpCheck) {
                        log.info("Real XP combo fired: {} dmg ({} XP) vs {}% HP", lastDropDamage, hitXp, hpPercent);
                        clientThread.invoke(() -> {
                            boolean hasBerserker = Inventory.search().nameContains("Berserker ring").first().isPresent();
                            comboHandler.executeComboPrecheck(hasBerserker);
                        });
                    }
                }
            }
        }
    }

    public void onFakeXpDrop(final FakeXpDrop event)
    {
        int xpGained = event.getXp();
        int damage = xpGained / 4;
        lastDropDamage = damage;
        lastDropSkill = event.getSkill();

        WorldPoint loc = client.getLocalPlayer().getWorldLocation();
        expOverlay.addPopup(String.valueOf(damage), loc);

        if (!BOUNTY_HUNTER.contains(loc)) {
            return;
        }

        // Validate skill type
        ExpType type = config.expType();
        boolean validDrop = false;

        if (type == ExpType.RANGED && event.getSkill() == Skill.RANGED) {
            validDrop = true;
        } else if (type == ExpType.MELEE) {
            validDrop = (event.getSkill() == Skill.ATTACK || event.getSkill() == Skill.STRENGTH || event.getSkill() == Skill.DEFENCE);
        }

        if (!validDrop || damage < config.specOverExp()) {
            return;
        }

        Player me = client.getLocalPlayer();
        if (me.getInteracting() instanceof Player) {
            comboHandler.setLastTargetedPlayer((Player) me.getInteracting());
        }

        Player target = comboHandler.getLastTargetedPlayer();
        if (target == null || target.getHealthRatio() <= 0 || target.getHealthScale() <= 0) {
            return;
        }

        int hpPercent = (int) (((double) target.getHealthRatio() / target.getHealthScale()) * 100);
        boolean hpCheck = config.useTargetHP()
                ? hpPercent <= config.targetHPForCombo()
                : hpPercent <= 90;

        StacksForCombo mode = config.stacksForCombo();
        if (mode == StacksForCombo.OFF) {
            if (hpCheck) {
                log.info("Fake XP combo fired: {} dmg vs {}% HP on {}", damage, hpPercent, target.getName());
                delayedCombo = true;

                if (config.whackAfterSpecial()) {
                    comboHandler.queueWhack();
                }
            }
            return;
        }

        // Stack-based logic
        List<Integer> stackSequence = getConfiguredXpSequence();
        recentXpHits.add(damage);
        if (recentXpHits.size() > stackSequence.size()) {
            recentXpHits.removeFirst();
        }

        boolean match = true;
        if (recentXpHits.size() == stackSequence.size()) {
            for (int i = 0; i < stackSequence.size(); i++) {
                if (!recentXpHits.get(i).equals(stackSequence.get(i))) {
                    match = false;
                    break;
                }
            }
        } else {
            match = false;
        }

        if (match && hpCheck) {
            log.info("Stacked XP combo fired: {} vs {}% HP on {}", recentXpHits, hpPercent, target.getName());
            delayedCombo = true;
            recentXpHits.clear();

            if (config.whackAfterSpecial()) {
                comboHandler.queueWhack();
            }
        }
    }



    private List<Integer> getConfiguredXpSequence() {
        String[] parts = config.stacksAmount().split(",");
        int count = config.stacksForCombo().ordinal();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < Math.min(parts.length, count); i++) {
            try {
                list.add(Integer.parseInt(parts[i].trim()));
            } catch (NumberFormatException ignored) {}
        }
        return list;
    }

    public List<Integer> getRecentXpHits() {
        return new ArrayList<>(recentXpHits);
    }


    public void resetXpTracking() {
        prevAttackXp = client.getSkillExperience(Skill.ATTACK);
        prevStrengthXp = client.getSkillExperience(Skill.STRENGTH);
        prevDefenceXp = client.getSkillExperience(Skill.DEFENCE);
        prevRangedXp = client.getSkillExperience(Skill.RANGED);
        log.info("XP tracking reset.");
    }



}
