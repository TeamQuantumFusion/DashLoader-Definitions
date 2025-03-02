package dev.notalpha.dashloader.client.model.components;

import net.minecraft.client.render.model.BakedQuad;

import java.util.List;

public class BakedQuadCollection {
	public final List<BakedQuad> quads;

	public BakedQuadCollection(List<BakedQuad> quads) {
		this.quads = quads;
	}
}
