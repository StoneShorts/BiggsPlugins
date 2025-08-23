package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson;

import net.runelite.api.Prayer;

public enum OffensivePrayerTypeMagic {
    AUGURY(Prayer.AUGURY),
    MYSTIC_MIGHT(Prayer.MYSTIC_MIGHT),
    MYSTIC_LORE(Prayer.MYSTIC_LORE),
    NONE(null);
    private final Prayer prayer;

    OffensivePrayerTypeMagic(Prayer prayer) {
        this.prayer = prayer;
    }

    public Prayer getPrayer() {
        return prayer;
    }
}
