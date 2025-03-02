package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.collection.IntObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.mixin.accessor.UnihexFontAccessor;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.UnihexFont;

public final class DashUnihexFont implements DashObject<UnihexFont, UnihexFont> {
    public final IntObjectList<UnihexFont.UnicodeTextureGlyph> glyphs;

    public DashUnihexFont(IntObjectList<UnihexFont.UnicodeTextureGlyph> glyphs) {
        this.glyphs = glyphs;
    }

    public DashUnihexFont(UnihexFont rawFont, RegistryWriter writer) {
        this.glyphs = new IntObjectList<>();
        var font = ((UnihexFontAccessor) rawFont);
        var fontImages = font.getGlyphs();
        fontImages.forEachGlyph(this.glyphs::put);
    }

    public UnihexFont export(RegistryReader handler) {
        GlyphContainer<UnihexFont.UnicodeTextureGlyph> container = new GlyphContainer<>(
            UnihexFont.UnicodeTextureGlyph[]::new,
            UnihexFont.UnicodeTextureGlyph[][]::new
        );
        this.glyphs.forEach(container::put);
        return UnihexFontAccessor.create(container);
    }

    public static class DashUnicodeTextureGlyph {
        public final UnihexFont.BitmapGlyph contents;
        public final int left;
        public final int right;

        public DashUnicodeTextureGlyph(UnihexFont.BitmapGlyph contents, int left, int right) {
            this.contents = contents;
            this.left = left;
            this.right = right;
        }

        public DashUnicodeTextureGlyph(UnihexFont.UnicodeTextureGlyph glyph) {
            this.contents = glyph.contents();
            this.left = glyph.left();
            this.right = glyph.right();
        }
    }
}
