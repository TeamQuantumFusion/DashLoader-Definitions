package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.font.UnihexFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(UnihexFont.class)
public interface UnihexFontAccessor {
    @Invoker("<init>")
    static UnihexFont create(GlyphContainer<UnihexFont.UnicodeTextureGlyph> glyphs) {
        throw new AssertionError();
    }

    @Accessor
    GlyphContainer<UnihexFont.UnicodeTextureGlyph> getGlyphs();

    @Accessor
    @Mutable
    void setGlyphs(GlyphContainer<UnihexFont.UnicodeTextureGlyph> glyphs);
}
