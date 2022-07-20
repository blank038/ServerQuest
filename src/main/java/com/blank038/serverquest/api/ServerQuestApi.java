package com.blank038.serverquest.api;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.model.ProgressData;
import com.blank038.serverquest.model.QuestData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class ServerQuestApi {

    /**
     * 提交任务进度
     *
     * @param player 触发玩家
     * @param type   任务类型
     * @param count  任务进度
     */
    public static void submitQuest(Player player, String type, int count) {
        if (player == null) {
            return;
        }
        QuestData.QUEST_MAP.forEach((k, v) -> {
            if (type.equals(v.getType())) {
                Bukkit.getScheduler().runTaskAsynchronously(ServerQuest.getInstance(),
                        () -> AbstractQuestDaoImpl.getInstance().addQuestProgress(player, k, count));
            }
        });
    }

    /**
     * 创建一个任务进度
     *
     * @param questId  任务编号
     * @param progress 任务进度
     */
    public static void createProgress(String questId, int progress) {
        if (ProgressData.PROGRESS_MAP.containsKey(questId)) {
            return;
        }
        ProgressData.PROGRESS_MAP.put(questId, new ProgressData(questId, progress));
    }
}