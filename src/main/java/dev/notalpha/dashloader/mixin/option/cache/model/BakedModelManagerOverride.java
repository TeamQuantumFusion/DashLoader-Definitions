package dev.notalpha.dashloader.mixin.option.cache.model;

import dev.notalpha.dashloader.DashLoader;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.model.ModelModule;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = BakedModelManager.class, priority = 69420)
public abstract class BakedModelManagerOverride {
	@Shadow
	private Map<Identifier, BakedModel> models;

	@Inject(method = "upload",
			at = @At(value = "TAIL")
	)
	private void yankAssets(BakedModelManager.BakingResult bakingResult, Profiler profiler, CallbackInfo ci) {
		ModelModule.MODELS_SAVE.visit(CacheStatus.SAVE, map -> {
			DashLoader.LOG.info("Yanking Minecraft Assets");
			map.putAll(this.models);
		});
	}
}
