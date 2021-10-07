package com.blank038.serverquest.hook;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.data.ProgressData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 * @since 2021-10-06
 */
public class PlaceholderBridge extends PlaceholderExpansion {
    private final ServerQuest INSTANCE;

    public PlaceholderBridge() {
        this.INSTANCE = ServerQuest.getInstance();
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if (params.contains("_")) {
            String[] split = params.split("_");
            if (split[0].equals("progress")) {
                if (ProgressData.PROGRESS_MAP.containsKey(split[1])) {
                    return String.valueOf(ProgressData.PROGRESS_MAP.get(split[1]).getNow());
                }
                return "0";
            } else if (split[0].equals("player")) {
                if (ProgressData.PROGRESS_MAP.containsKey(split[1])) {
                    return String.valueOf(ProgressData.PROGRESS_MAP.get(split[1]).getPlayerProgress(p.getName()));
                }
                return "0";
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
}