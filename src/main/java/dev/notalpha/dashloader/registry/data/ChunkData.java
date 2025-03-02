package dev.notalpha.dashloader.registry.data;

import dev.notalpha.dashloader.DashObjectClass;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.thread.ThreadHandler;

public class ChunkData<R, D extends DashObject<R, ?>> {
	public final byte chunkId;
	public final String name;
	public final DashObjectClass<?, ?> dashObject;
	public final Entry<D>[] dashables;

	public ChunkData(byte chunkId, String name, DashObjectClass<?, ?> dashObject, Entry<D>[] dashables) {
		this.chunkId = chunkId;
		this.name = name;
		this.dashObject = dashObject;
		this.dashables = dashables;
	}

	public void preExport(RegistryReader reader) {
		for (Entry<D> entry : this.dashables) {
			entry.data.preExport(reader);
		}
	}

	public void export(Object[] data, RegistryReader registry) {
		ThreadHandler.INSTANCE.parallelExport(this.dashables, data, registry);
	}

	public void postExport(RegistryReader reader) {
		for (Entry<D> entry : this.dashables) {
			entry.data.postExport(reader);
		}
	}

	public int getSize() {
		return this.dashables.length;
	}

	public static final class Entry<D> {
		public final D data;
		public final int pos;

		public Entry(D data, int pos) {
			this.data = data;
			this.pos = pos;
		}
	}
}
