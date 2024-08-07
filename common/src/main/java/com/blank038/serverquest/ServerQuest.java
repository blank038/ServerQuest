package com.blank038.serverquest;

import com.aystudio.core.bukkit.AyCore;
import com.aystudio.core.bukkit.plugin.AyPlugin;
import com.aystudio.core.pixelmon.PokemonAPI;
import com.aystudio.core.pixelmon.api.enums.EnumPixelmon;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.command.ServerQuestCommand;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.dao.impl.MysqlQuestDaoImpl;
import com.blank038.serverquest.dao.impl.YamlQuestDaoImpl;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import com.blank038.serverquest.hook.PlaceholderBridge;
import com.blank038.serverquest.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;


/**
 * @author Blank038
 * @since 2021-10-04
 */
public class ServerQuest extends AyPlugin {
    @Getter
    private static ServerQuest instance;

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        this.loadConfig();
        // 注册普通玩家监听器
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        // 判断是否存在 Pixelmon 模组
        PokemonAPI pokemonApi = this.getPokemonApi();
        if (pokemonApi.getEnumPixelmon() == EnumPixelmon.PIXELMON_REFORGED) {
            String version = pokemonApi.getVersion(EnumPixelmon.PIXELMON_REFORGED);
            String listenerClass = null;
            if (version.startsWith("8.4")) {
                listenerClass = "com.blank038.serverquest.impl.pixelmon.v1_12.PixelmonListener";
            } else if (version.startsWith("9.1")) {
                listenerClass = "com.blank038.serverquest.impl.pixelmon.v1_16.PixelmonListener";
            }
            if (listenerClass != null) {
                try {
                    Class<?> classes = Class.forName(listenerClass);
                    Object obj = classes.newInstance();
                    classes.getMethod("register").invoke(obj);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException | InvocationTargetException e) {
                    this.getLogger().log(Level.WARNING, e, () -> "fail to hook Pixelmon");
                }
            }
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
        DataContainer.initialize(this);
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
        } else {
            DataContainer.saveAll();
        }
        AbstractQuestDaoImpl.getInstance().load();
    }

    private PokemonAPI getPokemonApi() {
        try {
            Method method = PokemonAPI.class.getMethod("getInstance");
            return (PokemonAPI) method.invoke(null);
        } catch (Exception ignore) {
            return AyCore.getPokemonAPI();
        }
    }

    public static String getString(String key, boolean... prefix) {
        String message = instance.getConfig().getString(key, "");
        if (prefix.length > 0 && prefix[0]) {
            message = instance.getConfig().getString("message.prefix") + message;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}