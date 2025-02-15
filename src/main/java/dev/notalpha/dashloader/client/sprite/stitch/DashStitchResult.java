package dev.notalpha.dashloader.client.sprite.stitch;

import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.taski.builtin.StepTask;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


public final class DashStitchResult {
    public final int width;
    public final int height;
    public final int mipLevel;
    public final int missing;
    public final IntIntList regions;

    public DashStitchResult(int width, int height, int mipLevel, int missing, IntIntList regions) {
        this.width = width;
        this.height = height;
        this.mipLevel = mipLevel;
        this.missing = missing;
        this.regions = regions;
    }

    public DashStitchResult(SpriteLoader.StitchResult stitchResult, RegistryWriter writer, StepTask task) {
        this.width = stitchResult.width();
        this.height = stitchResult.height();
        this.mipLevel = stitchResult.mipLevel();
        this.missing = writer.add(stitchResult.missing());

        this.regions = new IntIntList();
        stitchResult.regions().forEach((identifier, sprite) -> {
            this.regions.put(writer.add(identifier), writer.add(sprite));
            task.next();
        });
    }

    public SpriteLoader.StitchResult export(RegistryReader reader) {
        Map<Identifier, Sprite> regions = new Object2ObjectOpenHashMap<>();
        this.regions.forEach((key, value) -> regions.put(reader.get(key), reader.get(value)));

        return new SpriteLoader.StitchResult(
            this.width,
            this.height,
            this.mipLevel,
            reader.get(this.missing),
            regions,
            CompletableFuture.runAsync(
                () -> {
                    throw new RuntimeException("Cached object not yet finalized");
                }
            )
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashStitchResult that = (DashStitchResult) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        if (mipLevel != that.mipLevel) return false;
        if (missing != that.missing) return false;
        return regions.equals(that.regions);
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + mipLevel;
        result = 31 * result + missing;
        result = 31 * result + regions.hashCode();
        return result;
    }
}
