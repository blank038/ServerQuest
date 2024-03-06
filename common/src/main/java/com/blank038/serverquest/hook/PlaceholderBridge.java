package com.blank038.serverquest.hook;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import com.blank038.serverquest.cacheframework.cache.ProgressData;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 * @since 2021-10-06
 */
public class PlaceholderBridge extends PlaceholderExpansion {
    private static PlaceholderBridge instance;

    private final ServerQuest INSTANCE;

    public PlaceholderBridge() {
        instance = this;
        this.INSTANCE = ServerQuest.getInstance();
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (!PlayerData.DATA_MAP.containsKey(p.getName())) {
            return "0";
        }
        if (params.contains("_")) {
            String[] split = params.split("_");
            StringBuilder sb = new StringBuilder(split[1]);
            if (split.length > 2) {
                for (int i = 2; i < split.length; i++) {
                    sb.append("_").append(split[i]);
                }
            }
            String value = sb.toString();
            if (!ProgressData.PROGRESS_MAP.containsKey(value)) {
                return "0";
            }
            switch (split[0]) {
                case "progress":
                    return String.valueOf(ProgressData.PROGRESS_MAP.get(value).getCurrentTotalDevote());
                case "player":
                    return String.valueOf(AbstractQuestDaoImpl.getInstance().getQuestProgressCacheByPlayer(p, value));
                default:
                    break;
            }
        }
        return "";
    }

    @Override
    public String getIdentifier() {
        return "sq";
    }

    @Override
    public String getAuthor() {
        return "Blank038";
    }

    @Override
    public String getVersion() {
        return this.INSTANCE.getDescription().getVersion();
    }

    public static String setPlaceholders(Player player, String str) {
        if (instance == null) {
            return str;
        }
        return PlaceholderAPI.setPlaceholders(player, str);
    }

    public static PlaceholderBridge getInstance() {
        return instance;
    }
}