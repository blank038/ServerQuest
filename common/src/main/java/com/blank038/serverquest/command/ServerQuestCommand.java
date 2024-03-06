package com.blank038.serverquest.command;

import com.blank038.serverquest.ServerQuest;
import com.blank038.serverquest.cacheframework.DataContainer;
import com.blank038.serverquest.gui.QuestProgressGui;
import com.blank038.serverquest.gui.SubmitItemGui;
import com.blank038.serverquest.cacheframework.cache.QuestData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Blank038
 * @since 2021-10-06
 */
public class ServerQuestCommand implements CommandExecutor {
    private final ServerQuest INSTANCE;

    public ServerQuestCommand() {
        this.INSTANCE = ServerQuest.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            this.sendHelp(commandSender, s);
        } else {
            switch (strings[0]) {
                case "open":
                    this.open(commandSender, strings);
                    break;
                case "reload":
                    this.reload(commandSender);
                    break;
                case "submit":
                    this.submitItem(commandSender, strings);
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    private void sendHelp(CommandSender sender, String label) {
        for (String i : INSTANCE.getConfig().getStringList("message.help."
                + (sender.hasPermission("serverquest.admin") ? "admin" : "default"))) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', i)
                    .replace("%c", label));
        }
    }

    private void open(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1 || !DataContainer.QUEST_MAP.containsKey(args[1])) {
                sender.sendMessage(ServerQuest.getString("message.wrong_progress_key", true));
                return;
            }
            QuestProgressGui.open((Player) sender, args[1]);
        }
    }

    private void submitItem(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1 || !DataContainer.QUEST_MAP.containsKey(args[1])) {
                sender.sendMessage(ServerQuest.getString("message.wrong_progress_key", true));
                return;
            }
            SubmitItemGui.open((Player) sender, args[1]);
        }
    }

    private void reload(CommandSender sender) {
        if (sender.hasPermission("serverquest.admin")) {
            this.INSTANCE.loadConfig();
            sender.sendMessage(ServerQuest.getString("message.reload", true));
        }
    }
}
