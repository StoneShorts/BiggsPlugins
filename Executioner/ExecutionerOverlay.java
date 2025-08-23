package net.runelite.client.live.inDevelopment.biggs.Executioner;

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

public class ExecutionerOverlay extends Overlay {
    private final ExecutionerPlugin plugin;
    private final Client client;
    private final ExecutionerConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public ExecutionerOverlay(final ExecutionerPlugin plugin, ExecutionerConfig config, Client client) {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();
        Duration runtime = Duration.between(plugin.getStartTime(), Instant.now());

        addTitleComponent(graphics, "[Polar] Executioner", "#42e6f5");
        addInfoLine(graphics, "ALPHA TEST VERSION 1", "NOOB", false);

        addInfoLine(graphics, "Status", plugin.isStarted() ? "Running" : "Paused", plugin.isStarted());
        addInfoLine(graphics, "Runtime", formatDuration(runtime), true);
        addInfoLine(graphics, "Delay", String.valueOf(plugin.getTickDelay()), false);
        addInfoLine(graphics, "Hop timer", String.valueOf(plugin.getHopTimer()), false);
/*        if (plugin.needsToGetCannon) {
            addInfoLine(graphics, "Retrieve cannon?", "Yes", false);
        } else {
            addInfoLine(graphics, "Retrieve cannon?", "No", true);
        }*/
        addInfoLine(graphics, "Current state", String.valueOf(plugin.getStatus()), true);
        addInfoLine3(graphics, "★ ★ ★              Slayer Info");
        addInfoLine(graphics, "Slayer level", String.valueOf(client.getRealSkillLevel(Skill.SLAYER)), true);
        if (plugin.currentMonster != null) {
            addInfoLine(graphics, "Current Task", plugin.currentMonster.getNpcName(), true);
        } else {
            addInfoLine(graphics, "Current Task", "No Task", false);
        }
        int slayerTaskSize = plugin.getSlayerTaskSize();
        addInfoLine(graphics, "Monsters Remaining", slayerTaskSize > 0 ? String.valueOf(slayerTaskSize) : "None", slayerTaskSize > 0);
        addInfoLine(graphics, "Slayer Points", String.valueOf(plugin.getSlayerPoints()), true);
        addInfoLine2(graphics, "Tasks Complete", String.valueOf(plugin.getSlayerTasksComplete()), true);

/*        if (plugin.bagFull) {
            addInfoLine(graphics, "Looting Bag", "FULL" ,false);
        } else {
            addInfoLine(graphics, "Looting Bag", "NOT FULL", true);
        }*/


        panelComponent.setBackgroundColor(new Color(30, 30, 30, 175));

        return panelComponent.render(graphics);
    }

    private void addTitleComponent(Graphics2D graphics, String title, String colorHex) {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(title)
                .color(ColorUtil.fromHex(colorHex))
                .build());
        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(title) + 200, 100));
    }

    private void addInfoLine(Graphics2D graphics, String label, String value, boolean isPositive) {
        Font font = new Font("Impact", Font.PLAIN, 18);
        graphics.setFont(font);

        panelComponent.getChildren().add(LineComponent.builder()
                .left(label + ":")
                .leftColor(Color.WHITE)
                .leftFont(font)
                .right(value)
                .rightColor(isPositive ? Color.GREEN : Color.RED)
                .rightFont(font)
                .build());
    }

    private void addInfoLine2(Graphics2D graphics, String label, String value, boolean isPositive) {
        Font font = new Font("Impact", Font.PLAIN, 18);
        graphics.setFont(font);

        panelComponent.getChildren().add(LineComponent.builder()
                .left(label + ":")
                .leftColor(Color.CYAN)
                .leftFont(font)
                .right(value)
                .rightColor(isPositive ? Color.GREEN : Color.RED)
                .rightFont(font)
                .build());
    }

    private void addInfoLine3(Graphics2D graphics, String label) {
        Font font = new Font("Impact", Font.PLAIN, 20);
        graphics.setFont(font);

        panelComponent.getChildren().add(LineComponent.builder()
                .left(label + ":")
                .leftColor(Color.RED)
                .leftFont(font)
                .build());
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        return String.format(
                " %d: %02d: %02d ",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
    }
}
