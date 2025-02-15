package dev.notalpha.dashloader.registry;

import dev.notalpha.dashloader.DashObjectClass;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryAddException;
import dev.notalpha.dashloader.api.registry.RegistryUtil;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.registry.data.ChunkData;
import dev.notalpha.dashloader.registry.data.ChunkFactory;
import dev.notalpha.dashloader.registry.data.StageData;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public final class RegistryWriterImpl implements RegistryWriter {
    public final ChunkFactory<?, ?>[] chunks;
    private final IdentityHashMap<?, Integer> dedup = new IdentityHashMap<>();
    private final Object2ByteMap<Class<?>> target2chunkMappings;
    private final Object2ByteMap<Class<?>> dash2chunkMappings;
    private final List<MissingHandler<?>> missingHandlers;

    private RegistryWriterImpl(ChunkFactory<?, ?>[] chunks, List<MissingHandler<?>> missingHandlers) {
        this.target2chunkMappings = new Object2ByteOpenHashMap<>();
        this.target2chunkMappings.defaultReturnValue((byte) -1);

        this.dash2chunkMappings = new Object2ByteOpenHashMap<>();
        this.dash2chunkMappings.defaultReturnValue((byte) -1);

        this.missingHandlers = missingHandlers;
        this.chunks = chunks;
    }

    public static <R, D extends DashObject<R, ?>> RegistryWriterImpl create(List<MissingHandler<?>> missingHandlers, List<DashObjectClass<?, ?>> dashObjects) {
        if (dashObjects.size() > 63) {
            throw new RuntimeException("Hit group limit of 63. Please contact notalpha if you hit this limit!");
        }

        //noinspection unchecked
        ChunkFactory<R, D>[] chunks = new ChunkFactory[dashObjects.size()];
        RegistryWriterImpl writer = new RegistryWriterImpl(chunks, missingHandlers);

        for (int i = 0; i < dashObjects.size(); i++) {
            final DashObjectClass<R, D> dashObject = (DashObjectClass<R, D>) dashObjects.get(i);

            var dashClass = dashObject.getDashClass();
            var targetClass = dashObject.getTargetClass();
            byte old = writer.target2chunkMappings.put(targetClass, (byte) i);
            if (old != -1) {
                DashObjectClass<?, ?> conflicting = dashObjects.get(old);
                throw new IllegalStateException("DashObjects \"" + dashObject.getDashClass() + "\" and \"" + conflicting.getDashClass() + "\" have the same target class \"" + targetClass + "\".");
            }

            writer.dash2chunkMappings.put(dashClass, (byte) i);
            var factory = FactoryBinding.create(dashObject);
            var name = dashClass.getSimpleName();
            chunks[i] = new ChunkFactory<>((byte) i, name, factory, dashObject);
        }

        return writer;
    }

    public <R> int add(R object) {
        return this.addObject(object);
    }

    @SuppressWarnings("unchecked")
    private <R, D extends DashObject<R, ?>> int addObject(R object) {
        if (this.dedup.containsKey(object)) {
            return this.dedup.get(object);
        }

        if (object == null) {
            throw new NullPointerException("Registry add argument is null");
        }

        var targetClass = object.getClass();
        Integer pointer = null;
        // If we have a dashObject supporting the target we create using its factory constructor
        {
            byte chunkPos = this.target2chunkMappings.getByte(targetClass);
            if (chunkPos != -1) {
                var chunk = (ChunkFactory<R, D>) this.chunks[chunkPos];
                var entry = TrackingRegistryWriterImpl.create(this, writer -> chunk.create(object, writer));
                pointer = chunk.add(entry, this);
            }
        }

        // If we cannot find a target matching we go through the missing handlers
        if (pointer == null) {
            for (MissingHandler missingHandler : this.missingHandlers) {
                if (missingHandler.parentClass.isAssignableFrom(targetClass)) {
                    var entry = TrackingRegistryWriterImpl.create(this, writer -> (D) missingHandler.func.apply(object, writer));
                    if (entry.data != null) {
                        var dashClass = entry.data.getClass();
                        byte chunkPos = this.dash2chunkMappings.getByte(dashClass);
                        if (chunkPos == -1) {
                            throw new RuntimeException("Could not find a ChunkWriter for DashClass " + dashClass);
                        }
                        var chunk = (ChunkFactory<R, D>) this.chunks[chunkPos];
                        pointer = chunk.add(entry, this);
                        break;
                    }
                }
            }
        }

        if (pointer == null) {
            throw new RegistryAddException(targetClass, object);
        }

        ((IdentityHashMap<R, Integer>) this.dedup).put(object, pointer);
        return pointer;
    }

    public <D> ChunkFactory.Entry<D> get(int id) {
        return (ChunkFactory.Entry<D>) this.chunks[RegistryUtil.getChunkId(id)].list.get(RegistryUtil.getObjectId(id));
    }

    public StageData[] export() {
        // Create a queue with the elements with no references
        var exposedQueue = new ArrayDeque<ChunkFactory.Entry<?>>();
        for (ChunkFactory<?, ?> chunk : chunks) {
            for (ChunkFactory.Entry<?> entry : chunk.list) {
                if (entry.references == 0) {
                    entry.stage = 0;
                    exposedQueue.offer(entry);
                }
            }
        }

        // This sets the correct stage for every element
        int stages = 1;
        // Go through the exposed nodes (ones without edges)
        while (!exposedQueue.isEmpty()) {
            // Remove the element from the exposed queue.
            var element = exposedQueue.poll();
            for (var dependencyId : element.dependencies) {
                // Make dependencies a stage above
                ChunkFactory.Entry<?> dependency = get(dependencyId);
                if (dependency.stage <= element.stage) {
                    dependency.stage = element.stage + 1;
                    if (dependency.stage >= stages) {
                        stages = dependency.stage + 1;
                    }
                }
                // Remove the edge, if the dependency no longer has references, add it to the queue.
                if (--dependency.references == 0) {
                    exposedQueue.offer(dependency);
                }
            }
        }

        // Create the output
        StageData[] out = new StageData[stages];
        for (int i = 0; i < stages; i++) {
            ChunkData<?, ?>[] chunksOut = new ChunkData[this.chunks.length];

            for (int j = 0; j < this.chunks.length; j++) {
                ChunkFactory<?, ?> chunk = this.chunks[j];
                List<ChunkData.Entry<?>> dashablesOut = new ArrayList<>();
                for (int k = 0; k < chunk.list.size(); k++) {
                    ChunkFactory.Entry<?> entry = chunk.list.get(k);
                    if (entry.stage == i) {
                        dashablesOut.add(new ChunkData.Entry<>(entry.data, k));
                    }
                }

                chunksOut[j] = new ChunkData<>(chunk.chunkId, chunk.name, chunk.dashObject, dashablesOut.toArray(ChunkData.Entry[]::new));
            }

            out[stages - (i + 1)] = new StageData(chunksOut);
        }

        return out;
    }
}
