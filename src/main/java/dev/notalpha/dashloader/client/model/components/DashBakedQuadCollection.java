package dev.notalpha.dashloader.client.model.components;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DashBakedQuadCollection implements DashObject<BakedQuadCollection, DashBakedQuadCollection.DazyImpl> {
	public final List<Integer> quads;

	public DashBakedQuadCollection(List<Integer> quads) {
		this.quads = quads;
	}

	public DashBakedQuadCollection(BakedQuadCollection quads, RegistryWriter writer) {
		this.quads = new ArrayList<>();
		for (BakedQuad quad : quads.quads) {
			this.quads.add(writer.add(quad));
		}
	}

	@Override
	public DazyImpl export(RegistryReader reader) {
		var out = new ArrayList<DashBakedQuad.DazyImpl>(this.quads.size());
		for (Integer quad : this.quads) {
			out.add(reader.get(quad));
		}
		return new DazyImpl(out);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashBakedQuadCollection that = (DashBakedQuadCollection) o;

		return quads.equals(that.quads);
	}

	@Override
	public int hashCode() {
		return quads.hashCode();
	}

	public static class DazyImpl extends Dazy<List<BakedQuad>> {
		public final List<DashBakedQuad.DazyImpl> quads;

		public DazyImpl(List<DashBakedQuad.DazyImpl> quads) {
			this.quads = quads;
		}

		@Override
		protected List<BakedQuad> resolve(Function<SpriteIdentifier, Sprite> spriteLoader) {
			var out = new ArrayList<BakedQuad>(quads.size());
			quads.forEach(
					dazyBakedQuad -> out.add(dazyBakedQuad.get(spriteLoader))
			);
			return out;
		}
	}
}
