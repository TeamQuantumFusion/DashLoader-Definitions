package dev.notalpha.dashloader.api.collection;

import java.util.ArrayList;
import java.util.List;

public record IntIntList(List<IntInt> list) {
	public IntIntList() {
		this(new ArrayList<>());
	}

	public void put(int key, int value) {
		this.list.add(new IntInt(key, value));
	}

	public void forEach(IntIntConsumer c) {
		this.list.forEach(v -> c.accept(v.key, v.value));
	}

	@FunctionalInterface
	public interface IntIntConsumer {
		void accept(int key, int value);
	}

	public record IntInt(int key, int value) {
	}
}
