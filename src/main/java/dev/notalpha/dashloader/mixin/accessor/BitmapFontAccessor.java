package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BitmapFont.class)
public interface BitmapFontAccessor {
    @Invoker("<init>")
    static BitmapFont init(NativeImage image, GlyphContainer<BitmapFont.BitmapFontGlyph> glyphs) {
        throw new AssertionError();
    }

    @Accessor
    GlyphContainer<BitmapFont.BitmapFontGlyph> getGlyphs();

    @Accessor
    NativeImage getImage();
}
