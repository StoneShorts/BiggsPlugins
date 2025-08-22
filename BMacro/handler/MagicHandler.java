package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.live.inDevelopment.biggs.BMacro.PolarMacroConfig;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.Packets.MousePackets;
import net.runelite.client.live.polarbot.util.PolarAPI.utils.Packets.WidgetPackets;

@Slf4j
public class MagicHandler
{
    private static final WorldArea BH_BANK = new WorldArea(3415, 4055, 17, 18, 0);
    private static final int GREATER_CORRUPTION_WIDGET_ID = 14287024;

    private final Client client;
    private final ClientThread clientThread;
    private final PolarMacroConfig config;

    @Inject
    public MagicHandler(Client client, ClientThread clientThread, PolarMacroConfig config)
    {
        this.client = client;
        this.clientThread = clientThread;
        this.config = config;
    }

    public void tryCastGreaterCorruption()
    {
        if (!config.useGreaterCorruption()) return;
        if (client.getVarbitValue(Varbits.CORRUPTION_COOLDOWN) != 0) return;
        if (isInBHBank()) return;

        // ✅ Only cast if we have a valid player target
        Player me = client.getLocalPlayer();
        if (me == null || !(me.getInteracting() instanceof Player)) {
            return;
        }

        log.info("-----Casting Greater Corruption");

        clientThread.invokeLater(() -> {
            Widget widget = client.getWidget(GREATER_CORRUPTION_WIDGET_ID);
            if (widget != null) {
                MousePackets.queueWidgetClickPacket(widget);
                WidgetPackets.queueWidgetAction(widget, "Cast");
            } else {
                log.warn("-----Could not find Greater Corruption widget (ID: {}).", GREATER_CORRUPTION_WIDGET_ID);
            }
        });
    }


    private boolean isInBHBank()
    {
        if (client.getLocalPlayer() == null) {
            return false;
        }

        WorldPoint location = client.getLocalPlayer().getWorldLocation();
        return BH_BANK.contains(location);
    }
}
