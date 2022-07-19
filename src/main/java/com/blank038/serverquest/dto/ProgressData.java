package com.blank038.serverquest.dto;

import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class ProgressData {
    public static final HashMap<String, ProgressData> PROGRESS_MAP = new HashMap<>();

    private final Map<String, Integer> devoteMap = new HashMap<>();
    private final String progressKey;
    private int now;

    public ProgressData(String progressKey, int count) {
        this.progressKey = progressKey;
        this.now = count;
    }

    public ProgressData(String progressKey, FileConfiguration section) {
        this.progressKey = progressKey;
        this.now = section.getInt("now");
        section.getStringList("devote_list").forEach((s) -> {
            String[] split = s.split("//");
            this.devoteMap.put(split[0], Integer.parseInt(split[1]));
        });
    }

    public void updateData(Map<String, Integer> devoteMap) {
        this.devoteMap.clear();
        this.devoteMap.putAll(devoteMap);
    }

    public void add(Player player, int count) {
        AbstractQuestDaoImpl.getInstance().addQuestProgress(player, this.getProgressKey(), count);
    }

    public boolean hasPlayer(String playerName) {
        return this.devoteMap.containsKey(playerName);
    }

    public void replaceTempDevote(String playerName, int devote) {
        this.devoteMap.put(playerName, devote);
    }

    public int getPlayerDevote(String playerName) {
        return this.devoteMap.getOrDefault(playerName, 0);
    }

    public int getCurrentTotalDevote() {
        return this.now;
    }

    public int getPlayerProgress(String player) {
        return this.devoteMap.getOrDefault(player, 0);
    }

    public void addCurrentTotalDevote(int count) {
        this.now += count;
    }

    public Map<String, Integer> getAllDevote() {
        return new HashMap<>(this.devoteMap);
    }

    public String getProgressKey() {
        return this.progressKey;
    }
}
