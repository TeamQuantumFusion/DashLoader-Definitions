package dev.notalpha.dashloader.mixin.option.cache.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.client.shader.ShaderModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GlStateManager.class)
public class GlStateManagerMixin {
	@Inject(
			method = "glShaderSource",
			at = @At(value = "HEAD")
	)
	private static void glShaderSourceInject(int shader, List<String> strings, CallbackInfo ci) {
		ShaderModule.WRITE_PROGRAM_SOURCES.visit(CacheStatus.SAVE, map -> map.put(shader, strings));
	}
}

