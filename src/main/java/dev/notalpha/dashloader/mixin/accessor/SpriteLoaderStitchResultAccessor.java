package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.texture.SpriteLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteLoader.StitchResult.class)
public interface SpriteLoaderStitchResultAccessor {

    @Accessor
    int getWidth();

    @Accessor
    int getHeight();

    @Accessor
    int getMipLevel();


}
