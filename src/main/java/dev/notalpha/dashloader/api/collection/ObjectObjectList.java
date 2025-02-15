package dev.notalpha.dashloader.api.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public record ObjectObjectList<K, V>(List<ObjectObjectEntry<K, V>> list) {
    public ObjectObjectList() {
        this(new ArrayList<>());
    }

    public void put(K key, V value) {
        this.list.add(new ObjectObjectEntry<>(key, value));
    }

    public void forEach(BiConsumer<K, V> c) {
        this.list.forEach(v -> c.accept(v.key, v.value));
    }

    public record ObjectObjectEntry<K, V>(K key, V value) {
    }
}
