package net.runelite.client.live.inDevelopment.biggs.BiggsPhosani;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

@Slf4j
public class BiggsPhosaniOverlay extends Overlay {
    private final BiggsPhosaniPlugin plugin;
    private final BiggsPhosaniConfig config;
    private final PanelComponent panelComponent = new PanelComponent();
    private final Client client;

    @Inject
    public BiggsPhosaniOverlay(BiggsPhosaniPlugin plugin, BiggsPhosaniConfig config, Client client) {
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Duration runtime = Duration.between(plugin.getStartTime(), Instant.now());
        panelComponent.getChildren().clear();
        panelComponent.setBackgroundColor(new Color(30, 30, 30, 175));

        // Main Overlay Header
        addHeaderSection(runtime);

        addStatsSection(); // STATS

        // Debug Information (if enabled)
        if (config.debug()) {
            addDebugSection();
        }

        // Phase Information
        if (config.phaseOverlay()) {
            addPhaseSection();
        }

        // Status and Movement Information
        if (config.statusOverlay()) {
            addStatusSection();
        }
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth("[Polar] Phosani's Nightmare") + 75,
                0
        ));

        return panelComponent.render(graphics);
    }

    private void addHeaderSection(Duration runtime) {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("[Polar] Phosani's Nightmare")
                .color(ColorUtil.fromHex("#00a6ff"))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Elapsed Time:")
                .right(formatDuration(runtime))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left(String.valueOf(plugin.getCurrentState()))
                .right(plugin.isPluginRunning() ? "Running" : "Paused")
                .rightColor(plugin.isPluginRunning() ? Color.GREEN : Color.RED)
                .build());

    }


    private void addStatsSection() {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("[Statistics]")
                .color(Color.ORANGE)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Stage:")
                .right(String.valueOf(plugin.getStage()))
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left(String.format("HP: %d / %d",
                        client.getBoostedSkillLevel(Skill.HITPOINTS),
                        client.getRealSkillLevel(Skill.HITPOINTS)))
                .right(String.format("Prayer: %d / %d",
                        client.getBoostedSkillLevel(Skill.PRAYER),
                        client.getRealSkillLevel(Skill.PRAYER)))
                .leftColor(Color.PINK)
                .rightColor(Color.CYAN)
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Total Kills:")
                .right(String.valueOf(plugin.killCount))
                .rightColor(Color.GREEN)
                        .leftColor(Color.RED)
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());
        if (plugin.fightingPhosani && plugin.getFightStartTime() != null) {
            Duration elapsedTime = Duration.between(plugin.getFightStartTime(), Instant.now());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current Fight Time:")
                    .right(formatDuration(elapsedTime))
                    .leftFont(new Font("Arial", Font.PLAIN, 10))
                    .rightFont(new Font("Arial", Font.PLAIN, 10))
                    .build());
        }
        if (plugin.getBestFightTime() != null) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Best Fight Time:")
                    .right(formatDuration(plugin.getBestFightTime()))
                    .leftFont(new Font("Arial", Font.PLAIN, 10))
                    .rightFont(new Font("Arial", Font.PLAIN, 10))
                    .build());
        }
    }



    private void addDebugSection() {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Debug Information")
                .color(Color.ORANGE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Bank Progress:")
                .right(String.valueOf(plugin.bankProgress))
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());


        panelComponent.getChildren().add(LineComponent.builder()
                .left("Traversal Progress:")
                .right(String.valueOf(plugin.getTraversalProgress()))
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Is Healed:")
                .right(plugin.isHealed() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("In PoH:")
                .right(plugin.isInPOH() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Drakan's Medallion:")
                .right(plugin.hasDrakansMedallion() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Rune Pouch:")
                .right(plugin.hasRunePouch() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Needs Food:")
                .right(plugin.needsMoreFood() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Needs Prayer Restore:")
                .right(plugin.needsMorePrayerRestores() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Needs Boost Potions:")
                .right(plugin.needsMoreBoostPotions() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Needs Cure:")
                .right(plugin.needsMoreCures() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("In TOB Bank Area:")
                .right(plugin.isInTOBBankArea() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("In Nightmare Lobby:")
                .right(plugin.isInNightmareLobby() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("In Nightmare:")
                .right(plugin.isInNightmare() ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Fighting:")
                .right(plugin.fightingPhosani ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());
    }

    private void addPhaseSection() {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("[Phase Information]")
                .color(Color.ORANGE)
                .build());

        panelComponent.getChildren().add(createPhaseLine("Final Phase:", plugin.isFinalPhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Dark Hole Phase:", plugin.isDarkHolePhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Flower Phase:", plugin.isFlowerPhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Husk Phase:", plugin.isHuskPhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Shroom Phase:", plugin.isMushroomPhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Parasite Phase:", plugin.isParasitePhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Charge Phase:", plugin.isChargePhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Totem Phase:", plugin.isTotemPhase(plugin.getNightmare())));
        panelComponent.getChildren().add(createPhaseLine("Sleepwalker Phase:", plugin.isSleepwalkerPhase(plugin.getNightmare())));
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Sleepwalkers Spawned:")
                .right(String.valueOf(plugin.sleepwalkersSpawnedStorage))
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Sleepwalkers Remaining:")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .right(String.valueOf(plugin.getRemainingSleepwalkers()))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Sleepwalkers Killed:")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .right(String.valueOf(plugin.totalSleepwalkersKilled))
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Sleepwalkers Attacked:")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .right(String.valueOf(plugin.sleepwalkersAttacked))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Sleepwalker Wait:")
                .right(plugin.waitingForWalkers ? "Waiting for sleepwalker!" : "Not waiting.")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .rightColor(plugin.waitingForWalkers ? Color.MAGENTA : Color.BLACK)
                .build());
    }

    private void addStatusSection() {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("[Status and Movement]")
                .color(Color.ORANGE)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Avoid Charge:")
                .right(plugin.isInChargePhase ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .rightColor(plugin.backwardsPrayers ? Color.GREEN : Color.RED)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Pregnant:")
                .right(String.valueOf(plugin.isPregnant()))
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());


        panelComponent.getChildren().add(LineComponent.builder()
                .left("Current Prayer:")
                .right(String.valueOf(plugin.getCurrentPrayer()))
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Prayer shuffle:")
                .right(plugin.backwardsPrayers ? "Yes" : "No")
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .rightColor(plugin.backwardsPrayers ? Color.GREEN : Color.RED)
                .build());
    }

    private LineComponent createPhaseLine(String label, boolean condition) {
        return LineComponent.builder()
                .left(label)
                .right(condition ? "Yes" : "No")
                .rightColor(condition ? Color.GREEN : Color.RED)
                .leftFont(new Font("Arial", Font.PLAIN, 10))
                .rightFont(new Font("Arial", Font.PLAIN, 10))
                .build();
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        return String.format("%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
    }
}
