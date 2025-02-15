package dev.notalpha.dashloader.client.sprite.stitch;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.config.Option;
import dev.notalpha.taski.builtin.StepTask;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class SpriteStitcherModule implements DashModule<SpriteStitcherModule.Data> {
    public final static CachingData<List<Pair<Identifier, TextureStitcher<?>>>> STITCHERS_SAVE = new CachingData<>(CacheStatus.SAVE);
    public final static CachingData<Map<Identifier, DashTextureStitcher.ExportedData<?>>> STITCHERS_LOAD = new CachingData<>(CacheStatus.LOAD);

    @Override
    public void reset(Cache cache) {
        STITCHERS_SAVE.reset(cache, new ArrayList<>());
        STITCHERS_LOAD.reset(cache, new HashMap<>());
    }

    @Override
    public Data save(RegistryWriter writer, StepTask task) {
        task.reset(2);

        var stitchers = new HashMap<Identifier, DashTextureStitcher.Data<?>>();
        var duplicate = new HashSet<Identifier>();
        task.run(new StepTask("Caching Stitchers"), (stepTask) -> stepTask.doForEach(STITCHERS_SAVE.get(CacheStatus.SAVE), (pair) -> {
            var identifier = pair.getLeft();
            var textureStitcher = pair.getRight();
            DashTextureStitcher.Data<?> existing = stitchers.put(identifier, new DashTextureStitcher.Data<>(writer, textureStitcher));
            if (existing != null) {
                duplicate.add(identifier);
            }
        }));
        duplicate.forEach(identifier -> {
            DashLoader.LOG.warn("Duplicate stitcher {}", identifier);
            stitchers.remove(identifier);
        });

        var output = new IntObjectList<DashTextureStitcher.Data<?>>();

        stitchers.forEach((identifier, data) -> output.put(writer.add(identifier), data));

        //var results = new IntObjectList<DashStitchResult>();
        //task.run(new StepTask("Caching Atlases"), (stepTask) -> {
        //	var map = ATLASES.get(CacheStatus.SAVE);
        //	stepTask.doForEach(map, (identifier, stitchResult) -> {
        //		StepTask atlases = new StepTask("atlas", stitchResult.regions().size());
        //		task.setSubTask(atlases);
        //		results.put(factory.add(identifier), new DashStitchResult(stitchResult, factory, atlases));
        //	});
        //});

        return new Data(output);
    }

    @Override
    public void load(Data data, RegistryReader reader, StepTask task) {
        var map = new HashMap<Identifier, DashTextureStitcher.ExportedData<?>>();
        data.stitchers.forEach((key, value) -> map.put(reader.get(key), value.export(reader)));
        STITCHERS_LOAD.set(CacheStatus.LOAD, map);
    }

    @Override
    public Class<Data> getDataClass() {
        return Data.class;
    }

    @Override
    public boolean isActive() {
        return ConfigHandler.optionActive(Option.CACHE_SPRITE_STITCHING);
    }

    public static final class Data {
        public final IntObjectList<DashTextureStitcher.Data<?>> stitchers;

        public Data(
            IntObjectList<DashTextureStitcher.Data<?>> stitchers) {
            this.stitchers = stitchers;
        }
    }
}
