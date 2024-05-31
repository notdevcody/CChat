package me.devcody.cchat.command;

import me.devcody.cchat.CChat;
import me.devcody.cchat.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ManageCommand implements CommandExecutor {
    private final Config config = CChat.getInstance().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("cchat")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "CChat " + ChatColor.GRAY + "by " + ChatColor.YELLOW + "devcody");
            sender.sendMessage(ChatColor.GRAY + "Version " + ChatColor.GOLD + CChat.VERSION);
            return true;
        }

        if (!args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.RED + "Invalid argument! " + ChatColor.YELLOW + "Usage: /cchat <reload>");
            return true;
        }

        if (!(sender.hasPermission("cchat.command.reload") || sender.isOp())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            return true;
        }

        config.reload();
        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded configuration!");

        return true;
    }
}
