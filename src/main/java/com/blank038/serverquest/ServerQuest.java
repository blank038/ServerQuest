package com.blank038.serverquest;

import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.commands.ServerQuestCommand;
import com.blank038.serverquest.data.PlayerData;
import com.blank038.serverquest.data.ProgressData;
import com.blank038.serverquest.data.QuestData;
import com.blank038.serverquest.hook.PlaceholderBridge;
import com.blank038.serverquest.listener.PixelmonListener;
import com.blank038.serverquest.listener.PlayerListener;
import com.blank038.serverquest.utils.CommonUtil;
import com.mc9y.blank038api.Blank038API;
import com.mc9y.pokemonapi.api.enums.EnumPixelmon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

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
        // 注册普通玩家监听器
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        // 判断是否存在 Pixelmon 模组
        if (Blank038API.getPokemonAPI().getEnumPixelmon() == EnumPixelmon.PIXELMON_REFORGED) {
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
            PlayerData.DATA_MAP.put(player.getName(), new PlayerData(player.getName()));
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
        // 存储进度
        this.saveAll();
        new File(this.getDataFolder(), "data").mkdir();
        // 判断 Gui 文件是否存在
        File guiFile = new File(this.getDataFolder(), "gui");
        if (!guiFile.exists()) {
            guiFile.mkdir();
            CommonUtil.outputFile(this.getResource("gui/example.yml"), new File(guiFile, "example.yml"));
        }
        // 读取进度
        File progressFile = new File(this.getDataFolder(), "progress");
        if (!progressFile.exists()) {
            progressFile.mkdir();
        }
        // 读取任务
        File questFile = new File(this.getDataFolder(), "quests.yml");
        if (!questFile.exists()) {
            this.saveResource("quests.yml", true);
        }
        FileConfiguration questData = YamlConfiguration.loadConfiguration(questFile);
        // 开始读取数据
        QuestData.QUEST_MAP.clear();
        ProgressData.PROGRESS_MAP.clear();
        for (File file : Objects.requireNonNull(progressFile.listFiles())) {
            String questKey = file.getName().split(".yml")[0];
            ProgressData.PROGRESS_MAP.put(questKey, new ProgressData(questKey, YamlConfiguration.loadConfiguration(file)));
        }
        for (String key : questData.getKeys(false)) {
            QuestData.QUEST_MAP.put(key, new QuestData(key, questData.getConfigurationSection(key)));
            ServerQuestApi.createProgress(key, 0);
        }
    }

    public void saveAll() {
        // 写入配置
        new HashMap<>(ProgressData.PROGRESS_MAP).forEach((k, v) -> v.save());
        // 存储玩家数据
        new HashMap<>(PlayerData.DATA_MAP).forEach((k, v) -> v.save());
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