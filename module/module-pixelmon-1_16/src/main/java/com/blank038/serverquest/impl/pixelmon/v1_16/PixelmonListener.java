package com.blank038.serverquest.impl.pixelmon.v1_16;

import com.blank038.serverquest.api.ServerQuestApi;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.battles.BattleEndCause;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.LevelUpEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.raids.EndRaidEvent;
import com.pixelmonmod.pixelmon.battles.raids.RaidData;
import net.minecraftforge.eventbus.api.EventPriority;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * @author Blank038
 */
public class PixelmonListener implements Listener {

    public PixelmonListener() {
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, BeatWildPixelmonEvent.class, (e) -> {
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUUID()), e.wpp.allPokemon[0].pokemon.getSpecies().getName(), "BEAT_WILD_PIXELMON", 1);
        });
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, BattleEndEvent.class, (e) -> {
            if (e.getPlayers().size() >= 2 && e.getCause() == BattleEndCause.NORMAL) {
                e.getPlayers().forEach((player) -> {
                    ServerQuestApi.submitQuest(Bukkit.getPlayer(player.getUUID()), "all", "PLAYER_BATTLE", 1);
                });
            }
        });
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, CaptureEvent.SuccessfulCapture.class, (e) -> {
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.getPlayer().getUUID()), "NORMAL_CAPTURE", e.getPokemon().getSpecies().getName(), 1);
        });
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, CaptureEvent.SuccessfulRaidCapture.class, (e) -> {
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.getPlayer().getUUID()), "RAID_CAPTURE", e.getPokemon().getSpecies().getName(), 1);
        });
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, EndRaidEvent.class, (e) -> {
            if (e.didRaidersWin()) {
                for (RaidData.RaidPlayer player : e.getRaid().getPlayers()) {
                    if (player == null || player.player == null) {
                        continue;
                    }
                    ServerQuestApi.submitQuest(Bukkit.getPlayer(player.player), "RAID_WIN", String.valueOf(e.getRaid().getStars()), e.getRaid().getStars());
                }
            }
        });
        Pixelmon.EVENT_BUS.addListener(EventPriority.NORMAL, true, LevelUpEvent.class, (e) -> {
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.getPlayer().getUUID()), "LEVEL_UP",
                    e.getPokemon().getSpecies().getName(), Math.max(1, e.getAfterLevel() - e.getBeforeLevel()));
        });
    }

    public void register() {
    }
}
