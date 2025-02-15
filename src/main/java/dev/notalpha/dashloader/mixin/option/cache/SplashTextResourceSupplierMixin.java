package dev.notalpha.dashloader.mixin.option.cache;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.splash.SplashModule;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {
    @Inject(
        method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void applySplashCache(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<List<String>> cir) {
        SplashModule.TEXTS.visit(CacheStatus.LOAD, cir::setReturnValue);
    }

    @Inject(
        method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Ljava/util/List;",
        at = @At(value = "RETURN")
    )
    private void stealSplashCache(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<List<String>> cir) {
        SplashModule.TEXTS.visit(CacheStatus.SAVE, strings -> {
            strings.clear();
            strings.addAll(cir.getReturnValue());
        });
    }
}
