package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
@Getter
public enum KonarMonsterRules {
    SPIDER(
            "Spider",
            new int[]{3853}, // Assuming some required items; replace with actual item IDs
            new WorldArea(3220, 3400, 10, 10, 0),
            new WorldPoint(3225, 3405, 0), // Outside point
            new WorldPoint(3225, 3405, 0), // Inside point, assuming same as outside for simplicity
            new WorldPoint(3228, 3408, 0), // Obstacle point
            21166, // Example traversing method ID
            true, // Use traversal item
            false, // Skip task
            new WorldArea(3220, 3400, 10, 10, 0), // Teleport destination area
            new WorldPoint(3227, 3407, 0), // Inside cave point
            new WorldPoint(3230, 3410, 0)  // Cannon point
    ),
    GOBLIN(
            "Goblin",
            new int[]{3853}, // Assuming some required items; replace with actual item IDs
            new WorldArea(3250, 3420, 15, 15, 0),
            new WorldPoint(3255, 3425, 0),
            new WorldPoint(3255, 3425, 0),
            new WorldPoint(3258, 3428, 0),
            21166,
            true,
            false,
            new WorldArea(3250, 3420, 15, 15, 0),
            new WorldPoint(3257, 3427, 0),
            new WorldPoint(3260, 3430, 0)
    );

    private final String npcName;
    private final int[] requiredSlayerItems; // Can be null
    private final WorldArea npcWorldArea;
    private final WorldPoint outsideWorldPoint;  // Optional
    private final WorldPoint insideWorldPoint;   // Optional
    private final WorldPoint obstacleWorldPoint; // Optional
    private final Integer traversingMethodId;    // Optional, Integer to allow null
    private final boolean useTraversalItem;
    private final boolean skipTask;
    private final WorldArea teleDestinationArea;
    private final WorldPoint insideCavePoint;
    private final WorldPoint cannonPoint;

    KonarMonsterRules(String npcName, int[] requiredSlayerItems, WorldArea npcWorldArea,
                       WorldPoint outsideWorldPoint, WorldPoint insideWorldPoint, WorldPoint obstacleWorldPoint,
                       Integer traversingMethodId, boolean useTraversalItem, boolean skipTask, WorldArea teleDestinationArea,
                       WorldPoint insideCavePoint, WorldPoint cannonPoint) {
        this.npcName = npcName;
        this.requiredSlayerItems = requiredSlayerItems;
        this.npcWorldArea = npcWorldArea;
        this.teleDestinationArea = teleDestinationArea;
        this.outsideWorldPoint = outsideWorldPoint;
        this.insideWorldPoint = insideWorldPoint;
        this.obstacleWorldPoint = obstacleWorldPoint;
        this.traversingMethodId = traversingMethodId;
        this.useTraversalItem = useTraversalItem;
        this.skipTask = skipTask;
        this.insideCavePoint = insideCavePoint;
        this.cannonPoint = cannonPoint;
    }
}
