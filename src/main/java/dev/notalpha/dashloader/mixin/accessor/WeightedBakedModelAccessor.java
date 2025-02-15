package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.util.collection.Weighted;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(WeightedBakedModel.class)
public interface WeightedBakedModelAccessor {

    @Accessor("models")
    List<Weighted.Present<BakedModel>> getBakedModels();
}
