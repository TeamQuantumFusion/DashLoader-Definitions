package dev.notalpha.dashloader.client.font;

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
import net.minecraft.client.font.Font;
import net.minecraft.util.Identifier;
import org.lwjgl.stb.STBTTFontinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontModule implements DashModule<FontModule.Data> {
    public static final CachingData<ProviderIndex> DATA = new CachingData<>();
    public static final CachingData<Map<STBTTFontinfo, Identifier>> FONT_TO_IDENT = new CachingData<>();

    @Override
    public void reset(Cache cache) {
        DATA.reset(cache, new ProviderIndex(new HashMap<>(), new ArrayList<>()));
        FONT_TO_IDENT.reset(cache, new HashMap<>());
    }

    @Override
    public Data save(RegistryWriter factory, StepTask task) {
        ProviderIndex providerIndex = DATA.get(CacheStatus.SAVE);
        assert providerIndex != null;


        int taskSize = 0;
        for (List<Font> value : providerIndex.providers.values()) {
            taskSize += value.size();
        }
        taskSize += providerIndex.allProviders.size();
        task.reset(taskSize);

        var providers = new IntObjectList<List<Integer>>();
        providerIndex.providers.forEach((identifier, fonts) -> {
            var values = new ArrayList<Integer>();
            for (Font font : fonts) {
                values.add(factory.add(font));
                task.next();
            }
            providers.put(factory.add(identifier), values);
        });

        var allProviders = new ArrayList<Integer>();
        for (Font allProvider : providerIndex.allProviders) {
            allProviders.add(factory.add(allProvider));
            task.next();
        }

        return new Data(new DashProviderIndex(providers, allProviders));
    }

    @Override
    public void load(Data data, RegistryReader reader, StepTask task) {
        ProviderIndex index = new ProviderIndex(new HashMap<>(), new ArrayList<>());
        data.fontMap.providers.forEach((key, value) -> {
            var fonts = new ArrayList<Font>();
            for (Integer i : value) {
                fonts.add(reader.get(i));
            }
            index.providers.put(reader.get(key), fonts);
        });

        data.fontMap.allProviders.forEach((value) -> {
            index.allProviders.add(reader.get(value));
        });
        DATA.set(CacheStatus.LOAD, index);
    }

    @Override
    public Class<Data> getDataClass() {
        return Data.class;
    }

    @Override
    public boolean isActive() {
        return ConfigHandler.optionActive(Option.CACHE_FONT);
    }

    public static final class Data {
        public final DashProviderIndex fontMap;

        public Data(DashProviderIndex fontMap) {
            this.fontMap = fontMap;
        }
    }

    public static final class DashProviderIndex {
        public final IntObjectList<List<Integer>> providers;
        public final List<Integer> allProviders;

        public DashProviderIndex(IntObjectList<List<Integer>> providers, List<Integer> allProviders) {
            this.providers = providers;
            this.allProviders = allProviders;
        }
    }

    public static final class ProviderIndex {
        public final Map<Identifier, List<Font>> providers;
        public final List<Font> allProviders;


        public ProviderIndex(Map<Identifier, List<Font>> providers, List<Font> allProviders) {
            this.providers = providers;
            this.allProviders = allProviders;
        }
    }
}
