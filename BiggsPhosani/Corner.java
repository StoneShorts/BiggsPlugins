package net.runelite.client.live.inDevelopment.biggs.BiggsPhosani;

import lombok.Getter;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

@Getter
public enum Corner {
    NORTH_EAST(new LocalPoint(6720, 7616)),
    SOUTH_EAST(new LocalPoint(6729, 6592)),
    SOUTH_WEST(new LocalPoint(5696, 7616)),
    NORTH_WEST(new LocalPoint(5696, 6592));

    private final LocalPoint point;

    Corner(LocalPoint point) {
        this.point = point;
    }

}
