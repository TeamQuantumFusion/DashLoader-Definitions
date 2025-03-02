package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gl.ShaderStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ShaderStage.class)
public interface ShaderStageAccessor {
	@Invoker("<init>")
	static ShaderStage create(ShaderStage.Type shaderType, int shaderRef, String name) {
		throw new AssertionError();
	}

	@Accessor
	ShaderStage.Type getType();

	@Accessor
	int getGlRef();

	@Mixin(ShaderStage.Type.class)
	interface TypeAccessor {
		@Accessor
		int getGlType();
	}
}

