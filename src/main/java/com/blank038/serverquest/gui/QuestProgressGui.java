package com.blank038.serverquest.gui;

import com.aystudio.core.bukkit.util.inventory.GuiModel;
import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.dao.AbstractQuestDaoImpl;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import com.blank038.serverquest.cacheframework.cache.ProgressData;
import com.blank038.serverquest.cacheframework.cache.QuestData;
import com.blank038.serverquest.utils.CommonUtil;
import com.blank038.serverquest.utils.ScriptUtil;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        if (!DataContainer.QUEST_MAP.containsKey(questKey) || !ProgressData.PROGRESS_MAP.containsKey(questKey)) {
            player.sendMessage(ServerQuest.getString("message.progress_n_exists", true));
            return;
        }
        ProgressData temProgress = ProgressData.PROGRESS_MAP.get(questKey);
        // 创建 GuiModel 面板
        GuiModel model = new GuiModel(data.getString("title"), data.getInt("size"));
        model.registerListener(ServerQuest.getInstance());
        model.setCloseRemove(true);
        if (data.contains("items")) {
            String meDevote = String.valueOf(AbstractQuestDaoImpl.getInstance().getQuestProgressCacheByPlayer(player, questKey));
            for (String key : data.getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection section = data.getConfigurationSection("items." + key);
                ItemStack itemStack = new ItemStack(Material.valueOf(section.getString("type")),
                        section.getInt("amount"), (short) section.getInt("data"));
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
                List<String> lore = new ArrayList<>();
                for (String line : section.getStringList("lore")) {
                    lore.add(ChatColor.translateAlternateColorCodes('&', line)
                            .replace("%now%", String.valueOf(Math.min(temProgress.getCurrentTotalDevote(), section.getInt("progress"))))
                            .replace("%me%", meDevote));
                }
                lore.replaceAll((s) -> ChatColor.translateAlternateColorCodes('&', s));
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                if (section.contains("progress")) {
                    net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
                    NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
                    assert nbtTagCompound != null;
                    nbtTagCompound.setInt("ProgressReward", section.getInt("progress"));
                    nmsItem.setTag(nbtTagCompound);
                    itemStack = CraftItemStack.asBukkitCopy(nmsItem);
                }
                for (int i : CommonUtil.formatSlots(section.getString("slot"))) {
                    model.setItem(i, itemStack);
                }
            }
        }
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
                    QuestData questData = DataContainer.QUEST_MAP.get(questKey);
                    if (!questData.containsReward(progress) || !ProgressData.PROGRESS_MAP.containsKey(questKey)) {
                        clicker.sendMessage(ServerQuest.getString("message.progress_n_exists", true));
                        return;
                    }
                    ProgressData progressData = ProgressData.PROGRESS_MAP.get(questKey);
                    if (progressData.getCurrentTotalDevote() < progress) {
                        clicker.sendMessage(ServerQuest.getString("message.progress_n_reach", true));
                        return;
                    }
                    PlayerData tempData = PlayerData.DATA_MAP.get(clicker.getName());
                    if (tempData.contains(questData.getKey(), progress)) {
                        clicker.sendMessage(ServerQuest.getString("message.received", true));
                        return;
                    }
                    if (ScriptUtil.detectionCondition(clicker, questData.getReward(progress).getConditions())) {
                        tempData.add(questData.getKey(), progress);
                        // 给予奖励
                        questData.getReward(progress).getCommands().forEach(
                                (command) -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command
                                        .replace("%player%", clicker.getName()))
                        );
                        clicker.sendMessage(ServerQuest.getString("message.gotten", true));
                    } else {
                        clicker.sendMessage(ServerQuest.getString("message.condition_not_met", true));
                    }
                }
            }
        });
        model.openInventory(player);
    }
}
