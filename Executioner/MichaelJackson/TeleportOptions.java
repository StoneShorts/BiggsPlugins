package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson;

import net.runelite.api.ItemID;

public enum TeleportOptions {
    AMULET_OF_GLORY(new int[]{ItemID.AMULET_OF_GLORY1, ItemID.AMULET_OF_GLORY2, ItemID.AMULET_OF_GLORY3, ItemID.AMULET_OF_GLORY4, ItemID.AMULET_OF_GLORY5, ItemID.AMULET_OF_GLORY6});
   // ROYAL_SEED_POD(new int[]{ItemID.ROYAL_SEED_POD});
    // Add other teleports as needed

    private final int[] itemIds;

    TeleportOptions(int[] itemIds) {
        this.itemIds = itemIds;
    }

    public int[] getItemIds() {
        return this.itemIds;
    }
}