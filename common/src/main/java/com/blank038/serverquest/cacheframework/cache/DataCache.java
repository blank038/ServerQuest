package com.blank038.serverquest.cacheframework.cache;

import com.blank038.serverquest.cacheframework.DataContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Blank038
 */
public class DataCache {

    private final Map<String, Integer> devote = new HashMap<>();

    public void update(Map<String, Integer> map) {
        this.devote.clear();
        this.devote.putAll(map);
    }

    public int getPlayerDevote(String playerName) {
        return this.devote.getOrDefault(playerName, 0);
    }

    public static int getPlayerDevote(String questId, String playerName) {
        if (DataContainer.CACHE_MAP.containsKey(questId)) {
            return DataContainer.CACHE_MAP.get(questId).getPlayerDevote(playerName);
        }
        return 0;
    }
}
