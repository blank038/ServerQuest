package com.blank038.serverquest.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class RewardData {
    private final List<String> REWARD_COMMANDS = new ArrayList<>(), CONDITIONS = new ArrayList<>();

    public RewardData(ConfigurationSection section) {
        this.REWARD_COMMANDS.addAll(section.getStringList("commands"));
        this.CONDITIONS.addAll(section.getStringList("conditions"));
    }

    public List<String> getCommands() {
        return this.REWARD_COMMANDS;
    }

    public List<String> getConditions() {
        return this.CONDITIONS;
    }
}