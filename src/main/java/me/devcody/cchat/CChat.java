package me.devcody.cchat;

import me.devcody.cchat.command.CooldownCommand;
import me.devcody.cchat.command.ManageCommand;
import me.devcody.cchat.config.Config;
import me.devcody.cchat.cooldown.CooldownManager;
import me.devcody.cchat.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class CChat extends JavaPlugin {
    public static final String VERSION = "1.0";

    private static CChat instance;
    private Config config;
    private CooldownManager cooldownManager;
    public static final Logger LOGGER = Logger.getLogger("CChat");

    @Override
    public void onEnable() {
        instance = this;
        config = new Config(new File("plugins/CChat/config.yml"), this.getDataFolder());
        cooldownManager = new CooldownManager();

        this.getCommand("cchat").setExecutor(new ManageCommand());
        this.getCommand("cooldown").setExecutor(new CooldownCommand());

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
    }

    @Override
    public void onDisable() {
    }

    public static CChat getInstance() {
        return instance;
    }

    public Config getConfig() {
        return config;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}