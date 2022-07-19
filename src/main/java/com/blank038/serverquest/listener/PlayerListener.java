package com.blank038.serverquest.listener;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.dto.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class PlayerListener implements Listener {
    private final ServerQuest INSTANCE;

    public PlayerListener() {
        this.INSTANCE = ServerQuest.getInstance();
        // 提交玩家在线状态
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.INSTANCE, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ServerQuestApi.submitQuest(player, "ONLINE", 1);
            }
        }, 1200L, 1200L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerData.DATA_MAP.put(event.getPlayer().getName(), new PlayerData(event.getPlayer().getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerData data = PlayerData.DATA_MAP.get(event.getPlayer().getName());
        if (data != null) {
            data.save();
        }
    }
}
