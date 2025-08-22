package net.runelite.client.live.inDevelopment.biggs.BiggsPhosani;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.NPCs;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class DebugOverlay extends Overlay {
    private final Client client;
    private final BiggsPhosaniPlugin plugin;

    @Inject
    public DebugOverlay(Client client, BiggsPhosaniPlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE); // Draw above the game world
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (plugin.fightingPhosani) {
            // Highlight Husks
            for (NPC husk : plugin.getHusks()) {
                renderNpcOverlay(graphics, husk, new Color(227, 52, 118, 171)); // blu
            }

            // Highlight Parasites
            for (NPC parasite : plugin.getParasites()) {
                renderNpcOverlay(graphics, parasite, new Color(252, 208, 98, 189)); // Dark
            }

            for (NPC sleepwalker : NPCs.search().withName("Sleepwalker").result()) {
                renderNpcOverlay(graphics, sleepwalker, new Color(15, 0, 16, 166));
            }

            // Highlight Bad Tiles
            for (WorldPoint badTile : plugin.getBadTiles()) {
                renderTileOverlay(graphics, badTile, new Color(203, 19, 19, 29)); // Light Red
            }

            // Highlight Good Flowers
            for (WorldPoint goodFlower : plugin.getGoodFlowers()) {
                renderTileOverlay(graphics, goodFlower, new Color(30, 208, 86, 39)); // Light Green
            }
            for (WorldPoint borderTile : plugin.getSafeBorderTilesAroundPhosani()) {
                renderTileOverlay(graphics, borderTile, new Color(138, 196, 181, 44)); // Light
            }
            //Highlight quadrant
            Rectangle safeBounds = plugin.getCurrentSafeQuadrantBounds();
            if (safeBounds != null) {
                renderQuadrantOverlay(graphics, safeBounds, new Color(162, 215, 157, 34)); // Baby bgreemn
            }
            //Path
            NPC targetedTotem = plugin.getTargetedTotem();
            if (targetedTotem != null) {
                int distance = targetedTotem.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation());
                renderNpcOutlineOverlay(graphics, targetedTotem, new Color(0, 122, 255, 120)); // Blue with transparency
                displayTotemInfo(graphics, targetedTotem, distance);
            }
        }
        return null;
    }








    private String getTotemName(int totemId) {
        switch (totemId) {
            case 9438:
                return "Southeast Totem";
            case 9435:
                return "Southwest Totem";
            case 9441:
                return "Northwest Totem";
            case 9444:
                return "Northeast Totem";
            default:
                return "Unknown Totem";
        }
    }
    private void renderNpcOutlineOverlay(Graphics2D graphics, NPC npc, Color color) {
        if (npc == null) {
            return;
        }

        // Get the convex hull of the NPC
        Shape convexHull = npc.getConvexHull();
        if (convexHull != null) {
            graphics.setColor(color);
            graphics.fill(convexHull);
            graphics.setColor(color.darker());
            graphics.setStroke(new BasicStroke(3)); // Adjust stroke thickness if needed
            graphics.draw(convexHull);
        }
    }
    private void displayTotemInfo(Graphics2D graphics, NPC totem, int distance) {
        String totemName = getTotemName(totem.getId());

        // Display the totem info above the totem
        LocalPoint totemLocation = totem.getLocalLocation();
        if (totemLocation != null) {
            Point textLocation = Perspective.getCanvasTextLocation(client, graphics, totemLocation, totemName + " (" + distance + " tiles)", 2);
            if (textLocation != null) {
                OverlayUtil.renderTextLocation(graphics, textLocation, totemName + " (" + distance + " tiles)", Color.YELLOW);
            }
        }
    }
    private void renderQuadrantOverlay(Graphics2D graphics, Rectangle bounds, Color color) {
        for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
            for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
                WorldPoint point = new WorldPoint(x, y, client.getPlane());
                LocalPoint localPoint = LocalPoint.fromWorld(client, point);

                if (localPoint != null) {
                    Polygon tilePoly = Perspective.getCanvasTilePoly(client, localPoint);
                    if (tilePoly != null) {
                        graphics.setColor(color);
                        graphics.fill(tilePoly); // Fill with transparency
                        graphics.setColor(color.darker());
                        graphics.draw(tilePoly); // Draw outline for visibility
                    }
                }
            }
        }
    }
    private void renderNpcOverlay(Graphics2D graphics, NPC npc, Color color) {
        if (npc == null) return;

        LocalPoint npcLocation = npc.getLocalLocation();
        if (npcLocation == null) return;

        Polygon tilePolygon = Perspective.getCanvasTilePoly(client, npcLocation);
        if (tilePolygon != null) {
            graphics.setColor(color);
            graphics.fill(tilePolygon);
            graphics.setColor(color.darker());
            graphics.setStroke(new BasicStroke(3));
            graphics.draw(tilePolygon);
        }

        String npcName = npc.getName();
        if (npcName != null) {
            renderNpcName(graphics, npc, npcName, color);
        }
    }

    private void renderNpcName(Graphics2D graphics, NPC npc, String npcName, Color color) {
        LocalPoint npcLocation = npc.getLocalLocation();
        Point textLocation = Perspective.getCanvasTextLocation(client, graphics, npcLocation, npcName, 2);

        if (textLocation != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, npcName, color);
        }
    }

    private void renderTileOverlay(Graphics2D graphics, WorldPoint worldPoint, Color color) {
        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null) return;

        Polygon tilePolygon = Perspective.getCanvasTilePoly(client, localPoint);
        if (tilePolygon != null) {
            graphics.setColor(color);
            graphics.fill(tilePolygon);
            graphics.setColor(color.darker());
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(tilePolygon);
        }
    }
}
