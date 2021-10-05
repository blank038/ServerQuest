package com.blank038.serverquest.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class QuestData {
    private final HashMap<Integer, List<String>> REWARDS = new HashMap<>();

    public QuestData(ConfigurationSection section) {
        section.getKeys(false).forEach((key) -> this.REWARDS.put(Integer.parseInt(key), section.getStringList(key)));
    }

    public List<String> getRewards(int count) {
        return this.REWARDS.getOrDefault(count, new ArrayList<>());
    }
}
