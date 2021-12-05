package com.dhiep.rediseconomybridge;

import com.dhiep.rediseconomybridge.data.VaultData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListeners implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        VaultData.join(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        VaultData.quit(event.getPlayer());
    }
}
