package net.runelite.client.live.inDevelopment.biggs.BiggsPhosani;

public enum State {
    TOB_BANK("Preparing at ToB Bank.."),
    RESTOCKING("Restocking supplies.."),
    NIGHTMARE_TRAVERSAL("Traveling to Phosani's Nightmare.."),
    NIGHTMARE_LOBBY("Handling Nightmare Lobby.."),
    NIGHTMARE_COMBAT("In combat with the Phosani's Nightmare.."),
    POH_HEALING("Healing at the Player-Owned House.."),
    DRAKANS_RETURN("Returning via Drakan's Medallion.."),
    IDLE("Idle.."),
    TIMEOUT("Timeout.");

    private final String description;

    State(String description) {
        this.description = description;
    }

    public String getActivity() {
        return description;
    }
}
