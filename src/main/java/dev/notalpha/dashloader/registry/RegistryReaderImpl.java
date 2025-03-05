package dev.notalpha.dashloader.registry;

import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.io.data.CacheInfo;
import dev.notalpha.dashloader.registry.data.StageData;
import dev.notalpha.taski.Task;
import dev.notalpha.taski.builtin.StepTask;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@SuppressWarnings("FinalMethodInFinalClass")
public final class RegistryReaderImpl implements RegistryReader {
	private final StageData[] chunkData;
	// Holds an array of the exported dataChunks array values.
	private final Object[][] data;

	public RegistryReaderImpl(CacheInfo metadata, StageData[] data) {
		this.chunkData = data;
		this.data = new Object[metadata.chunks.size()][];
		for (int i = 0; i < metadata.chunks.size(); i++) {
			this.data[i] = new Object[metadata.chunks.get(i).size];
		}
	}

	public final void export(@Nullable Consumer<Task> taskConsumer) {
		StepTask task = new StepTask("Exporting", Integer.max(this.chunkData.length, 1));
		if (taskConsumer != null) {
			taskConsumer.accept(task);
		}

		for (StageData chunkData : chunkData) {
			chunkData.preExport(this);
			chunkData.export(data, this);
			chunkData.postExport(this);
		}
	}

	@SuppressWarnings("unchecked")
	public final <R> R get(final int pointer) {
		// inlining go brrr
		return (R) this.data[pointer & 0x3f][pointer >>> 6];
	}
}
