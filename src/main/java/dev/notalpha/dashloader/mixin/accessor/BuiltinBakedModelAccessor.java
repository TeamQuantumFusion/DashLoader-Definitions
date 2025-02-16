package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinBakedModel.class)
public interface BuiltinBakedModelAccessor {
    @Accessor
    ModelTransformation getTransformation();

    @Accessor
    ModelOverrideList getItemPropertyOverrides();

    @Accessor
    Sprite getSprite();

    @Accessor
    boolean getSideLit();
}

