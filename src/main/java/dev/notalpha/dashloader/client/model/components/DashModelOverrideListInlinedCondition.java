package dev.notalpha.dashloader.client.model.components;

import dev.notalpha.dashloader.mixin.accessor.ModelOverrideListInlinedCondition;
import net.minecraft.client.render.model.json.ModelOverrideList;

public final class DashModelOverrideListInlinedCondition {
	public final int index;
	public final float threshold;

	public DashModelOverrideListInlinedCondition(int index, float threshold) {
		this.index = index;
		this.threshold = threshold;
	}

	public DashModelOverrideListInlinedCondition(ModelOverrideList.InlinedCondition inlinedCondition) {
		this(inlinedCondition.index, inlinedCondition.threshold);
	}

	public ModelOverrideList.InlinedCondition export() {
		return ModelOverrideListInlinedCondition.newModelOverrideListInlinedCondition(this.index, this.threshold);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashModelOverrideListInlinedCondition that = (DashModelOverrideListInlinedCondition) o;

		if (index != that.index) return false;
		return Float.compare(that.threshold, threshold) == 0;
	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + (threshold != 0.0f ? Float.floatToIntBits(threshold) : 0);
		return result;
	}
}
