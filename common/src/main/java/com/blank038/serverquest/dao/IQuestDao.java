package com.blank038.serverquest.dao;

import com.blank038.serverquest.cacheframework.cache.PlayerData;
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
     * 从缓存中获取玩家的任务进度
     *
     * @param player  目标玩家
     * @param questId 目标任务编号
     * @return 玩家任务个人贡献进度缓存
     */
    int getQuestProgressCacheByPlayer(Player player, String questId);

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
    void saveAll();

    /**
     * 载入数据
     */
    void load();

    /**
     * 存储玩家数据
     *
     * @param data   玩家数据
     * @param locked 是否上锁
     */
    void savePlayerData(PlayerData data, boolean locked);

    /**
     * 获取玩家数据
     *
     * @param name 玩家名
     * @return 玩家数据
     */
    PlayerData getPlayerData(String name);

    /**
     * 对玩家数据上锁
     *
     * @param player 目标玩家
     * @param locked 锁状态
     */
    void setLocked(Player player, boolean locked);

    /**
     * 获取玩家数据是否被上锁
     *
     * @param player 目标玩家
     * @return 锁状态
     */
    boolean isLocked(Player player);
}