package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import it.unimi.dsi.fastutil.ints.Int2FloatArrayMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.SpaceFont;

public final class DashSpaceFont implements DashObject<SpaceFont, SpaceFont> {
	public final int[] ints;
	public final float[] floats;

	public DashSpaceFont(int[] ints, float[] floats) {
		this.ints = ints;
		this.floats = floats;
	}

	public DashSpaceFont(SpaceFont font) {
		IntSet glyphs = font.getProvidedGlyphs();
		this.ints = new int[glyphs.size()];
		this.floats = new float[glyphs.size()];
		int i = 0;
		for (Integer providedGlyph : glyphs) {
			Glyph glyph = font.getGlyph(providedGlyph);
			assert glyph != null;
			this.ints[i] = providedGlyph;
			this.floats[i] = glyph.getAdvance();
			i++;
		}
	}

	@Override
	public SpaceFont export(RegistryReader exportHandler) {
		return new SpaceFont(new Int2FloatArrayMap(ints, floats));
	}
}
