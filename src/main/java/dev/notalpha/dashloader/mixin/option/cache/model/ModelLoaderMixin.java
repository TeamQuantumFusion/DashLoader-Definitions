package dev.notalpha.dashloader.mixin.option.cache.model;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import dev.notalpha.dashloader.client.model.fallback.UnbakedBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Mixin(value = ModelLoader.class, priority = 69420)
public abstract class ModelLoaderMixin {
    @Mutable
    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> unbakedModels;
    @Mutable
    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> modelsToBake;

    @Shadow
    protected abstract void method_4716(BlockState blockState);

    @Inject(
        method = "<init>",
        at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=static_definitions", shift = At.Shift.AFTER)
    )
    private void injectLoadedModels(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<ModelLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        ModelModule.MODELS_LOAD.visit(CacheStatus.LOAD, dashModels -> {
            int total = dashModels.size();
            this.unbakedModels.keySet().forEach(dashModels::remove);
            this.modelsToBake.keySet().forEach(dashModels::remove);
            DashLoader.LOG.info("Injecting {}/{} Cached Models", dashModels.size(), total);
            this.unbakedModels.putAll(dashModels);
            this.modelsToBake.putAll(dashModels);
        });
    }

    /**
     * We want to not load all of the blockstate models as we have a list of them available on which ones to load to save a lot of computation
     */
    @WrapOperation(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z", ordinal = 0)
    )
    private boolean loadMissingModels(Iterator instance, Operation<Boolean> original) {
        var map = ModelModule.MISSING_READ.get(CacheStatus.LOAD);
        if (map != null) {
            for (BlockState blockState : map.keySet()) {
                // load thing lambda
                this.method_4716(blockState);
            }
            DashLoader.LOG.info("Loaded {} unsupported models.", map.size());
            return false;
        }
        return original.call(instance);
    }

    @Inject(
        method = "bake",
        at = @At(
            value = "HEAD"
        )
    )
    private void countModels(BiFunction<Identifier, SpriteIdentifier, Sprite> spriteLoader, CallbackInfo ci) {
        if (ModelModule.MODELS_LOAD.active(CacheStatus.LOAD)) {
            // Cache stats
            int cachedModels = 0;
            int fallbackModels = 0;
            for (UnbakedModel value : this.modelsToBake.values()) {
                if (value instanceof UnbakedBakedModel) {
                    cachedModels += 1;
                } else {
                    fallbackModels += 1;
                }
            }
            long totalModels = cachedModels + fallbackModels;
            DashLoader.LOG.info("{}% Cache coverage", (int) (((cachedModels / (float) totalModels) * 100)));
            DashLoader.LOG.info("with {} Fallback models", fallbackModels);
            DashLoader.LOG.info("and  {} Cached models", cachedModels);
        }
    }

//    @Inject(
//        method = "bake",
//        at = @At(
//            value = "TAIL"
//        )
//    )
//    private void debug(BiFunction<Identifier, SpriteIdentifier, Sprite> spriteLoader, CallbackInfo ci) {
//var models = new HashMap<Identifier, BakedModel>();
//this.bakedModels.forEach((identifier, bakedModel) -> {
//	if (
//			bakedModel.getClass() == BasicBakedModel.class ||
//			bakedModel.getClass() == MultipartBakedModel.class ||
//			bakedModel.getClass() == WeightedBakedModel.class ||
//					bakedModel.getClass() == BuiltinBakedModel.class
//	) {
//		return;
//	}
//
//	models.put(identifier, bakedModel);
//});
//		System.out.println();

//
    //String dump = ObjectDumper.dump(new ObjectDumper.Wrapper(models));
    //try {
    //	Files.writeString(Path.of("./output." + DashLoaderClient.CACHE.getStatus()), dump);
    //} catch (IOException e) {
    //	throw new RuntimeException(e);
    //}
//    }
}
