package com.blank038.serverquest.gui;

import com.aystudio.core.bukkit.util.common.CommonUtil;
import com.aystudio.core.bukkit.util.inventory.GuiModel;
import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.model.PlayerData;
import com.blank038.serverquest.model.QuestData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blank038
 */
public class SubmitItemGui {

    public static void open(Player player, String questId) {
        if (PlayerData.DATA_MAP.containsKey(player.getName()) && QuestData.QUEST_MAP.containsKey(questId)) {
            ServerQuest.getInstance().saveResource("gui/submitItem.yml", "gui/submitItem.yml", false, (file) -> {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                GuiModel model = new GuiModel(data.getString("title"), data.getInt("size"));
                model.registerListener(ServerQuest.getInstance());
                model.setCloseRemove(true);
                // 开始设置物品
                if (data.contains("items")) {
                    for (String key : data.getConfigurationSection("items").getKeys(false)) {
                        ConfigurationSection section = data.getConfigurationSection("items." + key);
                        ItemStack itemStack = new ItemStack(Material.valueOf(section.getString("type")),
                                section.getInt("amount"), (short) section.getInt("data"));
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name")));
                        List<String> lore = new ArrayList<>();
                        for (String text : section.getStringList("lore")) {
                            lore.add(ChatColor.translateAlternateColorCodes('&', text));
                        }
                        itemMeta.setLore(lore);
                        itemStack.setItemMeta(itemMeta);
                        for (int slot : CommonUtil.formatSlots(section.getString("slot"))) {
                            model.setItem(slot, itemStack);
                        }
                    }
                }
                model.execute((e) -> {
                    if (e.getClickedInventory() != e.getInventory() || e.getSlot() == data.getInt("item-slot")) {
                        return;
                    }
                    e.setCancelled(true);
                });
                model.setCloseInterface((e) -> {
                    ItemStack itemStack = e.getInventory().getItem(data.getInt("item-slot")).clone();
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        return;
                    }
                    Player target = (Player) e.getPlayer();
                    if (QuestData.QUEST_MAP.containsKey(questId)) {
                        String name = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ?
                                itemStack.getItemMeta().getDisplayName() : itemStack.getType().name();
                        if (ServerQuestApi.submitQuest(target, "SUBMIT_ITEM", name, itemStack.getAmount())) {
                            e.getInventory().setItem(data.getInt("item-slot"), null);
                            target.sendMessage(ServerQuest.getString("message.submit-item", true)
                                    .replace("%item%", name).replace("%amount%", String.valueOf(itemStack.getAmount())));
                        } else {
                            e.getInventory().setItem(data.getInt("item-slot"), null);
                            target.getInventory().addItem(itemStack);
                        }
                    } else {
                        e.getInventory().setItem(data.getInt("item-slot"), null);
                        target.getInventory().addItem(itemStack);
                    }
                });
                model.openInventory(player);
            });
        }
    }
}