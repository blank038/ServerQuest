package com.blank038.serverquest.dao.impl;

import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public class MysqlQuestDaoImpl extends AbstractQuestDaoImpl {

    @Override
    public int getQuestProgressByPlayer(Player player, String questId) {
        return 0;
    }

    @Override
    public int getQuestProgressTotal(String questId) {
        return 0;
    }

    @Override
    public void addQuestProgress(Player trigger, String questId, int count) {

    }

    @Override
    public void save() {

    }
}
