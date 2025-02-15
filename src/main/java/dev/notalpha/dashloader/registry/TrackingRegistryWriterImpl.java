package dev.notalpha.dashloader.registry;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.registry.data.ChunkFactory;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.function.Function;

/**
 * The Writers job is to allow dashObject to add dependencies by adding them to the registry and allowing parallelization.
 * The logic is actually in RegistryFactory, but we need to be able to track what added what so the writer gets issued on the invocation of the creator.
 */
public final class TrackingRegistryWriterImpl implements RegistryWriter {
    private final RegistryWriterImpl factory;
    private final IntList dependencies = new IntArrayList();

    private TrackingRegistryWriterImpl(RegistryWriterImpl factory) {
        this.factory = factory;
    }

    static <R, D extends DashObject<R, ?>> ChunkFactory.Entry<D> create(RegistryWriterImpl factory, Function<RegistryWriter, D> function) {
        TrackingRegistryWriterImpl writer = new TrackingRegistryWriterImpl(factory);
        D data = function.apply(writer);
        int[] dependencies = writer.dependencies.toIntArray();
        return new ChunkFactory.Entry<>(data, dependencies);
    }

    public <R> int add(R object) {
        int value = factory.add(object);
        dependencies.add(value);
        return value;
    }

}
