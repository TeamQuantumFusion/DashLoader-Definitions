package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gl.EffectShaderStage;
import net.minecraft.client.gl.ShaderStage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EffectShaderStage.class)
public interface EffectShaderStageAccessor {
    @Invoker("<init>")
    static EffectShaderStage create(ShaderStage.Type shaderType, int shaderRef, String name) {
        throw new AssertionError();
    }
}
