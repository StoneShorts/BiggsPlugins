package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson.FuckKamala;

import net.runelite.api.ItemID;

public enum BoltAmmoSelect {
    // Arrows
    NONE(0, ""),  // No item associated
    BRONZE_BOLT(ItemID.BRONZE_BOLTS, "Bronze bolt"),
    IRON_BOLT(ItemID.IRON_BOLTS, "Iron bolt"),
    STEEL_BOLT(ItemID.STEEL_BOLTS, "Steel bolt"),
    MITHRIL_BOLT(ItemID.MITHRIL_BOLTS, "Mithril bolt"),
    ADAMANT_BOLT(ItemID.ADAMANT_BOLTS, "Adamant bolt"),
    BROAD_BOLT(ItemID.BROAD_BOLTS, "Broad bolt"),
    AMETHYST_BROAD_BOLT(ItemID.AMETHYST_BROAD_BOLTS, "Amethyst broad bolt"),
    RUNITE_BOLT(ItemID.RUNITE_BOLTS, "Runite bolt"),
    DRAGON_BOLT(ItemID.DRAGON_BOLTS, "Dragon bolt"),
    OPAL_BOLTS_E(ItemID.OPAL_BOLTS_E, "Opal bolts (e)"),
    JADE_BOLTS_E(ItemID.JADE_BOLTS_E, "Jade bolts (e)"),
    PEARL_BOLTS_E(ItemID.PEARL_BOLTS_E, "Pearl bolts (e)"),
    TOPAZ_BOLTS_E(ItemID.TOPAZ_BOLTS_E, "Topaz bolts (e)"),
    SAPPHIRE_BOLTS_E(ItemID.SAPPHIRE_BOLTS_E, "Sapphire bolts (e)"),
    EMERALD_BOLTS_E(ItemID.EMERALD_BOLTS_E, "Emerald bolts (e)"),
    RUBY_BOLTS_E(ItemID.RUBY_BOLTS_E, "Ruby bolts (e)"),
    DIAMOND_BOLTS_E(ItemID.DIAMOND_BOLTS_E, "Diamond bolts (e)"),
    DRAGONSTONE_BOLTS_E(ItemID.DRAGONSTONE_BOLTS_E, "Dragonstone bolts (e)"),
    ONYX_BOLTS_E(ItemID.ONYX_BOLTS_E, "Onyx bolts (e)"),
    OPAL_DRAGON_BOLTS_E(ItemID.OPAL_DRAGON_BOLTS_E, "Opal dragon bolts (e)"),
    JADE_DRAGON_BOLTS_E(ItemID.JADE_DRAGON_BOLTS_E, "Jade dragon bolts (e)"),
    PEARL_DRAGON_BOLTS_E(ItemID.PEARL_DRAGON_BOLTS_E, "Pearl dragon bolts (e)"),
    TOPAZ_DRAGON_BOLTS_E(ItemID.TOPAZ_DRAGON_BOLTS_E, "Topaz dragon bolts (e)"),
    SAPPHIRE_DRAGON_BOLTS_E(ItemID.SAPPHIRE_DRAGON_BOLTS_E, "Sapphire dragon bolts (e)"),
    EMERALD_DRAGON_BOLTS_E(ItemID.EMERALD_DRAGON_BOLTS_E, "Emerald dragon bolts (e)"),
    RUBY_DRAGON_BOLTS_E(ItemID.RUBY_DRAGON_BOLTS_E, "Ruby dragon bolts (e)"),
    DIAMOND_DRAGON_BOLTS_E(ItemID.DIAMOND_DRAGON_BOLTS_E, "Diamond dragon bolts (e)"),
    DRAGONSTONE_DRAGON_BOLTS_E(ItemID.DRAGONSTONE_DRAGON_BOLTS_E, "Dragonstone dragon bolts (e)"),
    ONYX_DRAGON_BOLTS_E(ItemID.ONYX_DRAGON_BOLTS_E, "Onyx dragon bolts (e)");

    private final int id;
    private final String displayName; // Friendly name for in-game use

    BoltAmmoSelect(int ammoId, String displayName) {
        this.id = ammoId;
        this.displayName = displayName;
    }

    BoltAmmoSelect() {
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

