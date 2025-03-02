package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.json.ModelOverrideList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelOverrideList.InlinedCondition.class)
public interface ModelOverrideListInlinedCondition {
	@Invoker("<init>")
	static ModelOverrideList.InlinedCondition newModelOverrideListInlinedCondition(int index, float threshold) {
		throw new AssertionError();
	}
}
