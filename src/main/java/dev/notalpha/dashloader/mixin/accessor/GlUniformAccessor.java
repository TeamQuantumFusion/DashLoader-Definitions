package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.gl.GlUniform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Mixin(GlUniform.class)
public interface GlUniformAccessor {
    @Accessor
    IntBuffer getIntData();

    @Accessor
    @Mutable
    void setIntData(IntBuffer intData);

    @Accessor
    FloatBuffer getFloatData();

    @Accessor
    @Mutable
    void setFloatData(FloatBuffer floatData);

    @Accessor
    @Mutable
    void setName(String name);
}
