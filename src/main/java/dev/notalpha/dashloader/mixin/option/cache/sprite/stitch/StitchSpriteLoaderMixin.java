package dev.notalpha.dashloader.mixin.option.cache.sprite.stitch;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.sprite.stitch.DashTextureStitcher;
import dev.notalpha.dashloader.client.sprite.stitch.SpriteStitcherModule;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureStitcher;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public final class StitchSpriteLoaderMixin {
    @Shadow
    @Final
    private Identifier id;

    @WrapOperation(
        method = "stitch",
        at = @At(value = "NEW", target = "(III)Lnet/minecraft/client/texture/TextureStitcher;")
    )
    private TextureStitcher dashloaderStitcherLoad(int maxWidth, int maxHeight, int mipLevel, Operation<TextureStitcher> original) {
        if (SpriteStitcherModule.STITCHERS_LOAD.active(CacheStatus.LOAD)) {
            var map = SpriteStitcherModule.STITCHERS_LOAD.get(CacheStatus.LOAD);
            var data = map.get(id);
            if (data != null) {
                return new DashTextureStitcher(maxWidth, maxHeight, mipLevel, data);
            }
        }

        return original.call(maxWidth, maxHeight, mipLevel);
    }

    @Inject(
        method = "stitch",
        at = @At(value = "RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void dashloaderStitcherSave(List<SpriteContents> sprites, int mipLevel, Executor executor, CallbackInfoReturnable<SpriteLoader.StitchResult> cir, int i, TextureStitcher<SpriteContents> textureStitcher) {
        SpriteStitcherModule.STITCHERS_SAVE.visit(CacheStatus.SAVE, map -> map.add(Pair.of(id, textureStitcher)));
    }

    //@Inject(
    //			method = "load(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/Identifier;ILjava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
    //			at = @At(value = "RETURN"),
    //			cancellable = true
    //	)
    //	private void dashloaderWrite(ResourceManager resourceManager, Identifier identifier, int i, Executor executor, CallbackInfoReturnable<CompletableFuture<SpriteLoader.StitchResult>> cir) {
    //		SpriteModule.ATLASES.visit(CacheStatus.SAVE, map -> {
    //			SpriteModule.ATLAS_IDS.get(CacheStatus.SAVE).put(id, identifier);
    //			cir.setReturnValue(cir.getReturnValue().thenApply(stitchResult -> {
    //				map.put(identifier, stitchResult);
    //				return stitchResult;
    //			}));
    //		});
    //	}
    //
    //	@Inject(
    //			method = "load(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/Identifier;ILjava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
    //			at = @At(value = "HEAD"),
    //			cancellable = true
    //	)
    //	private void dashloaderRead(ResourceManager resourceManager, Identifier identifier, int m, Executor executor, CallbackInfoReturnable<CompletableFuture<SpriteLoader.StitchResult>> cir) {
    //		SpriteModule.ATLASES.visit(CacheStatus.LOAD, map -> {
    //			SpriteLoader.StitchResult cached = map.get(identifier);
    //			if (cached != null) {
    //				int mipLevel = cached.mipLevel();
    //				// Correct the executor
    //				CompletableFuture<Void> completableFuture = mipLevel > 0 ? CompletableFuture.runAsync(() -> cached.regions().values().forEach(sprite -> sprite.getContents().generateMipmaps(mipLevel)), executor) : CompletableFuture.completedFuture(null);
    //				cir.setReturnValue(CompletableFuture.completedFuture(new SpriteLoader.StitchResult(
    //						cached.width(),
    //						cached.height(),
    //						mipLevel,
    //						cached.missing(),
    //						cached.regions(),
    //						completableFuture
    //				)));
    //				cir.cancel();
    //			}
    //		});
    //	}
}
