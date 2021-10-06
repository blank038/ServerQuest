package com.blank038.serverquest.gui;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.data.PlayerData;
import com.blank038.serverquest.data.ProgressData;
import com.blank038.serverquest.data.QuestData;
import com.mc9y.blank038api.util.inventory.GuiModel;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

/**
 * @author Blank038
 * @since 2021-10-05
 */
public class QuestProgressGui {

    public static void open(Player player, String fileName) {
        if (!PlayerData.DATA_MAP.containsKey(player.getName())) {
            player.sendMessage(ServerQuest.getString("message.wrong-data", true));
            return;
        }
        // 读取文件
        File file = new File(ServerQuest.getInstance().getDataFolder() + "/gui/", fileName + ".yml");
        if (!file.exists()) {
            player.sendMessage(ServerQuest.getString("message.progress_n_exists", true));
            return;
        }
        FileConfiguration data = YamlConfiguration.loadConfiguration(file);
        String questKey = data.getString("quest_key");
        if (!QuestData.QUEST_MAP.containsKey(questKey)) {
            player.sendMessage(ServerQuest.getString("message.progress_n_exists", true));
            return;
        }
        // 创建 GuiModel 面板
        GuiModel model = new GuiModel(data.getString("title"), data.getInt("size"));
        model.registerListener(ServerQuest.getInstance());
        model.setCloseRemove(true);
        model.execute((e) -> {
            e.setCancelled(true);
            if (e.getClickedInventory() == e.getInventory()) {
                ItemStack itemStack = e.getCurrentItem();
                if (itemStack == null || itemStack.getType() == Material.AIR) {
                    return;
                }
                net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
                NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
                if (nbtTagCompound.hasKey("ProgressReward")) {
                    Player clicker = (Player) e.getWhoClicked();
                    int progress = nbtTagCompound.getInt("ProgressReward");
                    QuestData questData = QuestData.QUEST_MAP.get(questKey);
                    if (!questData.containsReward(progress) || !ProgressData.PROGRESS_MAP.containsKey(questKey)) {
                        clicker.sendMessage(ServerQuest.getString("message.progress_n_exists", true));
                        return;
                    }
                    ProgressData progressData = ProgressData.PROGRESS_MAP.get(questKey);
                    if (progressData.getNow() < progress) {
                        clicker.sendMessage(ServerQuest.getString("message.", true));
                        return;
                    }
                    PlayerData tempData = PlayerData.DATA_MAP.get(clicker.getName());
                    if (tempData.contains(questData.getKey(), progress)) {
                        clicker.sendMessage(ServerQuest.getString("message.received", true));
                        return;
                    }
                    tempData.add(questData.getKey(), progress);
                    // 给予奖励
                    questData.getReward(progress).getCommands().forEach(
                            (command) -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command
                                    .replace("%player%", clicker.getName()))
                    );
                    clicker.sendMessage(ServerQuest.getString("message.gotten", true));
                }
            }
        });
        model.openInventory(player);
    }
}
