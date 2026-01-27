package com.doruk.application.interfaces;

import java.time.Duration;
import java.util.Optional;

public interface MemoryStorage {
    <V> void save(String key, V value);
    <V> void saveEx(String key, V value, Duration ttl);

    void save(String key, Number value);

    void saveEx(String key, Number value, Duration ttl);

    <V> Optional<V> get(String key, Class<V> type);
    void delete(String key);
    long increment(String key);

    long increment(String key, int value);

    long decrement(String key);

    long decrement(String key, int value);
}
