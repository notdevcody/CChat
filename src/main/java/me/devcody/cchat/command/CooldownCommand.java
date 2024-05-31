package me.devcody.cchat.command;

import me.devcody.cchat.CChat;
import me.devcody.cchat.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CooldownCommand implements CommandExecutor {
    private final Config config = CChat.getInstance().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("cooldown") &&
            !cmd.getName().equalsIgnoreCase("ccd")) {
            return false;
        }

        if (!(sender.hasPermission("cchat.cooldown.manage") || sender.isOp())) {
            sender.sendMessage(ChatColor.RED + "You don't have permissions to do that!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments! " + ChatColor.YELLOW + "Usage: /cooldown <on | off | time>");
            return true;
        }

        String msg;
        switch (args[0].toLowerCase()) {
            case "on":
                config.cooldown.enabled = true;
                msg = ChatColor.GRAY + "Turned " + ChatColor.GREEN + "on " + ChatColor.GRAY + "chat cooldown";
                break;

            case "off":
                config.cooldown.enabled = false;
                msg = ChatColor.GRAY + "Turned " + ChatColor.RED + "off " + ChatColor.GRAY + "chat cooldown";
                break;

            default:
                double secs = Double.parseDouble(args[0]);
                config.cooldown.length = (int) (secs * 1000);
                msg = ChatColor.GRAY + "Cooldown was set to " + ChatColor.YELLOW + secs + ChatColor.GRAY + " seconds";
                break;
        }

        sender.sendMessage(ChatColor.GOLD + "CChat" + ChatColor.DARK_GRAY + " > " + msg);
        return true;
    }
}
