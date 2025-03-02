package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NativeImage.class)
public interface NativeImageAccessor {
	@Invoker("<init>")
	static NativeImage init(NativeImage.Format format, int width, int height, boolean useStb, long pointer) {
		throw new AssertionError();
	}

	@Accessor
	long getPointer();

	@Accessor
	boolean getIsStbImage();
}
