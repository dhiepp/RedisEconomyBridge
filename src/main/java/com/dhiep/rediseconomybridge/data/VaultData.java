package com.dhiep.rediseconomybridge.data;

import com.dhiep.rediseconomybridge.RedisEconomyBridge;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class VaultData {
    public static Economy economy;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().severe("Vault plugin not found! Disabling...");
            Bukkit.getPluginManager().disablePlugin(RedisEconomyBridge.instance);
            return;
        }

        RegisteredServiceProvider<Economy> rspe = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (rspe == null) {
            Bukkit.getLogger().severe("Vault economy not found! Disabling...");
            Bukkit.getPluginManager().disablePlugin(RedisEconomyBridge.instance);
            return;
        }

        economy = rspe.getProvider();
    }

    public static void join(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () -> {
            UUID uuid = player.getUniqueId();
            double syncBalance = SyncData.load(uuid);
            double localBalance = economy.getBalance(player);

            if (syncBalance < 0) return;
            if (syncBalance < localBalance) {
                double diff = localBalance - syncBalance;
                economy.withdrawPlayer(player, diff);
            }
            if (syncBalance > localBalance) {
                double diff = syncBalance - localBalance;
                economy.depositPlayer(player, diff);
            }
        });
    }

    public static void quit(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () -> {
            UUID uuid = player.getUniqueId();
            String name = player.getName();
            double balance = economy.getBalance(player);
            SyncData.save(uuid, name, balance);
        });
    }
}
