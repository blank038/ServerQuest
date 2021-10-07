package com.blank038.serverquest.api;

import com.blank038.serverquest.data.ProgressData;
import com.blank038.serverquest.data.QuestData;
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
                if (ProgressData.PROGRESS_MAP.containsKey(k)) {
                    ProgressData.PROGRESS_MAP.get(k).add(player, count);
                } else {
                    ProgressData data = new ProgressData(k, 0);
                    data.add(player, count);
                    ProgressData.PROGRESS_MAP.put(k, data);
                }
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