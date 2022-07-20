package com.blank038.serverquest.listener;

import com.aystudio.core.bukkit.thread.BlankThread;
import com.aystudio.core.bukkit.thread.ThreadProcessor;
import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.model.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class PlayerListener implements Listener {
    private final ServerQuest instance = ServerQuest.getInstance();
    ;

    public PlayerListener() {
        // 提交玩家在线状态
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.instance, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ServerQuestApi.submitQuest(player, "ONLINE", "all", 1);
            }
        }, 1200L, 1200L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.instance.getConfig().getBoolean("data-option.pull-notify")) {
            event.getPlayer().sendMessage(ServerQuest.getString("message.pull-start", true));
        }
        // 创建线程
        ThreadProcessor.crateTask(this.instance, new BlankThread(10) {
            private int count;

            @Override
            public void run() {
                Player player = event.getPlayer();
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                if (!AbstractQuestDaoImpl.getInstance().isLocked(player)) {
                    PlayerListener.this.loadData(player);
                    this.cancel();
                } else {
                    count++;
                    if (count > PlayerListener.this.instance.getConfig().getInt("data-option.time-out")) {
                        PlayerListener.this.loadData(player);
                        this.cancel();
                    }
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerData data = PlayerData.DATA_MAP.remove(event.getPlayer().getName());
        if (data != null) {
            Bukkit.getScheduler().runTaskAsynchronously(this.instance, () -> AbstractQuestDaoImpl.getInstance().savePlayerData(data, false));
        }
    }

    @EventHandler
    public void onPlayerKillEntity(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null && PlayerData.DATA_MAP.containsKey(event.getEntity().getKiller().getName())) {
            String entityName = event.getEntity().getCustomName() != null ? event.getEntity().getCustomName() : event.getEntity().getType().name();
            ServerQuestApi.submitQuest(event.getEntity().getKiller(), "KILL_ENTITY", entityName, 1);
        }
    }

    private void loadData(Player player) {
        PlayerData playerData = AbstractQuestDaoImpl.getInstance().getPlayerData(player.getName());
        PlayerData.DATA_MAP.put(player.getName(), playerData);
        AbstractQuestDaoImpl.getInstance().setLocked(player, true);
        if (instance.getConfig().getBoolean("data-option.pull-notify")) {
            player.sendMessage(ServerQuest.getString("message.pull-end", true));
        }
    }
}
