package dev.notalpha.dashloader.client.sprite.stitch;

import net.minecraft.client.texture.TextureStitcher;

public class DashTextureSlot<T extends TextureStitcher.Stitchable> {
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public transient T contents;

	public DashTextureSlot(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}
