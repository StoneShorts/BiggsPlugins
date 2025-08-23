package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson;

import net.runelite.api.Prayer;

public enum OffensivePrayerTypeMelee {
    PIETY(Prayer.PIETY),
    CHIVALRY(Prayer.CHIVALRY),
    ULTIMATE_STRENGTH(Prayer.ULTIMATE_STRENGTH),
    IMPROVED_REFLEXES(Prayer.IMPROVED_REFLEXES),
    
    NONE(null);

    private final Prayer prayer;

    OffensivePrayerTypeMelee(Prayer prayer) {
        this.prayer = prayer;
    }

    public Prayer getPrayer() {
        return prayer;
    }
}
