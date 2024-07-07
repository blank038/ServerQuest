package com.blank038.serverquest.cacheframework.cache;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-05
 */
@Getter
public class RewardData {
    private final List<String> rewardCommands = new ArrayList<>(),
            conditions = new ArrayList<>();

    public RewardData(ConfigurationSection section) {
        this.rewardCommands.addAll(section.getStringList("commands"));
        this.conditions.addAll(section.getStringList("conditions"));
    }

    public List<String> getCommands() {
        return this.rewardCommands;
    }
}