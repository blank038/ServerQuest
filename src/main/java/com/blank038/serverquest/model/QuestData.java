package com.blank038.serverquest.model;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class QuestData {
    public static final HashMap<String, QuestData> QUEST_MAP = new HashMap<>();

    private final String QUEST_TYPE, SOURCE_KEY;
    private final HashMap<Integer, RewardData> REWARDS = new HashMap<>();

    public QuestData(String sourceKey, ConfigurationSection section) {
        this.QUEST_TYPE = section.getString("type");
        this.SOURCE_KEY = sourceKey;
        section.getConfigurationSection("rewards.").getKeys(false).forEach((key) -> this.REWARDS.put(Integer.parseInt(key),
                new RewardData(section.getConfigurationSection("rewards." + key))));
    }

    public RewardData getReward(int count) {
        return this.REWARDS.getOrDefault(count, null);
    }

    public String getType() {
        return this.QUEST_TYPE;
    }

    public String getKey() {
        return this.SOURCE_KEY;
    }

    public boolean containsReward(int progress) {
        return this.REWARDS.containsKey(progress);
    }
}
