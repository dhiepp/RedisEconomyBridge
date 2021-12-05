package com.dhiep.rediseconomybridge.data;

import com.dhiep.rediseconomybridge.RedisEconomyBridge;
import org.bukkit.configuration.file.FileConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static String namespace;
    private static JedisPool pool;

    public static void init() {
        FileConfiguration config = RedisEconomyBridge.instance.getConfig();
        String host = config.getString("host");
        int port = config.getInt("port");
        int timeout = config.getInt("timeout");
        String password = config.getString("password");
        namespace = config.getString("namespace", "economy") + ":";

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        pool = new JedisPool(poolConfig, host, port, timeout, password);
    }

    public static String getNamespace() {
        return namespace;
    }

    public static JedisPool getPool() {
        return pool;
    }

    public static void shutdown() {
        pool.destroy();
    }
}
