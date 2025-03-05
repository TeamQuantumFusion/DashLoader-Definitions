package dev.notalpha.dashloader.api.collection;

import java.util.ArrayList;
import java.util.List;

public record ObjectIntList<K>(List<ObjectIntEntry<K>> list) {
	public ObjectIntList() {
		this(new ArrayList<>());
	}

	public void put(K key, int value) {
		this.list.add(new ObjectIntEntry<>(key, value));
	}

	public void forEach(ObjectIntConsumer<K> c) {
		this.list.forEach(v -> c.accept(v.key, v.value));
	}

	@FunctionalInterface
	public interface ObjectIntConsumer<K> {
		void accept(K key, int value);
	}

	public record ObjectIntEntry<K>(K key, int value) {
	}
}
