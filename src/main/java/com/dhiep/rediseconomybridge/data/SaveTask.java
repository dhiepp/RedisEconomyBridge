package com.dhiep.rediseconomybridge.data;

import com.dhiep.rediseconomybridge.RedisEconomyBridge;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SaveTask extends BukkitRunnable {
    public static void init() {
        FileConfiguration config = RedisEconomyBridge.instance.getConfig();
        int interval = config.getInt("interval");
        long delay = interval * 20L;
        new SaveTask().runTaskTimerAsynchronously(RedisEconomyBridge.instance, delay, delay);
    }

    @Override
    public void run() {
        Map<String, String> dataMap = new HashMap<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            double balance = VaultData.economy.getBalance(player);
            dataMap.put(uuid.toString(), String.valueOf(balance));
        }
        SyncData.msave(dataMap);
    }
}
