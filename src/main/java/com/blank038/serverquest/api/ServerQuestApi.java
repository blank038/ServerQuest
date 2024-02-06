package com.blank038.serverquest.api;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.cacheframework.cache.ProgressData;
import com.blank038.serverquest.cacheframework.cache.QuestData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

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
    public static boolean submitQuest(Player player, String type, String condition, int count) {
        if (player == null) {
            return false;
        }
        boolean result = false;
        Iterator<Map.Entry<String, QuestData>> iterator = DataContainer.QUEST_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, QuestData> entry = iterator.next();
            QuestData questData = entry.getValue();
            if (type.equals(questData.getType()) && (condition.equalsIgnoreCase("all") || questData.getCondition() == null
                    || questData.getCondition().equalsIgnoreCase("all") || questData.getCondition().equals(condition))) {
                Bukkit.getScheduler().runTaskAsynchronously(ServerQuest.getInstance(),
                        () -> AbstractQuestDaoImpl.getInstance().addQuestProgress(player, entry.getKey(), count));
                result = true;
            }
        }
        return result;
    }

    public static boolean submitQuestSingle(Player player, String type, String condition, int count) {
        if (player == null) {
            return false;
        }
        Iterator<Map.Entry<String, QuestData>> iterator = DataContainer.QUEST_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, QuestData> entry = iterator.next();
            QuestData questData = entry.getValue();
            if (type.equals(questData.getType()) && (condition.equalsIgnoreCase("all") || questData.getCondition() == null
                    || questData.getCondition().equalsIgnoreCase("all") || questData.getCondition().equals(condition))) {
                Bukkit.getScheduler().runTaskAsynchronously(ServerQuest.getInstance(),
                        () -> AbstractQuestDaoImpl.getInstance().addQuestProgress(player, entry.getKey(), count));
                return true;
            }
        }
        return false;
    }

    public static boolean submitItem(Player player, String questId, String type, String condition, int count) {
        if (player == null || !DataContainer.QUEST_MAP.containsKey(questId)) {
            return false;
        }
        QuestData questData = DataContainer.QUEST_MAP.get(questId);
        if (type.equals(questData.getType()) && (condition.equalsIgnoreCase("all") || questData.getCondition() == null
                || questData.getCondition().equalsIgnoreCase("all") || questData.getCondition().equals(condition))) {
            Bukkit.getScheduler().runTaskAsynchronously(ServerQuest.getInstance(),
                    () -> AbstractQuestDaoImpl.getInstance().addQuestProgress(player, questId, count));
            return true;
        }
        return false;
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