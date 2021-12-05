package com.dhiep.rediseconomybridge;

import com.dhiep.rediseconomybridge.data.SyncData;
import com.dhiep.rediseconomybridge.data.VaultData;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecoCommand implements CommandExecutor {
    @SuppressWarnings( "deprecation" )
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("reco.admin")) {
            sendMessage(sender, "&cBạn không có quyền dùng lệnh này");
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, "&c/reco <check|set|give|take> <player> [amount]");
            return true;
        }

        String name = args[1];
        Player player = Bukkit.getPlayer(args[1]);
        double localBalance = -1;
        double syncBalance;
        if (player != null) {
            name = player.getName();
            localBalance = VaultData.economy.getBalance(player);
            syncBalance = SyncData.load(player.getUniqueId());
        } else {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
            name = offline.getName();
            if (VaultData.economy.hasAccount(offline)) {
                localBalance = VaultData.economy.getBalance(offline);
            }
            syncBalance = SyncData.load(name);
        }

        if (args[0].equalsIgnoreCase("check")) {
            String display = "&e&l" + name + " &7(" + (player == null ? "&cOffline" : "&aOnline") + "&7)" +
                    "\n&7Local balance: " + (localBalance < 0 ? "&cUndefined" : "&a" + localBalance) +
                    "\n&7Sync balance: " + (syncBalance < 0 ? "&cUndefined" : "&a" + syncBalance);
            sendMessage(sender, display);
            return true;
        }

        // Balance modification
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
            sendMessage(sender, "&cPlease enter a valid amount!");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            double diff = localBalance - amount;
            if (player != null) {
                EconomyResponse response;
                if (diff < 0) {
                    response = VaultData.economy.depositPlayer(player, -diff);
                } else {
                    response = VaultData.economy.withdrawPlayer(player, diff);
                }
                if (response.transactionSuccess()) {
                    Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () ->
                            SyncData.save(player.getUniqueId(), player.getName(), amount));
                    sendMessage(sender, "&b[Local] &7Set balance of &e" + name + " &7to &a&l" + amount);
                } else {
                    sendMessage(sender, "&b[Local] &cFailed to set balance of &e" + name);
                }
            } else if (syncBalance >= 0) {
                String finalName = name;
                Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () -> {
                    SyncData.save(finalName, amount);
                    sendMessage(sender, "&d[Sync] &7Set balance of &e" + finalName + " &7to &a&l" + amount);
                });
            } else {
                sendMessage(sender, "&d[Sync] &cFailed to set balance of &e" + name);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (player != null) {
                double current = localBalance + amount;
                EconomyResponse response = VaultData.economy.depositPlayer(player, amount);
                if (response.transactionSuccess()) {
                    Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () ->
                            SyncData.save(player.getUniqueId(), player.getName(), current));
                    sendMessage(sender, "&b[Local] &7Given &a&l" + amount + " &7to &e" + name
                            + "&7. Current balance: &a&l" + current);
                } else {
                    sendMessage(sender, "&b[Local] &cFailed to give to &e" + name);
                }
            } else if (syncBalance >= 0) {
                double current = syncBalance + amount;
                String finalName = name;
                Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () -> {
                    SyncData.save(finalName, current);
                    sendMessage(sender, "&d[Sync] &7Given &a&l" + amount + " &7to &e" + finalName
                            + "&7. Current balance: &a&l" + current);
                });
            } else {
                sendMessage(sender, "&d[Sync] &cFailed to give to &e" + name);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("take")) {
            if (player != null) {
                double current = localBalance - amount;
                if (current < 0) {
                    sendMessage(sender, "&b[Local] &cPlayer balance will be negative!");
                    return true;
                }
                EconomyResponse response = VaultData.economy.withdrawPlayer(player, amount);
                if (response.transactionSuccess()) {
                    Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () ->
                            SyncData.save(player.getUniqueId(), player.getName(), current));
                    sendMessage(sender, "&b[Local] &7Taken &a&l" + amount + " &7from &e" + name
                            + "&7. Current balance: &a&l" + current);
                } else {
                    sendMessage(sender, "&b[Local] &cFailed to take from &e" + name);
                }
            } else if (syncBalance >= 0) {
                double current = syncBalance - amount;
                if (current < 0) {
                    sendMessage(sender, "&d[Sync] &cPlayer balance will be negative!");
                    return true;
                }
                String finalName = name;
                Bukkit.getScheduler().runTaskAsynchronously(RedisEconomyBridge.instance, () -> {
                    SyncData.save(finalName, current);
                    sendMessage(sender, "&d[Sync] &7Taken &a&l" + amount + " &7from &e" + finalName
                            + "&7. Current balance: &a&l" + current);
                });
            } else {
                sendMessage(sender, "&d[Sync] &cFailed to take from &e" + name);
            }
            return true;
        }

        sendMessage(sender, "&c/reco <check|set|give|take> <player> [amount]");
        return true;
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
