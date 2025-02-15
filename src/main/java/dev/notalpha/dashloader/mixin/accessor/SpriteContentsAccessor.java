package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {

    @Accessor
    NativeImage getImage();

    @Accessor
    @Mutable
    void setImage(NativeImage image);

    @Accessor
    SpriteContents.Animation getAnimation();

    @Accessor
    @Mutable
    void setAnimation(SpriteContents.Animation animation);

    @Accessor
    NativeImage[] getMipmapLevelsImages();

    @Accessor
    @Mutable
    void setMipmapLevelsImages(NativeImage[] mipmapLevelsImages);

    @Accessor
    @Mutable
    void setId(Identifier id);

    @Accessor
    @Mutable
    void setWidth(int width);

    @Accessor
    @Mutable
    void setHeight(int height);

    @Accessor
    @Mutable
    void setMetadata(ResourceMetadata animation);
}
