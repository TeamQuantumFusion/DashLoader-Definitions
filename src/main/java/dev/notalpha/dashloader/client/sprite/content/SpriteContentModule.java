package dev.notalpha.dashloader.client.sprite.content;

import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.taski.builtin.StepTask;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class SpriteContentModule implements DashModule<SpriteContentModule.Data> {
    public final static CachingData<Map<Identifier, SpriteContents>> SOURCE = new CachingData<>();

    @Override
    public void reset(Cache cache) {
        SOURCE.reset(cache, new HashMap<>());
    }

    @Override
    public Data save(RegistryWriter writer, StepTask task) {
        var spriteData = SOURCE.get(CacheStatus.SAVE);
        assert spriteData != null;

        var map = new IntIntList();
        task.doForEach(spriteData, (identifier, spriteContents) -> {
            map.put(writer.add(identifier), writer.add(spriteContents));
        });

        return new Data(map);
    }

    @Override
    public void load(Data data, RegistryReader reader, StepTask task) {
        Map<Identifier, SpriteContents> spriteData = SOURCE.get(CacheStatus.LOAD);
        assert spriteData != null;

        data.sprites.forEach((key, value) -> {
            Identifier identifier = reader.get(key);
            SpriteContents contents = reader.get(value);
            spriteData.put(identifier, contents);
        });
    }

    @Override
    public Class<Data> getDataClass() {
        return Data.class;
    }

    public static class Data {
        public final IntIntList sprites;

        public Data(IntIntList sprites) {
            this.sprites = sprites;
        }
    }
}
