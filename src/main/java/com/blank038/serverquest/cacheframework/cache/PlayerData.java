package com.blank038.serverquest.cacheframework.cache;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class PlayerData {
    public static final HashMap<String, PlayerData> DATA_MAP = new HashMap<>();

    private final String playerName;
    private final List<String> rewards = new ArrayList<>();
    private boolean newData;

    public PlayerData(String name, FileConfiguration data) {
        this.playerName = name;
        this.rewards.addAll(data.getStringList("rewards"));
        this.newData = data.getBoolean("new");
    }

    public boolean isNewData() {
        return this.newData;
    }

    public void setNewData(boolean newData) {
        this.newData = newData;
    }

    public boolean contains(String key, int count) {
        return this.rewards.contains(key + "-" + count);
    }

    public void add(String key, int count) {
        this.rewards.add(key + "-" + count);
    }

    public List<String> getRewards() {
        return this.rewards;
    }

    public String getOwner() {
        return this.playerName;
    }
}