package me.devcody.cchat.config;

import me.devcody.cchat.CChat;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;


public class Config extends Configuration {
    private final File dataFolder;
    public Cooldown cooldown;
    public Blacklist blacklist;

    public Config(File configFile, File dataFolder) {
        super(configFile);
        this.dataFolder = dataFolder;

        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }

        this.reload();
    }

    public void reload() {
        this.load();

        this.cooldown = new Cooldown();
        this.blacklist = new Blacklist();
    }

    private void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        try (InputStream in = CChat.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found!");
            }

            File outFile = new File(dataFolder, resourcePath);
            File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(resourcePath.lastIndexOf('/'), 0)));

            if (!outDir.exists()) {
                outDir.mkdirs();
            }

            if (!outFile.exists() || replace) {
                try (OutputStream out = Files.newOutputStream(outFile.toPath())) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
            } else {
                CChat.LOGGER.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because it already exists.");
            }
        } catch (IOException ex) {
            CChat.LOGGER.log(Level.SEVERE, "Could not save " + resourcePath + " to " + dataFolder, ex);
        }
    }

    public String fGetString(String key, String def) {
        return this.getString(key, def).replace('&', '§');
    }

    public class Blacklist {
        public final boolean enabled;
        public final boolean commands;
        public final List<String> blockList;
        public final String blockMessage;

        public final List<String> muteList;
        public final String muteTime;
        public final String muteReason;
        public final String muteMessage;
        public final String muteCommand;

        public final List<String> banList;
        public final String banReason;
        public final String banCommand;

        private Blacklist() {
            Config config = Config.this;
            this.enabled = config.getBoolean("blacklist.enabled", true);
            this.commands = config.getBoolean("blacklist.commands", true);
            this.blockList = config.getStringList("blacklist.block", Collections.emptyList());
            this.blockMessage = config.fGetString("blacklist.block-message", "&cYou shouldn't say that! Offending word: %s.");

            this.muteList = config.getStringList("blacklist.mute", Collections.emptyList());
            this.muteTime = config.getString("blacklist.mute-time", "10m");
            this.muteReason = config.fGetString("blacklist.mute-reason", "Usage of blacklisted words");
            this.muteMessage = config.fGetString("blacklist.mute-message", "&cYou were muted for &e%time%&c. Offending word: %s.");
            this.muteCommand = config.getString("blacklist.mute-command", "");

            this.banList = config.getStringList("blacklist.ban", Collections.emptyList());
            this.banReason = config.fGetString("blacklist.ban-reason", "&cYou've been banned for using prohibited wording.");
            this.banCommand = config.getString("blacklist.ban-command", "");
        }
    }

    public class Cooldown {
        private final Config config = Config.this;

        public boolean enabled;
        public final boolean commands;
        public int length;
        public final String message;

        private Cooldown() {
            this.enabled = config.getBoolean("cooldown.enabled", true);
            this.commands = config.getBoolean("cooldown.commands", true);
            this.length = config.getInt("cooldown.length", 1500);
            this.message = config.fGetString("cooldown.message", "&cYou're typing too fast! %.2f seconds remaining.");
        }
    }
}