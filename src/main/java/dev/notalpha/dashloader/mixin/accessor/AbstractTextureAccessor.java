package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractTexture.class)
public interface AbstractTextureAccessor {

    @Accessor
    boolean getBilinear();

    @Accessor
    void setBilinear(boolean bilinear);

    @Accessor
    boolean getMipmap();

    @Accessor
    void setMipmap(boolean mipmap);
}
