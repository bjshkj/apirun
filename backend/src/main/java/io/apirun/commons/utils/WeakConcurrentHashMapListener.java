package io.apirun.commons.utils;

public interface WeakConcurrentHashMapListener<K, V> {
    public void notifyOnAdd(K key, V value);

    public void notifyOnRemoval(K key, V value);
}