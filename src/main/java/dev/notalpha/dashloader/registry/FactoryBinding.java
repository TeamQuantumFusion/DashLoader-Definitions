package dev.notalpha.dashloader.registry;

import dev.notalpha.dashloader.DashObjectClass;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

public final class FactoryBinding<R, D extends DashObject<R, ?>> {
    private final MethodHandle method;
    private final FactoryFunction creator;

    public FactoryBinding(MethodHandle method, FactoryFunction creator) {
        this.method = method;
        this.creator = creator;
    }

    public static <R, D extends DashObject<R, ?>> FactoryBinding<R, D> create(DashObjectClass<R, D> dashObject) {
        final Class<?> dashClass = dashObject.getDashClass();

        var factory = tryScanCreators((look, type) -> look.findConstructor(dashClass, type.changeReturnType(void.class)), dashObject);
        if (factory == null) {
            factory = tryScanCreators((look, type) -> look.findStatic(dashClass, "factory", type), dashObject);
        }
        if (factory == null) {
            factory = tryScanCreators((look, type) -> look.findStatic(dashClass, "factory", type), dashObject);
        }

        if (factory == null) {
            throw new RuntimeException("Could not find a way to create " + dashClass.getSimpleName() + ". Create the method and/or check if it's accessible.");
        }

        return factory;
    }

    @Nullable
    private static <R, D extends DashObject<R, ?>> FactoryBinding<R, D> tryScanCreators(MethodTester tester, DashObjectClass<R, D> dashObject) {
        for (InvokeType value : InvokeType.values()) {
            final Class<?>[] apply = value.parameters.apply(dashObject);

            try {
                var method = tester.getMethod(
                    MethodHandles.publicLookup(),
                    MethodType.methodType(dashObject.getTargetClass(), apply));

                if (method != null) {
                    return new FactoryBinding<>(method, value.creator);
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    public D create(R raw, RegistryWriter writer) {
        try {
            //noinspection unchecked
            return (D) this.creator.create(this.method, raw, writer);
        } catch (Throwable e) {
            throw new RuntimeException("Could not create DashObject " + raw.getClass().getSimpleName(), e);
        }
    }

    // FULL object, writer
    // WRITER writer
    // RAW object
    // EMPTY
    private enum InvokeType {
        FULL((methodHandle, args, args2) -> methodHandle.invoke(args, args2), doc -> new Class[]{doc.getTargetClass(), RegistryWriter.class}),
        WRITER((mh, raw, writer) -> mh.invoke(writer), doc -> new Class[]{RegistryWriter.class}),
        RAW((mh, raw, writer) -> mh.invoke(raw), doc -> new Class[]{doc.getTargetClass()}),
        EMPTY((mh, raw, writer) -> mh.invoke(), doc -> new Class[0]);
        private final FactoryFunction creator;
        private final Function<DashObjectClass<?, ?>, Class<?>[]> parameters;

        InvokeType(FactoryFunction creator, Function<DashObjectClass<?, ?>, Class<?>[]> parameters) {
            this.creator = creator;
            this.parameters = parameters;
        }
    }

    @FunctionalInterface
    private interface FactoryFunction {
        Object create(MethodHandle method, Object raw, RegistryWriter writer) throws Throwable;
    }

    @FunctionalInterface
    private interface MethodTester {
        MethodHandle getMethod(MethodHandles.Lookup lookup, MethodType parameters) throws Throwable;
    }

}
