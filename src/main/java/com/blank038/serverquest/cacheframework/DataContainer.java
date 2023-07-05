package com.blank038.serverquest.cacheframework;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.cacheframework.cache.DataCache;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import com.blank038.serverquest.cacheframework.cache.QuestData;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Blank038
 */
public class DataContainer {
    public static final Map<String, DataCache> CACHE_MAP = new HashMap<>();
    public static final HashMap<String, QuestData> QUEST_MAP = new HashMap<>();
    public static final Map<String, Long> ACTION_COOLDOWN = new HashMap<>();


    public static void initialize(ServerQuest serverQuest) {
        File progressFile = new File(serverQuest.getDataFolder(), "progress");
        if (!progressFile.exists()) {
            progressFile.mkdir();
        }
        new File(serverQuest.getDataFolder(), "data").mkdir();
        // 判断 Gui 文件是否存在
        File guiFolder = new File(serverQuest.getDataFolder(), "gui");
        if (!guiFolder.exists()) {
            serverQuest.saveResource("gui/example.yml", "gui/example.yml");
        }
        // 读取任务
        serverQuest.saveResource("quests.yml", "quests.yml", false, (file) -> {
            FileConfiguration questData = YamlConfiguration.loadConfiguration(file);
            DataContainer.QUEST_MAP.clear();
            for (String key : questData.getKeys(false)) {
                DataContainer.QUEST_MAP.put(key, new QuestData(key, questData.getConfigurationSection(key)));
                ServerQuestApi.createProgress(key, 0);
            }
        });
    }

    public static void saveAll() {
        // 写入配置
        if (AbstractQuestDaoImpl.getInstance() != null) {
            AbstractQuestDaoImpl.getInstance().saveAll();
        }
        // 存储玩家数据
        Iterator<Map.Entry<String, PlayerData>> iterator = PlayerData.DATA_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            AbstractQuestDaoImpl.getInstance().savePlayerData(iterator.next().getValue(), true);
        }
    }
}
