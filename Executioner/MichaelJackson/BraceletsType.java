package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson;

import net.runelite.api.ItemID;

public enum BraceletsType {
    SLAUGHTER(ItemID.BRACELET_OF_SLAUGHTER),
    EXPEDITIOUS(ItemID.EXPEDITIOUS_BRACELET),
    NONE(-1);
    final int itemID;
    BraceletsType(int itemID) {
        this.itemID = itemID;
    }
    public int getItemID() {
        return this.itemID;
    }
}