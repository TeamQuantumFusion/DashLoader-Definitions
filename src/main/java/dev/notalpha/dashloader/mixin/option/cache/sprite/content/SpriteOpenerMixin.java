package dev.notalpha.dashloader.mixin.option.cache.sprite.content;

import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.sprite.content.SpriteContentModule;
import dev.notalpha.dashloader.mixin.accessor.SpriteContentsAccessor;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;

@Mixin(SpriteOpener.class)
public interface SpriteOpenerMixin {
	@Inject(
			method = "method_52851",
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/Resource;getInputStream()Ljava/io/InputStream;", shift = At.Shift.BEFORE)
	)
	private static void dashloaderLoad(Collection<ResourceMetadataReader<?>> metadatas, Identifier id, Resource resource, CallbackInfoReturnable<SpriteContents> cir, ResourceMetadata resourceMetadata) {
		var dashSpriteData = SpriteContentModule.SOURCE.get(CacheStatus.LOAD);
		if (dashSpriteData != null) {
			SpriteContents spriteContents = dashSpriteData.get(id);
			if (spriteContents != null) {
				((SpriteContentsAccessor) spriteContents).setMetadata(resourceMetadata);
				cir.setReturnValue(spriteContents);
			}
		}
	}

	@Inject(
			method = "method_52851",
			at = @At(value = "RETURN")
	)
	private static void dashloaderSave(Collection<?> collection, Identifier id, Resource resource, CallbackInfoReturnable<SpriteContents> cir) {
		var dashSpriteData = SpriteContentModule.SOURCE.get(CacheStatus.SAVE);
		if (dashSpriteData != null) {
			dashSpriteData.put(id, cir.getReturnValue());
		}
	}
}
