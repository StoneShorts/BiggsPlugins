package net.runelite.client.live.inDevelopment.biggs.BMacro.overlay;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.inDevelopment.biggs.BMacro.config.StacksForCombo;
import net.runelite.client.live.inDevelopment.biggs.BMacro.handler.ComboHandler;
import net.runelite.client.live.inDevelopment.biggs.BMacro.handler.PrayerHandler;
import net.runelite.client.live.inDevelopment.biggs.BMacro.handler.XPHandler;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;

public class PolarMacroOverlay extends OverlayPanel
{
    private final Client client;
    private final ClientThread clientThread;
    private final XPHandler xpHandler;
    private final PrayerHandler prayerHandler;
    private final ComboHandler comboHandler;
    private final PolarMacroConfig config;

    @Setter @Getter
    private int lastHitDamage = 0;

    @Setter
    private Skill lastHitSkill = null;

    @Setter
    private Player target;

    private boolean prayerModeEnabled;
    private int lastAnimId = -1;

    @Inject
    public PolarMacroOverlay(Client client, ClientThread clientThread, XPHandler xpHandler, PrayerHandler prayerHandler, PolarMacroConfig config, ComboHandler comboHandler)
    {
        super();
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.clientThread = clientThread;
        this.xpHandler = xpHandler;
        this.prayerHandler = prayerHandler;
        this.comboHandler = comboHandler;
        this.config = config;
    }

    public void setPrayerMode(boolean enabled)
    {
        this.prayerModeEnabled = enabled;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.expoverlaytoggle() || client.getLocalPlayer() == null)
            return null;

        panelComponent.getChildren().clear();

        // Header
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Polar Macro Overlay")
                .color(Color.CYAN)
                .build());

        // Basic info
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Prayer Mode:")
                .right(prayerModeEnabled ? "ON" : "OFF")
                .rightColor(prayerModeEnabled ? Color.GREEN : Color.RED)
                .build());

        // Local player anim ID
        int anim = client.getLocalPlayer().getAnimation();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("My Anim ID:")
                .right(String.valueOf(anim))
                .rightColor(Color.YELLOW)
                .build());

        // Draw anim as floating text
        net.runelite.api.Point textLoc = Perspective.getCanvasTextLocation(client, graphics, client.getLocalPlayer().getLocalLocation(), String.valueOf(anim), 40);
        if (textLoc != null)
        {
            graphics.setFont(new Font("Arial", Font.BOLD, 12));
            graphics.setColor(Color.YELLOW);
            graphics.drawString(String.valueOf(anim), textLoc.getX(), textLoc.getY());
        }

        // Weapon name
        Item weapon = client.getItemContainer(InventoryID.EQUIPMENT) != null
                ? client.getItemContainer(InventoryID.EQUIPMENT).getItem(EquipmentInventorySlot.WEAPON.getSlotIdx())
                : null;

        String weaponName = (weapon != null)
                ? client.getItemDefinition(weapon.getId()).getName()
                : "None";

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Weapon:")
                .right(weaponName)
                .rightColor(Color.ORANGE)
                .build());

        // Target info
        if (target != null)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Target:")
                    .right(target.getName())
                    .build());

            if (target.getHealthRatio() > 0 && target.getHealthScale() > 0)
            {
                int hpPercent = (int)(((double) target.getHealthRatio() / target.getHealthScale()) * 100);
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Target HP %:")
                        .right(hpPercent + "%")
                        .build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Threshold:")
                        .right(config.targetHPForCombo() + "%")
                        .build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("HP Req For Spec?:")
                        .right(config.useTargetHP() ? "YES" : "NO")
                        .rightColor(config.useTargetHP() ? Color.GREEN.darker() : Color.LIGHT_GRAY)
                        .build());
            }

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Target Anim:")
                    .right(String.valueOf(target.getAnimation()))
                    .build());
        }

        // Last XP drop
        if (lastHitSkill != null && lastHitSkill != Skill.HITPOINTS)
        {
            String skillStr = lastHitSkill.getName();
            Color dmgColor = lastHitDamage >= config.specOverExp() ? config.highDamageColor() : config.lowDamageColor();

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Last Hit:")
                    .right(lastHitDamage + " (" + skillStr + ")")
                    .rightColor(dmgColor)
                    .build());
        }

        // XP Stack tracker
        int stackGoal = config.stacksForCombo() == StacksForCombo.OFF ? 0 : config.stacksForCombo().ordinal();
        String stackDisplay = stackGoal == 0 ? "OFF" : xpHandler.getRecentXpHits().size() + "/" + stackGoal;

        panelComponent.getChildren().add(LineComponent.builder()
                .left("XP Stack Combo:")
                .right(stackDisplay)
                .rightColor(stackGoal == 0 ? Color.GRAY : Color.MAGENTA.darker())
                .build());

        // Combo logic state

        if (target != null && target.getHealthRatio() > 0 && target.getHealthScale() > 0)
        {
            int hp = (int)(((double)target.getHealthRatio() / target.getHealthScale()) * 100);
            boolean ready = hp <= config.targetHPForCombo() && lastHitDamage >= config.specOverExp();

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Combo Ready:")
                    .right(ready ? "YES" : "NO")
                    .rightColor(ready ? Color.GREEN.darker() : Color.ORANGE)
                    .build());
        }

        // Draw tile on target
        if (target != null && target.getCanvasTilePoly() != null)
        {
            graphics.setColor(new Color(218, 24, 218, 219));
            graphics.draw(target.getCanvasTilePoly());
        }

        return super.render(graphics);
    }

    public void setLastHit(int damage, Skill skill)
    {
        this.lastHitDamage = damage;
        this.lastHitSkill = skill;
    }

    private int percent(Skill s)
    {
        int curr = client.getBoostedSkillLevel(s);
        int base = client.getRealSkillLevel(s);
        return base == 0 ? 0 : Math.round((curr / (float) base) * 100f);
    }

    public void setLastAnimationId(int id)
    {
        this.lastAnimId = id;
    }

    public Skill getLastHitSkill()
    {
        return lastHitSkill;
    }
}
