package com.blank038.serverquest;

import com.aystudio.core.bukkit.AyCore;
import com.aystudio.core.bukkit.plugin.AyPlugin;
import com.aystudio.core.pixelmon.api.enums.EnumPixelmon;
import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.commands.ServerQuestCommand;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.dao.impl.MysqlQuestDaoImpl;
import com.blank038.serverquest.dao.impl.YamlQuestDaoImpl;
import com.blank038.serverquest.model.PlayerData;
import com.blank038.serverquest.model.QuestData;
import com.blank038.serverquest.hook.PlaceholderBridge;
import com.blank038.serverquest.listener.PixelmonListener;
import com.blank038.serverquest.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Blank038
 * @since 2021-10-04
 */
public class ServerQuest extends AyPlugin {
    private static ServerQuest instance;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        this.loadConfig();
        // 注册普通玩家监听器
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        // 判断是否存在 Pixelmon 模组
        if (AyCore.getPokemonAPI().getEnumPixelmon() == EnumPixelmon.PIXELMON_REFORGED) {
            Bukkit.getPluginManager().registerEvents(new PixelmonListener(), this);
        }
        // 判断 PlaceholderAPI 是否挂钩
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderBridge().register();
        }
        // 注册命令
        this.getCommand("sq").setExecutor(new ServerQuestCommand());
        // 启动线程定时存储数据
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveAll, 1200L, 1200L);
        // 载入在线玩家的数据
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData.DATA_MAP.put(player.getName(), AbstractQuestDaoImpl.getInstance().getPlayerData(player.getName()));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.saveAll();
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        this.reloadConfig();
        // 初始化数据控制器
        if (AbstractQuestDaoImpl.getInstance() == null) {
            switch (this.getConfig().getString("data-option.type").toLowerCase()) {
                case "mysql":
                    new MysqlQuestDaoImpl();
                    break;
                case "sqlite":
                    break;
                default:
                    new YamlQuestDaoImpl();
                    break;
            }
        }
        // 读取进度
        File progressFile = new File(this.getDataFolder(), "progress");
        if (!progressFile.exists()) {
            progressFile.mkdir();
        }
        // 存储进度
        this.saveAll();
        new File(this.getDataFolder(), "data").mkdir();
        // 判断 Gui 文件是否存在
        this.saveResource("gui/example.yml", "gui/example.yml");
        // 读取任务
        File questFile = new File(this.getDataFolder(), "quests.yml");
        if (!questFile.exists()) {
            this.saveResource("quests.yml", true);
        }
        FileConfiguration questData = YamlConfiguration.loadConfiguration(questFile);
        // 开始读取数据
        QuestData.QUEST_MAP.clear();
        for (String key : questData.getKeys(false)) {
            QuestData.QUEST_MAP.put(key, new QuestData(key, questData.getConfigurationSection(key)));
            ServerQuestApi.createProgress(key, 0);
        }
        AbstractQuestDaoImpl.getInstance().load();
    }

    public void saveAll() {
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

    public static ServerQuest getInstance() {
        return instance;
    }

    public static String getString(String key, boolean... prefix) {
        return ChatColor.translateAlternateColorCodes('&',
                (prefix.length > 0 && prefix[0] ? instance.getConfig().getString("message.prefix") : "")
                        + instance.getConfig().getString(key, ""));
    }
}