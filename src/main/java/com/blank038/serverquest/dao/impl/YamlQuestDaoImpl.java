package com.blank038.serverquest.dao.impl;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import com.blank038.serverquest.cacheframework.cache.ProgressData;
import com.blank038.serverquest.cacheframework.cache.QuestData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Blank038
 */
public class YamlQuestDaoImpl extends AbstractQuestDaoImpl {

    @Override
    public int getQuestProgressByPlayer(Player player, String questId) {
        if (ProgressData.PROGRESS_MAP.containsKey(questId)) {
            return ProgressData.PROGRESS_MAP.get(questId).getPlayerDevote(player.getName());
        }
        return 0;
    }

    @Override
    public int getQuestProgressCacheByPlayer(Player player, String questId) {
        return this.getQuestProgressByPlayer(player, questId);
    }

    @Override
    public int getQuestProgressTotal(String questId) {
        if (ProgressData.PROGRESS_MAP.containsKey(questId)) {
            return ProgressData.PROGRESS_MAP.get(questId).getCurrentTotalDevote();
        }
        return 0;
    }

    @Override
    public void addQuestProgress(Player trigger, String questId, int count) {
        if (ProgressData.PROGRESS_MAP.containsKey(questId)) {
            String name = trigger.getName();
            ProgressData progressData = ProgressData.PROGRESS_MAP.get(questId);
            progressData.replaceTempDevote(name, progressData.getPlayerDevote(name) + count);
            progressData.addCurrentTotalDevote(count);
        } else {
            ProgressData data = new ProgressData(questId, 0);
            data.replaceTempDevote(trigger.getName(), count);
            data.addCurrentTotalDevote(count);
            ProgressData.PROGRESS_MAP.put(questId, data);
        }
    }

    @Override
    public void saveAll() {
        for (Map.Entry<String, ProgressData> entry : ProgressData.PROGRESS_MAP.entrySet()) {
            File file = new File(ServerQuest.getInstance().getDataFolder() + "/progress/", entry.getKey() + ".yml");
            FileConfiguration section = new YamlConfiguration();
            section.set("now", entry.getValue().getCurrentTotalDevote());
            List<String> list = new ArrayList<>();
            entry.getValue().getAllDevote().forEach((key, value) -> list.add(key + "//" + value));
            section.set("devote_list", list);
            try {
                section.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void load() {
        ProgressData.PROGRESS_MAP.clear();
        for (Map.Entry<String, QuestData> entry : DataContainer.QUEST_MAP.entrySet()) {
            File progressFile = new File(ServerQuest.getInstance().getDataFolder() + "/progress/", entry.getKey() + ".yml");
            ProgressData.PROGRESS_MAP.put(entry.getKey(), new ProgressData(entry.getKey(), YamlConfiguration.loadConfiguration(progressFile)));
        }
    }

    @Override
    public void savePlayerData(PlayerData data, boolean locked) {
        File file = new File(ServerQuest.getInstance().getDataFolder() + "/data/", data.getOwner() + ".yml");
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        configuration.set("rewards", data.getRewards());
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlayerData getPlayerData(String name) {
        if (PlayerData.DATA_MAP.containsKey(name)) {
            return PlayerData.DATA_MAP.get(name);
        }
        File file = new File(ServerQuest.getInstance().getDataFolder() + "/data/", name + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        return new PlayerData(name, data);
    }
}
