package dev.notalpha.dashloader;

import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheFactory;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.registry.MissingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

public class CacheFactoryImpl implements CacheFactory {
    private static final Logger LOGGER = LogManager.getLogger("CacheFactory");
    private final List<DashObjectClass<?, ?>> dashObjects;
    private final List<DashModule<?>> modules;
    private final List<MissingHandler<?>> missingHandlers;
    private boolean failed = false;

    public CacheFactoryImpl() {
        this.dashObjects = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.missingHandlers = new ArrayList<>();
    }

    @Override
    public void addDashObject(Class<? extends DashObject<?, ?>> dashClass) {
        final Class<?>[] interfaces = dashClass.getInterfaces();
        if (interfaces.length == 0) {
            LOGGER.error("No DashObject interface found. Class: {}", dashClass.getSimpleName());
            this.failed = true;
            return;
        }
        this.dashObjects.add(new DashObjectClass<>(dashClass));
    }

    @Override
    public void addModule(DashModule<?> module) {
        this.modules.add(module);
    }

    @Override
    public <R> void addMissingHandler(Class<R> rClass, BiFunction<R, RegistryWriter, DashObject<? extends R, ?>> func) {
        this.missingHandlers.add(new MissingHandler<>(rClass, func));
    }

    @Override
    public Cache build(Path cacheDir) {
        if (this.failed) {
            throw new RuntimeException("Failed to initialize the API");
        }

        // Set dashobject ids
        this.dashObjects.sort(Comparator.comparing(o -> o.getDashClass().getName()));
        this.modules.sort(Comparator.comparing(o -> o.getDataClass().getName()));

        int id = 0;
        Class<?> lastClass = null;
        for (DashObjectClass<?, ?> dashObject : this.dashObjects) {
            if (dashObject.getDashClass() == lastClass) {
                DashLoader.LOG.warn("Duplicate DashObject found: {}", dashObject.getDashClass());
                continue;
            }
            lastClass = dashObject.getDashClass();
            dashObject.dashObjectId = id;
            id += 1;
        }

        return new CacheImpl(cacheDir.resolve(DashLoader.MOD_HASH + "/"), modules, dashObjects, this.missingHandlers);

    }
}
