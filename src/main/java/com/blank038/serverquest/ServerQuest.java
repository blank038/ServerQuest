package com.blank038.serverquest;

import com.aystudio.core.bukkit.AyCore;
import com.aystudio.core.bukkit.plugin.AyPlugin;
import com.aystudio.core.pixelmon.api.enums.EnumPixelmon;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.command.ServerQuestCommand;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.dao.impl.MysqlQuestDaoImpl;
import com.blank038.serverquest.dao.impl.YamlQuestDaoImpl;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import com.blank038.serverquest.hook.PlaceholderBridge;
import com.blank038.serverquest.listener.PixelmonListener;
import com.blank038.serverquest.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


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
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, DataContainer::saveAll, 1200L, 1200L);
        // 载入在线玩家的数据
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData.DATA_MAP.put(player.getName(), AbstractQuestDaoImpl.getInstance().getPlayerData(player.getName()));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DataContainer.saveAll();
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
        DataContainer.initialize(this);
        DataContainer.saveAll();
        AbstractQuestDaoImpl.getInstance().load();
    }

    public static String getString(String key, boolean... prefix) {
        String message = instance.getConfig().getString(key, "");
        if (prefix.length > 0 && prefix[0]) {
            message = instance.getConfig().getString("message.prefix") + message;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static ServerQuest getInstance() {
        return instance;
    }
}