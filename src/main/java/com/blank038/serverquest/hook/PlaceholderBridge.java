package com.blank038.serverquest.hook;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.data.ProgressData;
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
                    return String.valueOf(ProgressData.PROGRESS_MAP.get(value).getNow());
                case "player":
                    return String.valueOf(ProgressData.PROGRESS_MAP.get(value).getPlayerProgress(p.getName()));
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

    public String setPlaceholders(Player player, String str) {
        return PlaceholderAPI.setPlaceholders(player, str);
    }

    public static PlaceholderBridge getInstance() {
        return instance;
    }
}