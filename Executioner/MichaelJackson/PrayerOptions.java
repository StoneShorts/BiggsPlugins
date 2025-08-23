package net.runelite.client.live.inDevelopment.biggs.Executioner.MichaelJackson;

import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarUtils.API.BankUtil;
import net.runelite.api.ItemID;

public enum PrayerOptions {
    PRAYER_POTION(new int[]{ItemID.PRAYER_POTION4, ItemID.PRAYER_POTION3, ItemID.PRAYER_POTION2, ItemID.PRAYER_POTION1}),
    SUPER_RESTORE(new int[]{ItemID.SUPER_RESTORE4, ItemID.SUPER_RESTORE3, ItemID.SUPER_RESTORE2, ItemID.SUPER_RESTORE1}),
    BLIGHTED_SUPER_RESTORE(new int[]{ItemID.BLIGHTED_SUPER_RESTORE4, ItemID.BLIGHTED_SUPER_RESTORE3, ItemID.BLIGHTED_SUPER_RESTORE2, ItemID.BLIGHTED_SUPER_RESTORE1});

    private final int[] itemIds;

    PrayerOptions(int[] itemIds) {
        this.itemIds = itemIds;
    }

    public int[] getItemIds() {
        return itemIds;
    }

    // Utility method to fetch the first available item from the list, prioritizing higher doses
    public Integer getFirstAvailableItemId() {
        for (int itemId : itemIds) {
            if (BankUtil.hasItem(itemId)) {
                return itemId;
            }
        }
        return null;
    }
}
