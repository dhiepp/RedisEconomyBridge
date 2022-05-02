package com.dhiep.rediseconomybridge.hook;

import com.dhiep.rediseconomybridge.RedisEconomyBridge;
import org.bukkit.configuration.file.FileConfiguration;

public class HookManager {
    public static void init() {
        FileConfiguration config = RedisEconomyBridge.instance.getConfig();
        boolean essentialsX = config.getBoolean("hook.essentialsx");

        if (essentialsX) new EssentialsXHook().register(RedisEconomyBridge.instance);
    }
}
