package com.dhiep.rediseconomybridge.data;

import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;

public class SyncData {
    public static double load(UUID uuid) {
        try (Jedis jedis = RedisPool.getPool().getResource()) {
            String key = RedisPool.getNamespace() + "mapping";
            String value = jedis.hget(key, uuid.toString());

            return Double.parseDouble(value);
        } catch (Exception exception) {
            return -1;
        }
    }

    public static double load(String name) {
        try (Jedis jedis = RedisPool.getPool().getResource()) {
            String key = RedisPool.getNamespace() + "lookup";
            String value = jedis.hget(key, name);

            return load(UUID.fromString(value));
        } catch (Exception exception) {
            return -1;
        }
    }

    public static void save(UUID uuid, String name, double balance) {
        try (Jedis jedis = RedisPool.getPool().getResource()) {
            String key = RedisPool.getNamespace() + "mapping";
            jedis.hset(key, uuid.toString(), String.valueOf(balance));

            key = RedisPool.getNamespace() + "lookup";
            jedis.hset(key, name, uuid.toString());
        } catch (Exception ignored) {}
    }

    public static void save(String name, double balance) {
        try (Jedis jedis = RedisPool.getPool().getResource()) {
            String key = RedisPool.getNamespace() + "lookup";
            String value = jedis.hget(key, name);

            save(UUID.fromString(value), name, balance);
        } catch (Exception ignored) {}
    }

    public static void msave(Map<String, String> dataMap) {
        try (Jedis jedis = RedisPool.getPool().getResource()) {
            String key = RedisPool.getNamespace() + "mapping";
            jedis.hmset(key, dataMap);
        } catch (Exception ignored) {}
    }
}
