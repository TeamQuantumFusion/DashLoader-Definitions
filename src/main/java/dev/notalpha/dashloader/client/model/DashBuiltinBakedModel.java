package dev.notalpha.dashloader.client.model;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import dev.notalpha.dashloader.client.model.components.DashModelOverrideList;
import dev.notalpha.dashloader.client.model.components.DashModelTransformation;
import dev.notalpha.dashloader.client.sprite.content.DashSprite;
import dev.notalpha.dashloader.mixin.accessor.BuiltinBakedModelAccessor;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;

import java.util.Objects;
import java.util.function.Function;

public final class DashBuiltinBakedModel implements DashObject<BuiltinBakedModel, DashBuiltinBakedModel.DazyImpl> {
	@DataNullable
	public final DashModelTransformation transformation;
	public final DashModelOverrideList itemPropertyOverrides;
	public final int spritePointer;
	public final boolean sideLit;

	public DashBuiltinBakedModel(DashModelTransformation transformation, DashModelOverrideList itemPropertyOverrides, int spritePointer, boolean sideLit) {
		this.transformation = transformation;
		this.itemPropertyOverrides = itemPropertyOverrides;
		this.spritePointer = spritePointer;
		this.sideLit = sideLit;
	}

	public DashBuiltinBakedModel(BuiltinBakedModel builtinBakedModel, RegistryWriter writer) {
		BuiltinBakedModelAccessor access = ((BuiltinBakedModelAccessor) builtinBakedModel);
		final ModelTransformation transformation = access.getTransformation();
		this.transformation = DashModelTransformation.createDashOrReturnNullIfDefault(transformation);
		this.itemPropertyOverrides = new DashModelOverrideList(access.getItemPropertyOverrides(), writer);
		this.spritePointer = writer.add(access.getSprite());
		this.sideLit = access.getSideLit();
	}

	@Override
	public DazyImpl export(RegistryReader reader) {
		DashSprite.DazyImpl sprite = reader.get(this.spritePointer);
		DashModelOverrideList.DazyImpl export = this.itemPropertyOverrides.export(reader);
		return new DazyImpl(DashModelTransformation.exportOrDefault(this.transformation), export, sprite, this.sideLit);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashBuiltinBakedModel that = (DashBuiltinBakedModel) o;

		if (spritePointer != that.spritePointer) return false;
		if (sideLit != that.sideLit) return false;
		if (!Objects.equals(transformation, that.transformation))
			return false;
		return itemPropertyOverrides.equals(that.itemPropertyOverrides);
	}

	@Override
	public int hashCode() {
		int result = transformation != null ? transformation.hashCode() : 0;
		result = 31 * result + itemPropertyOverrides.hashCode();
		result = 31 * result + spritePointer;
		result = 31 * result + (sideLit ? 1 : 0);
		return result;
	}

	public static class DazyImpl extends Dazy<BuiltinBakedModel> {
		public final ModelTransformation transformation;
		public final DashModelOverrideList.DazyImpl itemPropertyOverrides;
		public final DashSprite.DazyImpl sprite;
		public final boolean sideLit;

		public DazyImpl(ModelTransformation transformation, DashModelOverrideList.DazyImpl itemPropertyOverrides, DashSprite.DazyImpl sprite, boolean sideLit) {
			this.transformation = transformation;
			this.itemPropertyOverrides = itemPropertyOverrides;
			this.sprite = sprite;
			this.sideLit = sideLit;
		}

		@Override
		protected BuiltinBakedModel resolve(Function<SpriteIdentifier, Sprite> spriteLoader) {
			Sprite sprite = this.sprite.get(spriteLoader);
			ModelOverrideList list = itemPropertyOverrides.get(spriteLoader);
			return new BuiltinBakedModel(transformation, list, sprite, sideLit);
		}
	}
}
