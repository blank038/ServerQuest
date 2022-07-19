package com.blank038.serverquest.dto;

import com.blank038.serverquest.ServerQuest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class PlayerData {
    public static final HashMap<String, PlayerData> DATA_MAP = new HashMap<>();

    private final String PLAYER_NAME;
    private final List<String> REWARDS = new ArrayList<>();

    public PlayerData(String name) {
        this.PLAYER_NAME = name;
        File file = new File(ServerQuest.getInstance().getDataFolder() + "/data/", name + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.REWARDS.addAll(data.getStringList("rewards"));
    }

    public boolean contains(String key, int count) {
        return this.REWARDS.contains(key + "-" + count);
    }

    public void add(String key, int count) {
        this.REWARDS.add(key + "-" + count);
    }

    public void save() {
        File file = new File(ServerQuest.getInstance().getDataFolder() + "/data/", this.PLAYER_NAME + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        data.set("rewards", this.REWARDS);
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}