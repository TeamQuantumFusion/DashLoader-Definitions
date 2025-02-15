package dev.notalpha.dashloader.client.splash;

import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.config.Option;
import dev.notalpha.taski.builtin.StepTask;

import java.util.ArrayList;
import java.util.List;


public class SplashModule implements DashModule<SplashModule.Data> {
    public static final CachingData<List<String>> TEXTS = new CachingData<>();

    @Override
    public void reset(Cache cache) {
        TEXTS.reset(cache, new ArrayList<>());
    }

    @Override
    public Data save(RegistryWriter writer, StepTask task) {
        return new Data(TEXTS.get(CacheStatus.SAVE));
    }

    @Override
    public void load(Data data, RegistryReader reader, StepTask task) {
        TEXTS.set(CacheStatus.LOAD, data.splashList);
    }

    @Override
    public Class<Data> getDataClass() {
        return SplashModule.Data.class;
    }

    @Override
    public boolean isActive() {
        return ConfigHandler.optionActive(Option.CACHE_SPLASH_TEXT);
    }

    @Override
    public float taskWeight() {
        return 1;
    }

    public static final class Data {
        public final List<String> splashList;

        public Data(List<String> splashList) {
            this.splashList = splashList;
        }
    }
}
