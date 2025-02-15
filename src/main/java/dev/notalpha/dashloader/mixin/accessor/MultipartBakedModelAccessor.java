package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import net.minecraft.client.texture.Sprite;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.class)
public interface MultipartBakedModelAccessor {
    @Accessor
    List<Pair<Predicate<BlockState>, BakedModel>> getComponents();

    @Accessor
    @Mutable
    void setComponents(List<Pair<Predicate<BlockState>, BakedModel>> components);

    @Accessor
    Map<BlockState, BitSet> getStateCache();

    @Accessor
    @Mutable
    void setStateCache(Map<BlockState, BitSet> stateBitSetMap);

    @Accessor
    @Mutable
    void setSprite(Sprite sprite);
}
