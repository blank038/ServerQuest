package com.blank038.serverquest.cacheframework.cache;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class QuestData {
    private final String questType, sourceKey, condition;
    private final Map<Integer, RewardData> REWARDS = new HashMap<>();

    public QuestData(String sourceKey, ConfigurationSection section) {
        this.questType = section.getString("type");
        this.sourceKey = sourceKey;
        this.condition = ChatColor.translateAlternateColorCodes('&', section.getString("check", "all"));
        section.getConfigurationSection("rewards.").getKeys(false).forEach((key) -> this.REWARDS.put(Integer.parseInt(key),
                new RewardData(section.getConfigurationSection("rewards." + key))));
    }

    public RewardData getReward(int count) {
        return this.REWARDS.getOrDefault(count, null);
    }

    public String getType() {
        return this.questType;
    }

    public String getKey() {
        return this.sourceKey;
    }

    public String getCondition() {
        return this.condition;
    }

    public boolean containsReward(int progress) {
        return this.REWARDS.containsKey(progress);
    }
}
