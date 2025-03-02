package dev.notalpha.dashloader.mixin.option.misc;

import net.minecraft.client.util.ModelIdentifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(value = ModelIdentifier.class, priority = 999)
public abstract class ModelIdentifierMixin {
	@Shadow
	@Final
	private String variant;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		ModelIdentifier that = (ModelIdentifier) o;
		return Objects.equals(this.variant, that.getVariant());
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + variant.hashCode();
	}
}
