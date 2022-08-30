package io.github.thebesteric.framework.agile.logger.plugin.redis.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.function.Function;

/**
 * RedisUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @since 2022-08-10 14:57:48
 */
public class RedisUtils {

    private final JedisPool pool;
    private int db = 0;

    public RedisUtils(JedisPool pool) {
        this.pool = pool;
    }

    public RedisUtils(JedisPool pool, int db) {
        this(pool);
        this.db = db;
    }

    public String set(String key, String value, int secondsToExpire) {
        return execute(jedis -> jedis.setex(key, secondsToExpire, value));
    }

    public String set(String key, String value, SetParams params) {
        return execute(jedis -> jedis.set(key, value, params));
    }

    public String set(String key, String value) {
        return execute(jedis -> jedis.set(key, value));
    }

    public String get(String key) {
        return execute(jedis -> jedis.get(key));
    }

    public <R> R execute(Function<Jedis, R> function) {
        try (Jedis jedis = pool.getResource()) {
            jedis.select(db);
            return function.apply(jedis);
        }
    }
}
