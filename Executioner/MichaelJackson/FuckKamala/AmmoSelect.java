package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala;

import net.runelite.api.ItemID;

public enum AmmoSelect {
    // Arrows
    NONE(0, ""),  // No item associated
    BRONZE_ARROW(ItemID.BRONZE_ARROW, "Bronze arrow"),
    IRON_ARROW(ItemID.IRON_ARROW, "Iron arrow"),
    STEEL_ARROW(ItemID.STEEL_ARROW, "Steel arrow"),
    MITHRIL_ARROW(ItemID.MITHRIL_ARROW, "Mithril arrow"),
    ADAMANT_ARROW(ItemID.ADAMANT_ARROW, "Adamant arrow"),
    RUNE_ARROW(ItemID.RUNE_ARROW, "Rune arrow"),
    AMETHYST_ARROW(ItemID.AMETHYST_ARROW, "Amethyst arrow"),
    DRAGON_ARROW(ItemID.DRAGON_ARROW, "Dragon arrow");

    private final int id;
    private final String displayName; // Friendly name for in-game use

    AmmoSelect(int ammoId, String displayName) {
        this.id = ammoId;
        this.displayName = displayName;
    }

    AmmoSelect() {
        this.id = -1; // or some other logic for NONE
        this.displayName = "";
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}