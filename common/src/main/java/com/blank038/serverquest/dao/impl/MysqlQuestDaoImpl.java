package com.blank038.serverquest.dao.impl;

import com.aystudio.core.bukkit.util.mysql.MySqlStorageHandler;
import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.cacheframework.cache.DataCache;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import com.blank038.serverquest.cacheframework.cache.ProgressData;
import com.blank038.serverquest.cacheframework.cache.QuestData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Blank038
 */
public class MysqlQuestDaoImpl extends AbstractQuestDaoImpl {
    private final MySqlStorageHandler storageHandler;

    public MysqlQuestDaoImpl() {
        // 初始化数据连接接口
        String[] sqlArray = {
                "CREATE TABLE IF NOT EXISTS server_quest_records (" +
                        "id INT UNSIGNED AUTO_INCREMENT, " +
                        "player CHAR(36) NOT NULL, " +
                        "quest_id CHAR(40) NOT NULL, " +
                        "value INT NOT NULL, " +
                        "PRIMARY KEY ( id ))",
                "CREATE TABLE IF NOT EXISTS server_quest_player_data (user VARCHAR(30) NOT NULL, data TEXT, locked INT, PRIMARY KEY ( user ))"
        };
        this.storageHandler = new MySqlStorageHandler(ServerQuest.getInstance(), ServerQuest.getInstance().getConfig().getString("data-option.url"),
                ServerQuest.getInstance().getConfig().getString("data-option.user"), ServerQuest.getInstance().getConfig().getString("data-option.password"), sqlArray);
        this.storageHandler.setCheckConnection(true);
        this.storageHandler.setReconnectionQueryTable("server_quest_records");
        // 间隔读取数据
        Bukkit.getScheduler().runTaskTimerAsynchronously(ServerQuest.getInstance(), this::load, 200L, 200L);
    }

    @Override
    public int getQuestProgressByPlayer(Player player, String questId) {
        if (player == null || questId == null) {
            return 0;
        }
        AtomicInteger integer = new AtomicInteger(0);
        this.storageHandler.connect((statement) -> {
            try {
                statement.setString(1, player.getName());
                statement.setString(2, questId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    integer.set(resultSet.getInt(1));
                }
            } catch (SQLException e) {
                ServerQuest.getInstance().getLogger().info(e.toString());
            }
        }, "SELECT value FROM server_quest_records WHERE player=? and quest_id=?");
        return integer.get();
    }

    @Override
    public int getQuestProgressCacheByPlayer(Player player, String questId) {
        if (DataContainer.CACHE_MAP.containsKey(questId)) {
            return DataContainer.CACHE_MAP.get(questId).getPlayerDevote(player.getName());
        }
        return 0;
    }

    @Override
    public int getQuestProgressTotal(String questId) {
        if (questId == null) {
            return 0;
        }
        AtomicInteger integer = new AtomicInteger(0);
        this.storageHandler.connect((statement) -> {
            try {
                statement.setString(1, questId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    integer.set(resultSet.getInt(1));
                }
            } catch (SQLException e) {
                ServerQuest.getInstance().getLogger().info(e.toString());
            }
        }, "SELECT sum(value) FROM server_quest_records WHERE quest_id=?");
        return integer.get();
    }

    @Override
    public void addQuestProgress(Player trigger, String questId, int count) {
        if (trigger == null || questId == null || count <= 0) {
            return;
        }
        AtomicInteger atomicInteger = new AtomicInteger(0);
        this.storageHandler.connect((statement) -> {
            try {
                statement.setInt(1, count);
                statement.setString(2, trigger.getName());
                statement.setString(3, questId);
                atomicInteger.set(statement.executeUpdate());
            } catch (SQLException e) {
                ServerQuest.getInstance().getLogger().info(e.toString());
            }
        }, "UPDATE server_quest_records SET value=value + ? WHERE player=? and quest_id=?");
        if (atomicInteger.get() == 0) {
            this.storageHandler.connect((statement) -> {
                try {
                    statement.setString(1, trigger.getName());
                    statement.setString(2, questId);
                    statement.setInt(3, count);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    ServerQuest.getInstance().getLogger().info(e.toString());
                }
            }, "INSERT INTO server_quest_records(player,quest_id,value) VALUES(?,?,?)");
        }
    }

    @Override
    public void saveAll() {
        // 这里不需要做什么
    }

    @Override
    public void load() {
        ProgressData.PROGRESS_MAP.clear();
        DataContainer.CACHE_MAP.clear();
        // 读取全服进度
        for (Map.Entry<String, QuestData> entry : DataContainer.QUEST_MAP.entrySet()) {
            ServerQuestApi.createProgress(entry.getKey(), this.getQuestProgressTotal(entry.getKey()));
            // 载入缓存, 仅在线玩家的缓存
            DataContainer.CACHE_MAP.put(entry.getKey(), new DataCache());
            Map<String, Integer> values = new HashMap<>();
            Bukkit.getOnlinePlayers().forEach((player) -> values.put(player.getName(), this.getQuestProgressByPlayer(player, entry.getKey())));
            DataContainer.CACHE_MAP.get(entry.getKey()).update(values);
        }
    }

    @Override
    public void savePlayerData(PlayerData data, boolean locked) {
        FileConfiguration object = new YamlConfiguration();
        object.set("rewards", data.getRewards());
        String text = new String(Base64.getEncoder().encode(object.saveToString().getBytes(StandardCharsets.UTF_8)));
        String sql = String.format(data.isNewData() ? "INSERT INTO server_quest_player_data (user,data,locked) VALUES (?,?,%s)"
                : "UPDATE server_quest_player_data SET data=?, locked='%s' WHERE user=?", locked ? 0 : 1);
        this.storageHandler.connect((statement) -> {
            try {
                if (data.isNewData()) {
                    statement.setString(1, data.getOwner());
                    statement.setString(2, text);
                    data.setNewData(false);
                } else {
                    statement.setString(1, text);
                    statement.setString(2, data.getOwner());
                }
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, sql);
    }

    @Override
    public PlayerData getPlayerData(String name) {
        if (PlayerData.DATA_MAP.containsKey(name)) {
            return PlayerData.DATA_MAP.get(name);
        }
        AtomicReference<FileConfiguration> reference = new AtomicReference<>();
        this.storageHandler.connect((statement) -> {
            try {
                statement.setString(1, name);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String data = resultSet.getString("data");
                    if (data != null) {
                        FileConfiguration configuration = new YamlConfiguration();
                        configuration.loadFromString(new String(Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8))));
                        reference.set(configuration);
                    }
                }
                resultSet.close();
            } catch (SQLException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }, "SELECT data FROM server_quest_player_data WHERE user=?");
        if (reference.get() == null) {
            FileConfiguration configuration = new YamlConfiguration();
            configuration.set("new", true);
            reference.set(configuration);
        }
        return new PlayerData(name, reference.get());
    }

    @Override
    public boolean isLocked(Player player) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        this.storageHandler.connect((statement) -> {
            try {
                statement.setString(1, player.getName());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int locked = resultSet.getInt("locked");
                    if (locked == 0) {
                        result.set(true);
                        break;
                    }
                }
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, "SELECT locked FROM server_quest_player_data WHERE user=?");
        return result.get();
    }

    @Override
    public void setLocked(Player player, boolean locked) {
        this.storageHandler.connect((statement) -> {
            try {
                statement.setInt(1, (locked ? 0 : 1));
                statement.setString(2, player.getName());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, "UPDATE server_quest_player_data SET locked=? WHERE user=?");
    }
}
