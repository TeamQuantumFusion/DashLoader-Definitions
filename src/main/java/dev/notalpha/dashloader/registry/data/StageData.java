package dev.notalpha.dashloader.registry.data;

import dev.notalpha.dashloader.api.registry.RegistryReader;

public class StageData {
    public final ChunkData<?, ?>[] chunks;

    public StageData(ChunkData<?, ?>[] chunks) {
        this.chunks = chunks;
    }

    public void preExport(RegistryReader reader) {
        for (ChunkData<?, ?> chunk : chunks) {
            chunk.preExport(reader);
        }
    }

    public void export(Object[][] data, RegistryReader registry) {
        for (int i = 0; i < chunks.length; i++) {
            ChunkData<?, ?> chunk = chunks[i];
            chunk.export(data[i], registry);
        }
    }

    public void postExport(RegistryReader reader) {
        for (ChunkData<?, ?> chunk : chunks) {
            chunk.postExport(reader);
        }
    }
}
