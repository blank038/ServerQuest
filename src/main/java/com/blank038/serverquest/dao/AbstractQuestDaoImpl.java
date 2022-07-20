package com.blank038.serverquest.dao;

import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public abstract class AbstractQuestDaoImpl implements IQuestDao {
    private static AbstractQuestDaoImpl instance;

    public AbstractQuestDaoImpl() {
        instance = this;
    }

    public static AbstractQuestDaoImpl getInstance() {
        return instance;
    }

    @Override
    public void load() {
    }

    @Override
    public boolean isLocked(Player player) {
        return false;
    }

    @Override
    public void setLocked(Player player, boolean locked) {
    }
}
