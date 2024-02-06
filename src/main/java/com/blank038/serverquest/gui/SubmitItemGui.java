package com.blank038.serverquest.gui;

import com.aystudio.core.bukkit.util.common.CommonUtil;
import com.aystudio.core.bukkit.util.inventory.GuiModel;
import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.api.ServerQuestApi;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.cacheframework.cache.PlayerData;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
        if (PlayerData.DATA_MAP.containsKey(player.getName()) && DataContainer.QUEST_MAP.containsKey(questId)) {
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
                        if (section.contains("action")) {
                            net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
                            NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
                            assert nbtTagCompound != null;
                            nbtTagCompound.setString("SubmitItemAction", section.getString("action"));
                            nmsItem.setTag(nbtTagCompound);
                            itemStack = CraftItemStack.asBukkitCopy(nmsItem);
                        }
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
                    ItemStack itemStack = e.getCurrentItem();
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        return;
                    }
                    net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
                    NBTTagCompound nbtTagCompound = nmsItem.getTag();
                    if (nbtTagCompound != null && nbtTagCompound.hasKey("SubmitItemAction")) {
                        String key = nbtTagCompound.getString("SubmitItemAction");
                        if ("submit".equals(key)) {
                            Player target = (Player) e.getWhoClicked();
                            if (DataContainer.ACTION_COOLDOWN.getOrDefault(target.getName(), 0L) >= System.currentTimeMillis()) {
                                target.sendMessage(ServerQuest.getString("message.cooldown", true));
                                return;
                            }
                            int cooldown = ServerQuest.getInstance().getConfig().getInt("cooldown") * 1000;
                            DataContainer.ACTION_COOLDOWN.put(target.getName(), System.currentTimeMillis() + cooldown);
                            // 执行物品校验逻辑
                            ItemStack tarItem = e.getInventory().getItem(data.getInt("item-slot"));
                            if (tarItem == null || tarItem.getType() == Material.AIR) {
                                return;
                            }
                            tarItem = tarItem.clone();
                            if (DataContainer.QUEST_MAP.containsKey(questId)) {
                                String name = tarItem.hasItemMeta() && tarItem.getItemMeta().hasDisplayName() ?
                                        tarItem.getItemMeta().getDisplayName() : tarItem.getType().name();
                                // 扣除物品
                                e.getInventory().setItem(data.getInt("item-slot"), null);
                                // 提交数据
                                if (ServerQuestApi.submitItem(target, questId, "SUBMIT_ITEM", name, tarItem.getAmount())) {
                                    target.sendMessage(ServerQuest.getString("message.submit-item", true)
                                            .replace("%item%", name)
                                            .replace("%amount%", String.valueOf(tarItem.getAmount())));
                                } else {
                                    target.getInventory().addItem(tarItem);
                                }
                            } else {
                                e.getInventory().setItem(data.getInt("item-slot"), null);
                                target.getInventory().addItem(tarItem);
                            }
                        }
                    }
                });
                model.setCloseInterface((e) -> {
                    ItemStack tarItem = e.getInventory().getItem(data.getInt("item-slot"));
                    if (tarItem == null || tarItem.getType() == Material.AIR) {
                        return;
                    }
                    e.getPlayer().getInventory().addItem(tarItem);
                });
                model.openInventory(player);
            });
        }
    }
}