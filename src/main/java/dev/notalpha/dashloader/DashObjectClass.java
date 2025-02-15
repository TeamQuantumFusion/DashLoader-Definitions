package dev.notalpha.dashloader;

import dev.notalpha.dashloader.api.DashObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A DashObject which is an object with adds Dash support to a target object. <br>
 * This class is very lazy as reflection is really slow
 *
 * @param <R> Raw
 * @param <D> Dashable
 */
public final class DashObjectClass<R, D extends DashObject<R, ?>> {
    private final Class<D> dashClass;
    int dashObjectId;
    @Nullable
    private Class<R> targetClass;

    public DashObjectClass(Class<?> dashClass) {
        //noinspection unchecked
        this.dashClass = (Class<D>) dashClass;
    }

    public Class<D> getDashClass() {
        return this.dashClass;
    }

    // lazy
    @NotNull
    public Class<R> getTargetClass() {
        if (this.targetClass == null) {
            Type[] genericInterfaces = this.dashClass.getGenericInterfaces();
            if (genericInterfaces.length == 0) {
                throw new RuntimeException(this.dashClass + " does not implement DashObject.");
            }

            boolean foundDashObject = false;
            Class<?>[] interfaces = this.dashClass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i] == DashObject.class) {
                    foundDashObject = true;
                    var genericInterface = genericInterfaces[i];
                    if (genericInterface instanceof ParameterizedType targetClass) {
                        if (targetClass.getActualTypeArguments()[0] instanceof Class<?> targetClass2) {
                            this.targetClass = (Class<R>) targetClass2;
                        } else {
                            throw new RuntimeException(this.dashClass + " has a non resolvable DashObject parameter");
                        }
                    } else {
                        throw new RuntimeException(this.dashClass + " implements raw DashObject");
                    }
                }
            }

            if (!foundDashObject) {
                throw new RuntimeException(this.dashClass + " must implement DashObject");
            }
        }
        return this.targetClass;
    }

    public int getDashObjectId() {
        return dashObjectId;
    }

    @Override
    public String toString() {
        return "DashObjectClass{" +
            "dashClass=" + dashClass +
            ", targetClass=" + targetClass +
            ", dashObjectId=" + dashObjectId +
            '}';
    }
}
