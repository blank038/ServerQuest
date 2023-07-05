package com.blank038.serverquest.listener;

import com.aystudio.core.forge.event.ForgeEvent;
import com.blank038.serverquest.api.ServerQuestApi;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.LevelUpEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.raids.EndRaidEvent;
import com.pixelmonmod.pixelmon.battles.raids.RaidData;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class PixelmonListener implements Listener {

    @EventHandler
    public void onForge(ForgeEvent event) {
        if (event.getForgeEvent() instanceof BeatWildPixelmonEvent) {
            BeatWildPixelmonEvent e = (BeatWildPixelmonEvent) event.getForgeEvent();
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), e.wpp.allPokemon[0].pokemon.getSpecies().name(), "BEAT_WILD_PIXELMON", 1);
        } else if (event.getForgeEvent() instanceof BattleEndEvent) {
            BattleEndEvent e = (BattleEndEvent) event.getForgeEvent();
            if (e.getPlayers().size() >= 2 && e.cause == EnumBattleEndCause.NORMAL) {
                e.getPlayers().forEach((player) -> {
                    ServerQuestApi.submitQuest(Bukkit.getPlayer(player.getUniqueID()), "all", "PLAYER_BATTLE", 1);
                });
            }
        } else if (event.getForgeEvent() instanceof CaptureEvent.SuccessfulCapture) {
            CaptureEvent.SuccessfulCapture e = (CaptureEvent.SuccessfulCapture) event.getForgeEvent();
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "NORMAL_CAPTURE", e.getPokemon().getSpecies().name(), 1);
        } else if (event.getForgeEvent() instanceof CaptureEvent.SuccessfulRaidCapture) {
            CaptureEvent.SuccessfulRaidCapture e = (CaptureEvent.SuccessfulRaidCapture) event.getForgeEvent();
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "RAID_CAPTURE", e.getPokemon().getSpecies().name(), 1);
        } else if (event.getForgeEvent() instanceof EndRaidEvent) {
            EndRaidEvent e = (EndRaidEvent) event.getForgeEvent();
            if (e.didRaidersWin()) {
                for (RaidData.RaidPlayer player : e.getRaid().getPlayers()) {
                    if (player == null || player.player == null) {
                        continue;
                    }
                    ServerQuestApi.submitQuest(Bukkit.getPlayer(player.player), "RAID_WIN", String.valueOf(e.getRaid().getStars()), e.getRaid().getStars());
                }
            }
        } else if (event.getForgeEvent() instanceof LevelUpEvent) {
            LevelUpEvent e = (LevelUpEvent) event.getForgeEvent();
            ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "LEVEL_UP", e.pokemon.getSpecies().name(), Math.max(1, e.newLevel - e.pokemon.getLevel()));
        }
    }
}
