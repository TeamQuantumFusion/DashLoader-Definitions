package dev.notalpha.dashloader.api;

import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CachingData<D> {
    @Nullable
    private final CacheStatus onlyOn;
    @Nullable
    private D data;
    private Cache cacheManager;
    @Nullable
    private CacheStatus dataStatus;

    public CachingData(@Nullable CacheStatus onlyOn) {
        this.data = null;
        this.onlyOn = onlyOn;
    }

    public CachingData() {
        this(null);
    }

    public void visit(CacheStatus status, Consumer<D> consumer) {
        if (this.active(status)) {
            consumer.accept(this.data);
        }
    }


    /**
     * Gets the value or returns null if its status does not match the current state.
     **/
    public @Nullable D get(CacheStatus status) {
        if (this.active(status)) {
            return this.data;
        }
        return null;
    }

    public void reset(Cache cacheManager, @NotNull D data) {
        reset(cacheManager, () -> data);
    }

    public void reset(Cache cacheManager, Supplier<@NotNull D> data) {
        this.cacheManager = cacheManager;
        set(cacheManager.getStatus(), data);
    }

    public void set(CacheStatus status, @NotNull D data) {
        set(status, () -> data);
    }

    /**
     * Sets the optional data to the intended status
     **/
    public void set(CacheStatus status, Supplier<@NotNull D> data) {
        if (onlyOn != null && onlyOn != status) {
            this.data = null;
            this.dataStatus = null;
            return;
        }

        if (cacheManager == null) {
            throw new RuntimeException("cacheManager is null. This OptionData has never been reset in its handler.");
        }

        CacheStatus currentStatus = cacheManager.getStatus();
        if (status == currentStatus) {
            this.dataStatus = status;
            this.data = data.get();
        }
    }

    public boolean active(CacheStatus status) {
        return status == this.dataStatus && status == cacheManager.getStatus() && this.data != null && (onlyOn == null || onlyOn == status);
    }
}
