package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gl.GlBlendState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GlBlendState.class)
public interface GlBlendStateAccessor {
	@Invoker("<init>")
	static GlBlendState create(boolean separateBlend, boolean blendDisabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int mode) {
		throw new AssertionError();
	}

	@Accessor
	int getSrcRgb();

	@Accessor
	int getSrcAlpha();

	@Accessor
	int getDstRgb();

	@Accessor
	int getDstAlpha();

	@Accessor
	int getMode();

	@Accessor
	boolean getSeparateBlend();

	@Accessor
	boolean getBlendDisabled();
}
