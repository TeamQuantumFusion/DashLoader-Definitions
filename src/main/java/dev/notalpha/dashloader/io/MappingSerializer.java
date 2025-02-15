package dev.notalpha.dashloader.io;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.taski.Task;
import dev.notalpha.taski.builtin.StepTask;
import dev.notalpha.taski.builtin.WeightedStageTask;
import dev.quantumfusion.hyphen.io.ByteBufferIO;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MappingSerializer {
    private final Object2ObjectMap<Class<?>, Serializer<?>> serializers;

    public MappingSerializer(List<DashModule<?>> cacheHandlers) {
        this.serializers = new Object2ObjectOpenHashMap<>();

        cacheHandlers.forEach(handler -> {
            Class<?> dataClass = handler.getDataClass();
            this.serializers.put(dataClass, new Serializer<>(dataClass));
        });
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public void save(Path dir, RegistryWriter factory, List<DashModule<?>> handlers, StepTask parent) {
        List<WeightedStageTask.WeightedStage> tasks = new ArrayList<>();
        for (DashModule<?> value : handlers) {
            tasks.add(new WeightedStageTask.WeightedStage(value.taskWeight(), new StepTask(value.getDataClass().getSimpleName(), 1)));
        }
        WeightedStageTask stageTask = new WeightedStageTask("Mapping", tasks);
        parent.setSubTask(stageTask);


        List<Object> objects = new ArrayList<>();
        int i = 0;
        for (DashModule<?> handler : handlers) {
            Task task = stageTask.getStages().get(i).task;
            if (handler.isActive()) {
                Object object = handler.save(factory, (StepTask) task);
                Class<?> dataClass = handler.getDataClass();
                if (object.getClass() != dataClass) {
                    throw new RuntimeException("Handler DataClass does not match the output of saveMappings on " + handler.getClass());
                }
                objects.add(object);
            } else {
                objects.add(null);
            }
            //noinspection DataFlowIssue
            task.finish();
            i++;
        }

        Path path = dir.resolve("mapping.bin");

        int measure = 0;
        for (Object object : objects) {
            measure += 1;
            if (object != null) {
                Class<?> aClass = object.getClass();
                Serializer serializer = this.serializers.get(aClass);

                if (serializer == null) {
                    throw new RuntimeException("Could not find mapping serializer for " + aClass);
                }

                measure += serializer.measure(object);
            }
        }

        ByteBufferIO io = ByteBufferIO.createDirect(measure);
        for (Object object : objects) {
            if (object == null) {
                io.putByte((byte) 0);
            } else {
                io.putByte((byte) 1);
                Serializer serializer = this.serializers.get(object.getClass());
                serializer.put(io, object);
            }
        }

        try {
            io.rewind();
            IOHelper.save(path, new StepTask(""), io, measure, ConfigHandler.INSTANCE.config.compression);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean load(Path dir, RegistryReader reader, List<DashModule<?>> handlers) {
        try {
            ByteBufferIO io = IOHelper.load(dir.resolve("mapping.bin"));
            for (DashModule handler : handlers) {
                if (io.getByte() == 0 && handler.isActive()) {
                    DashLoader.LOG.info("Recaching as " + handler.getClass().getSimpleName() + " is now active.");
                    return false;
                } else {
                    Class<?> dataClass = handler.getDataClass();
                    Serializer<?> serializer = this.serializers.get(dataClass);
                    Object object = serializer.get(io);

                    if (handler.isActive()) {
                        handler.load(object, reader, new StepTask(""));
                    }
                }
            }

            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
