package dev.notalpha.dashloader.client.ui;

public class Color {
	private final int rgba;

	public Color(int rgba) {
		this.rgba = rgba;
	}

	public Color(int red, int green, int blue, int alpha) {
		this.rgba = ((red & 0xFF) << 24) | ((green & 0xFF) << 16) | ((blue & 0xFF) << 8) | (alpha & 0xFF);
	}

	public Color(int red, int green, int blue) {
		this(red, green, blue, 255);
	}

	public int red() {
		return (rgba >>> 24) & 0xFF;
	}

	public int green() {
		return (rgba >>> 16) & 0xFF;
	}

	public int blue() {
		return (rgba >>> 8) & 0xFF;
	}

	public int alpha() {
		return rgba & 0xFF;
	}

	public int rgb() {
		return rgba >>> 8;
	}

	public int rgba() {
		return rgba;
	}

	public int argb() {
		return this.rgb() | (this.alpha() << 24);
	}
}
