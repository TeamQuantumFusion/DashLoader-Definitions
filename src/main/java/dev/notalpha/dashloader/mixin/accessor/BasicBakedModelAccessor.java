package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(BasicBakedModel.class)
public interface BasicBakedModelAccessor {

    @Accessor
    List<BakedQuad> getQuads();

    @Accessor
    Map<Direction, List<BakedQuad>> getFaceQuads();

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
