package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson;

import net.runelite.api.Prayer;

public enum OffensivePrayerTypeRanged {
    RIGOUR(Prayer.RIGOUR),
    EAGLE_EYE(Prayer.EAGLE_EYE),
    HAWK_EYE(Prayer.HAWK_EYE),
    NONE(null);
    private final Prayer prayer;

    OffensivePrayerTypeRanged(Prayer prayer) {
        this.prayer = prayer;
    }

    public Prayer getPrayer() {
        return prayer;
    }
}
