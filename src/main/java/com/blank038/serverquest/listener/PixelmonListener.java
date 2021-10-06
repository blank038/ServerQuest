package com.blank038.serverquest.listener;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.api.ServerQuestApi;
import com.mc9y.pokemonapi.api.event.ForgeEvent;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class PixelmonListener implements Listener {
    private final ServerQuest INSTANCE;

    public PixelmonListener() {
        this.INSTANCE = ServerQuest.getInstance();
    }

    @EventHandler
    public void onForge(ForgeEvent event) {
        if (event.getForgeEvent() instanceof BeatWildPixelmonEvent) {
            BeatWildPixelmonEvent e = (BeatWildPixelmonEvent) event.getForgeEvent();
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "BEAT_WILD_PIXELMON", 1);
        }
    }
}
