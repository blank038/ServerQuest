package com.blank038.serverquest.dao;

import org.bukkit.entity.Player;

/**
 * @author Blank038
 */
public interface IQuestDao {

    /**
     * 获取玩家的任务进度
     *
     * @param player  目标玩家
     * @param questId 目标任务编号
     * @return 玩家任务个人贡献进度
     */
    int getQuestProgressByPlayer(Player player, String questId);

    /**
     * 获得任务全服进度
     *
     * @param questId 任务编号
     * @return 任务全服进度总数
     */
    int getQuestProgressTotal(String questId);

    /**
     * 增加任务进度
     *
     * @param trigger 触发玩家
     * @param questId 任务编号
     * @param count   数量
     */
    void addQuestProgress(Player trigger, String questId, int count);

    /**
     * 存储全服进度数据, 仅在 Yaml 生效
     */
    void save();
}