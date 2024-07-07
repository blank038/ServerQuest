package com.blank038.serverquest.impl.pixelmon.v1_12.container;


import com.blank038.serverquest.api.ServerQuestApi;
import com.pixelmonmod.pixelmon.api.events.*;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.raids.EndRaidEvent;
import com.pixelmonmod.pixelmon.battles.raids.RaidData;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleEndCause;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ForgeEventExecutorContainer {

    public static final Consumer<BeatWildPixelmonEvent> BEAT_WILD_PIXELMOn = (event) -> {
        Player player = Bukkit.getPlayer(event.player.getUniqueID());
        ServerQuestApi.submitQuest(player, "BEAT_WILD_PIXELMON", event.wpp.allPokemon[0].pokemon.getSpecies().name(), 1);
    };
    public static final Consumer<BattleEndEvent> BATTLE_END = (e) -> {
        if (e.getPlayers().size() >= 2 && e.cause == EnumBattleEndCause.NORMAL) {
            e.getPlayers().forEach((player) -> {
                ServerQuestApi.submitQuest(Bukkit.getPlayer(player.getUniqueID()), "all", "PLAYER_BATTLE", 1);
            });
        }
    };
    public static final Consumer<CaptureEvent.SuccessfulCapture> NORMAL_CAPTURE = (e) -> {
        ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "NORMAL_CAPTURE", e.getPokemon().getSpecies().name(), 1);
    };
    public static final Consumer<CaptureEvent.SuccessfulRaidCapture> RAID_CAPTURE = (e) -> {
        ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "RAID_CAPTURE", e.getPokemon().getSpecies().name(), 1);
    };
    public static final Consumer<EndRaidEvent> END_RAID = (e) -> {
        if (e.didRaidersWin()) {
            for (RaidData.RaidPlayer player : e.getRaid().getPlayers()) {
                if (player == null || player.player == null) {
                    continue;
                }
                ServerQuestApi.submitQuest(Bukkit.getPlayer(player.player), "RAID_WIN", String.valueOf(e.getRaid().getStars()), e.getRaid().getStars());
            }
        }
    };
    public static final Consumer<LevelUpEvent> LEVEL_UP = (e) -> {
        int amount = Math.max(1, e.newLevel - e.pokemon.getLevel());
        ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "LEVEL_UP", e.pokemon.getSpecies().name(), amount);
    };
    public static final Consumer<EggHatchEvent.Post> EGG_HATCH = (e) -> {
        ServerQuestApi.submitQuest(Bukkit.getPlayer(e.getPlayer().getUniqueID()), "HATCH_EGG", e.getPokemon().getSpecies().name, 1);
    };
    public static final Consumer<BeatTrainerEvent> BEAT_TRAINER = (e) -> {
        ServerQuestApi.submitQuest(Bukkit.getPlayer(e.player.getUniqueID()), "BEAT_TRAINER", e.trainer.getName(), 1);
    };
}