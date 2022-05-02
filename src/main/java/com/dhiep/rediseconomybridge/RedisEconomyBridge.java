package com.dhiep.rediseconomybridge;

import com.dhiep.rediseconomybridge.data.RedisPool;
import com.dhiep.rediseconomybridge.data.SaveTask;
import com.dhiep.rediseconomybridge.data.VaultData;
import com.dhiep.rediseconomybridge.hook.HookManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class RedisEconomyBridge extends JavaPlugin {
    public static RedisEconomyBridge instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        RedisPool.init();
        VaultData.init();
        SaveTask.init();
        HookManager.init();

        getServer().getPluginManager().registerEvents(new EventListeners(), this);
        getCommand("reco").setExecutor(new RecoCommand());
    }

    @Override
    public void onDisable() {
        RedisPool.shutdown();
    }
}
