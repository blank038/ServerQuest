package com.blank038.serverquest;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class ServerQuest extends JavaPlugin {
    private static ServerQuest instance;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        this.loadConfig();
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        this.reloadConfig();
    }

    public static ServerQuest getInstance() {
        return instance;
    }
}