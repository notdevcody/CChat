package me.devcody.cchat.listener;

import me.devcody.cchat.CChat;
import me.devcody.cchat.config.Config;
import me.devcody.cchat.cooldown.Cooldown;
import me.devcody.cchat.cooldown.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatListener implements Listener {
    private final CooldownManager cooldownManager = CChat.getInstance().getCooldownManager();
    private final Config config = CChat.getInstance().getConfig();
    private final static Map<Character, Character> leetMap = new HashMap<>();

    @EventHandler(priority = Event.Priority.Monitor)
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!checkCooldown(player, event, false)) {
            checkBlacklist(player, event.getMessage(), event, false);
        }
    }

    @EventHandler(priority = Event.Priority.Monitor)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!checkCooldown(player, event, true)) {
            checkBlacklist(player, event.getMessage(), event, true);
        }
    }

    private void checkBlacklist(Player player, String msg, Cancellable event, boolean command) {
        if (command && !config.blacklist.commands) return;
        if (!config.blacklist.enabled) return;
        String processedMsg = leetToText(msg.replace(" ", "").toLowerCase());

        for (String entry : this.config.blacklist.banList) {
            if (!this.bypassesBlacklist(player) && blacklistMatch(msg, processedMsg, entry)) {
                event.setCancelled(true);
                dispatchCommand(config.blacklist.banCommand.replace("%player%", player.getName()).replace("%reason%", config.blacklist.banReason));
                CChat.LOGGER.info(player.getName() + " was banned. Message: " + msg + " | Word: " + entry);
                break;
            }
        }

        for (String entry : this.config.blacklist.muteList) {
            if (!this.bypassesBlacklist(player) && blacklistMatch(msg, processedMsg, entry)) {
                event.setCancelled(true);
                dispatchCommand(config.blacklist.muteCommand.replace("%player%", player.getName()).replace("%time%", config.blacklist.muteTime).replace("%reason", config.blacklist.muteReason));
                player.sendMessage(String.format(config.blacklist.muteMessage.replace("%time%", config.blacklist.muteTime), entry));
                CChat.LOGGER.info(player.getName() + " was muted. Message: " + msg + " | Word: " + entry);
                break;
            }
        }

        for (String entry : this.config.blacklist.blockList) {
            if (!this.bypassesBlacklist(player) && blacklistMatch(msg, processedMsg, entry)) {
                event.setCancelled(true);
                player.sendMessage(String.format(config.blacklist.blockMessage, entry));
                CChat.LOGGER.info(player.getName() + " triggered a blacklisted word. Message: " + msg + " | Word: " + entry);
            }
        }
    }

    private boolean blacklistMatch(String msg, String processedMsg, String entry) {
        boolean match;
        if (entry.startsWith("regex-")) {
            match = Pattern.matches(entry.substring("regex-".length()), leetToText(msg).toLowerCase());
        } else if (entry.startsWith("\\l\\")) {
            match = Arrays.stream(msg.toLowerCase().split(" ")).anyMatch((word) -> word.equals(entry.replace("\\l\\","")));
        } else if (entry.startsWith("\\")) {
            match = Arrays.stream(leetToText(msg).toLowerCase().split(" ")).anyMatch((word) -> word.equals(entry.replace("\\", "")));
        } else {
            match = processedMsg.contains(entry.replace(" ", "").toLowerCase());
        }

        return match;
    }

    private boolean checkCooldown(Player player, Cancellable event, boolean command) {
        if (command && !config.cooldown.commands) return false;
        if (!config.cooldown.enabled) return false;
        UUID uuid = player.getUniqueId();

        if (!this.bypassesCooldown(player) && this.cooldownManager.hasCooldown(uuid)) {
            player.sendMessage(String.format(config.cooldown.message, getSecondsRemaining(uuid)));
            event.setCancelled(true);
            return true;
        }

        this.cooldownManager.add(new Cooldown(uuid, this.config.cooldown.length, System.currentTimeMillis()));
        return false;
    }

    private double getSecondsRemaining(UUID id) {
        return this.cooldownManager.getTimeRemaining(id) / 1000.0;
    }

    private boolean bypassesCooldown(Player player) {
        return player.hasPermission("cchat.cooldown.bypass") || player.isOp();
    }

    private boolean bypassesBlacklist(Player player) {
        return player.hasPermission("cchat.blacklist.bypass") || player.isOp();
    }

    private String leetToText(String leetString) {
        String output = leetString;
        for (Character key : leetMap.keySet()) {
            output = output.replace(key, leetMap.get(key));
        }

        return output;
    }

    private void dispatchCommand(String cmd) {
        if (cmd.isEmpty()) {
            return;
        }

        Bukkit.getServer().dispatchCommand(((CraftServer)Bukkit.getServer()).getHandle().server.console, cmd);
    }

    static {
        leetMap.put('@', 'a');
        leetMap.put('4', 'a');
        leetMap.put('8', 'b');
        leetMap.put('3', 'e');
        leetMap.put('1', 'i');
        leetMap.put('5', 's');
        leetMap.put('0', 'o');
    }
}
