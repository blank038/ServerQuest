package com.blank038.serverquest.data;

import com.blank038.serverquest.ServerQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class ProgressData {
    public static final HashMap<String, ProgressData> PROGRESS_MAP = new HashMap<>();

    private final HashMap<String, Integer> DEVOTE_MAP = new HashMap<>();
    private final String PROGRESS_KEY;
    private int now;

    public ProgressData(String progressKey, int count) {
        this.PROGRESS_KEY = progressKey;
        this.now = count;
    }

    public ProgressData(String progressKey, FileConfiguration section) {
        this.PROGRESS_KEY = progressKey;
        this.now = section.getInt("now");
        section.getStringList("devote_list").forEach((s) -> {
            String[] split = s.split("//");
            this.DEVOTE_MAP.put(split[0], Integer.parseInt(split[1]));
        });
    }

    public void add(Player player, int count) {
        String name = player.getName();
        if (this.DEVOTE_MAP.containsKey(name)) {
            this.DEVOTE_MAP.replace(name, this.DEVOTE_MAP.get(name) + count);
        } else {
            this.DEVOTE_MAP.put(name, count);
        }
        this.now += count;
    }

    public int getNow() {
        return this.now;
    }

    public int getPlayerProgress(String player) {
        return this.DEVOTE_MAP.getOrDefault(player, 0);
    }

    public void save() {
        File file = new File(ServerQuest.getInstance().getDataFolder() + "/progress/", this.PROGRESS_KEY + ".yml");
        FileConfiguration section = new YamlConfiguration();
        section.set("now", this.now);
        List<String> list = new ArrayList<>();
        this.DEVOTE_MAP.forEach((key, value) -> list.add(key + "//" + value));
        section.set("devote_list", list);
        try {
            section.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
