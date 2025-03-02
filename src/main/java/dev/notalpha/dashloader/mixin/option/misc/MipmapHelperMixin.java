package dev.notalpha.dashloader.mixin.option.misc;

import dev.notalpha.dashloader.mixin.accessor.NativeImageAccessor;
import net.minecraft.client.texture.MipmapHelper;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MipmapHelper.class)
public abstract class MipmapHelperMixin {
	// not using wrapOperation because this is just replacing the call
	@Redirect(
			method = {"hasAlpha", "getMipmapLevelsImages"},
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;getColor(II)I")
	)
	private static int getColor(NativeImage image, int x, int y) {
		return MemoryUtil.memGetInt(((NativeImageAccessor) (Object) image).getPointer() + ((long) x + (long) y * (long) image.getWidth()) * 4L);
	}

	@Redirect(
			method = "getMipmapLevelsImages",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V")
	)
	private static void setColor(NativeImage image, int x, int y, int color) {
		MemoryUtil.memPutInt(((NativeImageAccessor) (Object) image).getPointer() + ((long) x + (long) y * (long) image.getWidth()) * 4L, color);
	}
}
