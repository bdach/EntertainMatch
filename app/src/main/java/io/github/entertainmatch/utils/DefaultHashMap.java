package io.github.entertainmatch.utils;

import java.util.HashMap;

/**
 * Created by Adrian Bednarz on 5/13/17.
 */

public class DefaultHashMap<K, V> extends HashMap<K, V> {
    private V defaultValue;

    public DefaultHashMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public V get(Object key) {
        return containsKey(key) ? super.get(key) : defaultValue;
    }
}
