package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BasicBakedModel.class)
public interface BasicBakedModelAccessor {
    @Accessor
    boolean getUsesAo();

    @Accessor
    boolean getHasDepth();

    @Accessor
    boolean getIsSideLit();

    @Accessor
    Sprite getSprite();

    @Accessor
    ModelTransformation getTransformation();

    @Accessor
    ModelOverrideList getItemPropertyOverrides();
}
