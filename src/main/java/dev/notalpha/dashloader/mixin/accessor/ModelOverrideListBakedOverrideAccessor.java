package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelOverrideList.BakedOverride.class)
public interface ModelOverrideListBakedOverrideAccessor {
	@Invoker("<init>")
	static ModelOverrideList.BakedOverride newModelOverrideListBakedOverride(ModelOverrideList.InlinedCondition[] conditions, @Nullable BakedModel model) {
		throw new AssertionError();
	}

	@Accessor
	ModelOverrideList.InlinedCondition[] getConditions();

	@Accessor
	BakedModel getModel();
}
