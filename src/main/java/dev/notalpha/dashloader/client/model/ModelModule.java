package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.CachingData;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryAddException;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.fallback.UnbakedBakedModel;
import dev.notalpha.dashloader.config.ConfigHandler;
import dev.notalpha.dashloader.config.Option;
import dev.notalpha.dashloader.mixin.accessor.ModelLoaderAccessor;
import dev.notalpha.taski.builtin.StepTask;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModelModule implements DashModule<ModelModule.Data> {
    public static final CachingData<HashMap<Identifier, BakedModel>> MODELS_SAVE = new CachingData<>(CacheStatus.SAVE);
    public static final CachingData<HashMap<Identifier, UnbakedBakedModel>> MODELS_LOAD = new CachingData<>(CacheStatus.LOAD);
    public static final CachingData<HashMap<BlockState, Identifier>> MISSING_READ = new CachingData<>();
    public static final CachingData<HashMap<BakedModel, Pair<List<MultipartModelSelector>, StateManager<Block, BlockState>>>> MULTIPART_PREDICATES = new CachingData<>(CacheStatus.SAVE);

    public static StateManager<Block, BlockState> getStateManager(Identifier identifier) {
        StateManager<Block, BlockState> staticDef = ModelLoaderAccessor.getStaticDefinitions().get(identifier);
        if (staticDef != null) {
            return staticDef;
        } else {
            return Registries.BLOCK.get(identifier).getStateManager();
        }
    }

    @NotNull
    public static Identifier getStateManagerIdentifier(StateManager<Block, BlockState> stateManager) {
        // Static definitions like itemframes.
        for (Map.Entry<Identifier, StateManager<Block, BlockState>> entry : ModelLoaderAccessor.getStaticDefinitions().entrySet()) {
            if (entry.getValue() == stateManager) {
                return entry.getKey();
            }
        }

        return Registries.BLOCK.getId(stateManager.getOwner());
    }

    @Override
    public void reset(Cache cache) {
        MODELS_SAVE.reset(cache, new HashMap<>());
        MODELS_LOAD.reset(cache, new HashMap<>());
        MISSING_READ.reset(cache, new HashMap<>());
        MULTIPART_PREDICATES.reset(cache, new HashMap<>());
    }

    @Override
    public Data save(RegistryWriter factory, StepTask task) {
        var models = MODELS_SAVE.get(CacheStatus.SAVE);

        if (models == null) {
            return null;
        } else {
            var outModels = new IntIntList(new ArrayList<>(models.size()));
            var missingModels = new IntIntList();

            final HashSet<Identifier> out = new HashSet<>();
            task.doForEach(models, (identifier, bakedModel) -> {
                if (bakedModel != null) {
                    try {
                        final int add = factory.add(bakedModel);
                        outModels.put(factory.add(identifier), add);
                        out.add(identifier);
                    } catch (RegistryAddException ignored) {
                        // Fallback is checked later with the blockstates missing.
                    }
                }
            });


            // Check missing models for blockstates.
            for (Block block : Registries.BLOCK) {
                block.getStateManager().getStates().forEach((blockState) -> {
                    final ModelIdentifier modelId = BlockModels.getModelId(blockState);
                    if (!out.contains(modelId)) {
                        missingModels.put(factory.add(blockState), factory.add(modelId));
                    }
                });
            }

            return new Data(outModels, missingModels);
        }
    }

    @Override
    public void load(Data data, RegistryReader reader, StepTask task) {
        final HashMap<Identifier, UnbakedBakedModel> out = new HashMap<>(data.models.list().size());
        data.models.forEach((key, value) -> {
            Dazy<? extends BakedModel> model = reader.get(value);
            Identifier identifier = reader.get(key);
            out.put(identifier, new UnbakedBakedModel(model));
        });

        var missingModelsRead = new HashMap<BlockState, Identifier>();
        data.missingModels.forEach((blockState, modelId) -> {
            missingModelsRead.put(reader.get(blockState), reader.get(modelId));
        });

        DashLoader.LOG.info("Found {} Missing BlockState Models", missingModelsRead.size());
        MISSING_READ.set(CacheStatus.LOAD, missingModelsRead);
        MODELS_LOAD.set(CacheStatus.LOAD, out);
    }

    @Override
    public Class<Data> getDataClass() {
        return Data.class;
    }

    @Override
    public float taskWeight() {
        return 1000;
    }

    @Override
    public boolean isActive() {
        return ConfigHandler.optionActive(Option.CACHE_MODEL_LOADER);
    }

    public static final class Data {
        public final IntIntList models; // identifier to model list
        public final IntIntList missingModels;

        public Data(IntIntList models, IntIntList missingModels) {
            this.models = models;
            this.missingModels = missingModels;
        }
    }
}
