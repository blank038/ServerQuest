package com.blank038.serverquest.dao.impl;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.dto.ProgressData;
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
            return ProgressData.PROGRESS_MAP.get(questId).getPlayerProgress(player.getName());
        }
        return 0;
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
        }
    }

    @Override
    public void save() {
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
}
