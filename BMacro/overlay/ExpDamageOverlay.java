package net.runelite.client.live.inDevelopment.biggs.BMacro.overlay;

import com.google.inject.Singleton;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.Inventory;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.InteractionUtils.InventoryInteraction;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.api.Perspective;
import net.runelite.api.events.GameTick;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class ExpDamageOverlay extends Overlay {
    private final Client client;
    private final ClientThread clientThread;
    private final PolarMacroConfig config;
    private final SpriteManager spriteManager;
    private final MouseManager mouseManager;

    private Rectangle hpBounds, prayerBounds, specBounds;
    private int tripleEatTicksLeft;
    private int prayerSafetyTicksLeft;

    private static class Popup {
        private final String text;
        private final WorldPoint location;
        private int ticksLeft;

        Popup(String text, WorldPoint location, int ticks) {
            this.text = text;
            this.location = location;
            this.ticksLeft = ticks;
        }
    }

    private static final float[] EXP_OVERLAY_SPEEDS = {
            0.7f, 1.2f, 1.6f, 2.0f, 2.5f, 3.0f, 4.0f
    };

    private final List<Popup> popups = new LinkedList<>();

    @Inject
    public ExpDamageOverlay(Client client, ClientThread clientThread, PolarMacroConfig config, SpriteManager spriteManager, MouseManager mouseManager) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
        this.spriteManager = spriteManager;
        this.mouseManager = mouseManager;
    }

    public void startup() {
        mouseManager.registerMouseListener(mouseAdapter);
    }

    public void shutdown() {
        mouseManager.unregisterMouseListener(mouseAdapter);
    }

    private final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public MouseEvent mousePressed(MouseEvent event) {
            if (event.getButton() != MouseEvent.BUTTON3) return event;

            java.awt.Point clickPoint = event.getPoint();

            clientThread.invokeLater(() -> {
                if (specBounds != null && specBounds.contains(clickPoint) && getSpecialAttackEnergy() >= 10) {
                    PolarAPI.enableSpec();
                    PolarAPI.sendGameMessage("<col=fc8403>Attempting to enable special attack with: " + getSpecialAttackEnergy() + " energy, via player actionbar.</col>");
                }

                if (hpBounds != null && hpBounds.contains(clickPoint)) {
                    Inventory.search().withAction("Eat")
                            .first()
                            .ifPresent(item -> {
                                InventoryInteraction.useItem(item, "Eat");
                                PolarAPI.sendGameMessage("<col=fc8403>Attempting to eat item: " + item.getName() + " via player actionbar.</col>");
                            });
                }

                if (prayerBounds != null && prayerBounds.contains(clickPoint)) {
                    Inventory.search().withAction("Drink")
                            .nameContainsAny(new String[]{"restore", "prayer", "sanfew"})
                            .first()
                            .ifPresent(item -> {
                                InventoryInteraction.useItem(item, "Drink");
                                PolarAPI.sendGameMessage("<col=fc8403>Attempting to restore prayer with item: " + item.getName() + " via player actionbar.</col>");
                            });
                }
            });
            return event;
        }
    };

    public void onGameTick(GameTick event) {
        if (tripleEatTicksLeft > 0) tripleEatTicksLeft--;
        if (prayerSafetyTicksLeft > 0) prayerSafetyTicksLeft--;

        synchronized (popups) {
            popups.removeIf(p -> --p.ticksLeft <= 0);
        }
    }

    public void addPopup(String text, WorldPoint location) {
        if (location == null || text == null) return;

        float seconds = EXP_OVERLAY_SPEEDS[config.expoverlayspeed().ordinal()];
        int ticks = Math.max(1, Math.round(seconds / 0.6f));

        synchronized (popups) {
            popups.add(new Popup(text, location, ticks));
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Player localPlayer = client.getLocalPlayer();
        if (localPlayer == null) return null;

        if (config.hpprayeroverlaytoggle()) {
            int hp = getBoosted(Skill.HITPOINTS);
            int baseHp = getBase(Skill.HITPOINTS);
            int prayer = getBoosted(Skill.PRAYER);
            int basePrayer = getBase(Skill.PRAYER);
            int spec = getSpecialAttackEnergy();

            LocalPoint lp = localPlayer.getLocalLocation();
            Point base = Perspective.getCanvasTextLocation(client, graphics, lp, " ", 0);
            if (base != null) {
                int x = base.getX();
                int y = base.getY() - 120;

                hpBounds = drawStat(graphics, spriteManager.getSprite(SpriteID.SKILL_HITPOINTS, 0), x - 60, y, hp, baseHp, Color.RED);
                prayerBounds = drawStat(graphics, spriteManager.getSprite(SpriteID.SKILL_PRAYER, 0), x - 10, y, prayer, basePrayer, Color.CYAN);
                specBounds = drawStat(graphics, spriteManager.getSprite(SpriteID.UNKNOWN_DRAGON_DAGGER_P, 0), x + 40, y, spec, 100, Color.GREEN);

                if (tripleEatTicksLeft > 0) {
                    BufferedImage tripleEatIcon = spriteManager.getSprite(SpriteID.MINIMAP_ORB_HITPOINTS_ICON, 0);
                    if (tripleEatIcon != null) {
                        graphics.drawImage(tripleEatIcon, x - 60 + 16 - tripleEatIcon.getWidth() / 2, y - tripleEatIcon.getHeight() - 6, null);
                    }
                }

                if (prayerSafetyTicksLeft > 0) {
                    BufferedImage prayerIcon = spriteManager.getSprite(SpriteID.UNKNOWN_PRAYER_ICON, 0);
                    if (prayerIcon != null) {
                        graphics.drawImage(prayerIcon, x - 10 + 16 - prayerIcon.getWidth() / 2, y - prayerIcon.getHeight() - 6, null);
                    }
                }
            }
        }

        float fadeSeconds = EXP_OVERLAY_SPEEDS[config.expoverlayspeed().ordinal()];
        int maxTicks = Math.max(1, Math.round(fadeSeconds / 0.6f));

        synchronized (popups) {
            for (Iterator<Popup> it = popups.iterator(); it.hasNext(); ) {
                Popup popup = it.next();
                if (popup.ticksLeft <= 0) {
                    it.remove();
                    continue;
                }

                LocalPoint lp = LocalPoint.fromWorld(client, popup.location);
                if (lp == null) continue;

                Point screenPoint = Perspective.getCanvasTextLocation(client, graphics, lp, popup.text, 20);
                if (screenPoint == null) continue;

                float progress = (maxTicks - popup.ticksLeft) / (float) maxTicks;
                int offsetY = (int)(progress * 30);
                int alpha = (int)(255 * (1.0f - progress));

                Color baseColor = config.lowDamageColor();
                try {
                    int dmg = Integer.parseInt(popup.text.replaceAll("[^0-9]", ""));
                    if (dmg >= config.specOverExp())
                        baseColor = config.highDamageColor();
                } catch (NumberFormatException ignored) {}

                Color faded = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), Math.min(alpha, baseColor.getAlpha()));

                graphics.setFont(new Font("Arial", Font.BOLD, 14));
                graphics.setColor(faded);
                FontMetrics fm = graphics.getFontMetrics();
                int width = fm.stringWidth(popup.text);

                graphics.drawString(
                        popup.text,
                        screenPoint.getX() - (width / 2),
                        screenPoint.getY() - offsetY
                );

                popup.ticksLeft--;
            }
        }

        return null;
    }

    private Rectangle drawStat(Graphics2D g, BufferedImage sprite, int x, int y, int curr, int max, Color color) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, null);
            g.setColor(color);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            String percent = Math.min(100, Math.round((curr / (float) max) * 100)) + "%";
            String tooltip = curr + "/" + max;
            FontMetrics fm = g.getFontMetrics();
            g.drawString(percent, x + 15 - fm.stringWidth(percent) / 2, y + 36);

            if ((curr < 30 && color == Color.RED) || (curr < 20 && color == Color.CYAN)) {
                g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                g.fillOval(x - 4, y - 4, 36, 36);
            }

            g.setColor(Color.YELLOW);
            g.drawString(tooltip, x + 15 - fm.stringWidth(tooltip) / 2, y + 48);

            return new Rectangle(x, y, 32, 32);
        }
        return null;
    }

    public int getSpecialAttackEnergy() {
        return client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
    }

    private int getBoosted(Skill skill) {
        return client.getBoostedSkillLevel(skill);
    }

    private int getBase(Skill skill) {
        return client.getRealSkillLevel(skill);
    }

    public void flashTripleEat() {
        tripleEatTicksLeft = 4;
    }

    public void flashPrayerSafety() {
        prayerSafetyTicksLeft = 4;
    }
}
