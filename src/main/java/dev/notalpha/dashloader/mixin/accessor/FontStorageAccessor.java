package dev.notalpha.dashloader.mixin.accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.font.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(FontStorage.class)
public interface FontStorageAccessor {
    @Accessor
    void setBlankGlyphRenderer(GlyphRenderer renderer);

    @Accessor
    void setWhiteRectangleGlyphRenderer(GlyphRenderer renderer);

    @Accessor
    GlyphContainer<GlyphRenderer> getGlyphRendererCache();

    @Accessor
    GlyphContainer<FontStorage.GlyphPair> getGlyphCache();

    @Accessor
    Int2ObjectMap<IntList> getCharactersByWidth();

    @Accessor
    List<Font> getFonts();

    @Invoker
    GlyphRenderer callGetGlyphRenderer(RenderableGlyph c);

    @Invoker
    void callCloseFonts();

    @Invoker
    void callCloseGlyphAtlases();
}
