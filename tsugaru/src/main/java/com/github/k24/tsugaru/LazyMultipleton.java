package com.github.k24.tsugaru;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by k24 on 2015/07/13.
 */
public abstract class LazyMultipleton<K, T> implements Serializable {
    private final HashMap<K, T> instanceMap = new HashMap<>();

    public final T getInstance(K key) {
        synchronized (instanceMap) {
            T instance = instanceMap.get(key);
            if (instance == null) {
                instance = newInstance(key);
                if (instance == null) return null;
                instanceMap.put(key, instance);
            }
            return instance;
        }
    }

    public final boolean containsKey(K key) {
        synchronized (instanceMap) {
            return instanceMap.containsKey(key);
        }
    }

    public final void clear() {
        synchronized (instanceMap) {
            instanceMap.clear();
        }
    }

    protected abstract T newInstance(K key);
}
