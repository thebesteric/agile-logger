package io.github.thebesteric.framework.agile.logger.spring.plugin.mocker;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Builder;

import java.util.concurrent.TimeUnit;

/**
 * MockCache
 *
 * @author Eric Joe
 * @version 1.0
 */
public class MockCache {

    public Cache<Object, Object> cache;

    public MockCache() {
        this(CacheConfiguration.builder().build());
    }

    public MockCache(CacheConfiguration configuration) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder().softValues();
        if (configuration.expireAfterWrite > 0) {
            caffeine.expireAfterWrite(configuration.expireAfterWrite, TimeUnit.SECONDS);
        }
        if (configuration.expireAfterAccess > 0) {
            caffeine.expireAfterAccess(configuration.expireAfterAccess, TimeUnit.SECONDS);
        }
        cache = caffeine.build();
    }

    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    public Object get(Object key) {
        return cache.getIfPresent(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> clazz) {
        return (T) cache.getIfPresent(key);
    }

    public void clean() {
        cache.invalidateAll();
    }

    @Builder
    public static class CacheConfiguration {
        /** 最后一次写操作后经过指定时间过期，单位秒 */
        @Builder.Default
        private int expireAfterWrite = 600;

        /** 最后一次读或写操作后经过指定时间过期，单位秒 */
        @Builder.Default
        private int expireAfterAccess = 600;
    }

}
