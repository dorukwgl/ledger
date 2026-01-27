package com.doruk.infrastructure.caching;

import com.doruk.application.interfaces.MemoryStorage;
import com.doruk.infrastructure.config.AppConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.micronaut.context.annotation.Bean;
import io.micronaut.json.JsonMapper;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Singleton
@Bean(typed = MemoryStorage.class)
public class RedisMemoryStorage implements MemoryStorage {
    private final RedisCommands<String, byte[]> client;
    private final JsonMapper serde;
    private final AppConfig appConfig;

    public RedisMemoryStorage(RedisClient redisClient, JsonMapper mapper, AppConfig appConfig) {
        this.client = redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)).sync();
        this.serde = mapper;
        this.appConfig = appConfig;
    }

    private byte[] serialize(Object value) {
        try {
            return serde.writeValueAsBytes(value);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize value", e);
        }
    }

    private <T> T deserialize(byte[] data, Class<T> type) {
        try {
            return serde.readValue(data, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize value", e);
        }
    }

    private String getKey(String key) {
        return key + ":" + appConfig.appId();
    }

    @Override
    public <V> void save(String key, V value) {
        this.client.set(getKey(key), serialize(value));
    }

    @Override
    public <V> void saveEx(String key, V value, Duration ttl) {
        this.client.setex(getKey(key), ttl.getSeconds(), serialize(value));
    }

    @Override
    public void save(String key, Number value) {
        this.client.set(getKey(key), value.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void saveEx(String key, Number value, Duration ttl) {
        this.client.setex(getKey(key), ttl.getSeconds(), value.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public <V> Optional<V> get(String key, Class<V> type) {
        var bytes = this.client.get(getKey(key));
        if (bytes == null)
            return Optional.empty();

        return Optional.of(deserialize(bytes, type));
    }

    @Override
    public void delete(String key) {
        this.client.del(getKey(key));
    }

    @Override
    public long increment(String key) {
        return this.client.incr(getKey(key));
    }

    @Override
    public long increment(String key, int value) {
        return this.client.incrby(getKey(key), value);
    }

    @Override
    public long decrement(String key) {
        return this.client.decr(getKey(key));
    }

    @Override
    public long decrement(String key, int value) {
        return this.client.decrby(getKey(key), value);
    }
}
