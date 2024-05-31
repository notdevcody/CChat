package me.devcody.cchat.cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Cooldown> cooldowns;

    public CooldownManager() {
        this.cooldowns = new HashMap<>();
    }

    public void add(Cooldown cooldown) {
        UUID player = cooldown.getPlayer();
        this.cooldowns.remove(player);
        this.cooldowns.put(player, cooldown);
    }

    public boolean hasCooldown(UUID player) {
        Cooldown cooldown = this.cooldowns.get(player);
        if (cooldown == null) {
            return false;
        }
        if (cooldown.isExpired()) {
            this.cooldowns.remove(player);
            return false;
        }
        return true;
    }

    public long getTimeRemaining(UUID player) {
        Cooldown cooldown = this.cooldowns.get(player);
        if (cooldown != null) {
            return cooldown.getTimeRemaining();
        }
        return -1L;
    }
}
