package com.dhiep.rediseconomybridge.hook;

import com.dhiep.rediseconomybridge.RedisEconomyBridge;
import com.dhiep.rediseconomybridge.data.SyncData;
import net.ess3.api.events.UserBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class EssentialsXHook implements Listener {
    public void register(RedisEconomyBridge instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    @EventHandler
    public void onUserBalanceUpdate(UserBalanceUpdateEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () -> {
            UUID uuid = player.getUniqueId();
            String name = player.getName();
            double balance = event.getNewBalance().doubleValue();
            SyncData.save(uuid, name, balance);
        });
    }
}
