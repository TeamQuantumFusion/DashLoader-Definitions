package dev.notalpha.dashloader.api.collection;

import java.util.ArrayList;
import java.util.List;

public record IntObjectList<V>(List<IntObjectEntry<V>> list) {
	public IntObjectList() {
		this(new ArrayList<>());
	}

	public void put(int key, V value) {
		this.list.add(new IntObjectEntry<>(key, value));
	}

	public void forEach(IntObjectConsumer<V> c) {
		this.list.forEach(v -> c.accept(v.key, v.value));
	}

	@FunctionalInterface
	public interface IntObjectConsumer<V> {
		void accept(int key, V value);
	}

	public record IntObjectEntry<V>(int key, V value) {
	}
}
