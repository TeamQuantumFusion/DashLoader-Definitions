package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.collection.IntObjectList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.mixin.accessor.BitmapFontAccessor;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.GlyphContainer;

import java.util.ArrayList;

public final class DashBitmapFont implements DashObject<BitmapFont, BitmapFont> {
    public final int image;
    public final IntObjectList<DashBitmapFontGlyph> glyphs;

    public DashBitmapFont(int image,
                          IntObjectList<DashBitmapFontGlyph> glyphs) {
        this.image = image;
        this.glyphs = glyphs;
    }

    public DashBitmapFont(BitmapFont bitmapFont, RegistryWriter writer) {
        BitmapFontAccessor font = ((BitmapFontAccessor) bitmapFont);
        this.image = writer.add(font.getImage());
        this.glyphs = new IntObjectList<>(new ArrayList<>());
        font.getGlyphs().forEachGlyph((integer, bitmapFontGlyph) -> this.glyphs.put(integer, new DashBitmapFontGlyph(bitmapFontGlyph, writer)));
    }

    public BitmapFont export(RegistryReader reader) {
        GlyphContainer<BitmapFont.BitmapFontGlyph> out = new GlyphContainer<>(
            BitmapFont.BitmapFontGlyph[]::new,
            BitmapFont.BitmapFontGlyph[][]::new
        );
        this.glyphs.forEach((key, value) -> out.put(key, value.export(reader)));
        return BitmapFontAccessor.init(reader.get(this.image), out);
    }
}
