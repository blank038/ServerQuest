package com.blank038.serverquest.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class ProgressData {
    private final HashMap<String, Integer> DEVOTE_MAP = new HashMap<>();
    private int now;

    public ProgressData(ConfigurationSection section) {
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

    public ConfigurationSection toConfiguration() {
        ConfigurationSection section = new YamlConfiguration();
        section.set("now", this.now);
        List<String> list = new ArrayList<>();
        this.DEVOTE_MAP.forEach((key, value) -> list.add(key + "//" + value));
        section.set("devote_list", list);
        return section;
    }
}
