package net.runelite.client.live.inDevelopment.biggs.BMacro.config;

public enum HidePlayer {
    FIVE(5),
    TEN(10),
    FIFTEEN(15);

    private final int value;

    HidePlayer(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}