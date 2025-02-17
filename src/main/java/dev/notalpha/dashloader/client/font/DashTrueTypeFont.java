package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.io.IOHelper;
import dev.notalpha.dashloader.misc.UnsafeHelper;
import dev.notalpha.dashloader.mixin.accessor.TrueTypeFontAccessor;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FreeTypeUtil;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_Vector;
import org.lwjgl.util.freetype.FreeType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DashTrueTypeFont implements DashObject<TrueTypeFont, TrueTypeFont> {
    public final byte[] fontData;
    public final float oversample;
    public final List<Integer> excludedCharacters;
    public final float size;
    public final long shiftX;
    public final long shiftY;
    private transient TrueTypeFont _font;

    public DashTrueTypeFont(byte[] fontData, float oversample, List<Integer> excludedCharacters, float size, long shiftX, long shiftY) {
        this.fontData = fontData;
        this.oversample = oversample;
        this.excludedCharacters = excludedCharacters;
        this.size = size;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    public DashTrueTypeFont(TrueTypeFont font) {
        TrueTypeFontAccessor fontAccess = (TrueTypeFontAccessor) font;
        FT_Face ft_face = fontAccess.getFace();
        Pair<Identifier, Float> pair = FontModule.FONT_TO_DATA.get(CacheStatus.SAVE).get(ft_face);
        final Identifier ttFont = pair.getLeft();
        byte[] data = null;
        try {
            Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(ttFont.withPrefixedPath("font/"));
            if (resource.isPresent()) {
                data = IOHelper.streamToArray(resource.get().getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            FT_Vector vec = FT_Vector.malloc(memoryStack);
            FreeType.FT_Get_Transform(ft_face, null, vec);
            this.shiftX = vec.x();
            this.shiftY = vec.y();
        }

        this.fontData = data;
        this.oversample = fontAccess.getOversample();
        this.excludedCharacters = new ArrayList<>(fontAccess.getExcludedCharacters());
        this.size = pair.getRight();
    }

    @Override
    public TrueTypeFont export(RegistryReader handler) {
        this._font = UnsafeHelper.allocateInstance(TrueTypeFont.class);

        TrueTypeFontAccessor trueTypeFontAccess = (TrueTypeFontAccessor) this._font;
        trueTypeFontAccess.setOversample(this.oversample);
        trueTypeFontAccess.setExcludedCharacters(new IntArraySet(this.excludedCharacters));
        return this._font;
    }

    @Override
    public void postExport(RegistryReader reader) {
        ByteBuffer fontBuffer = MemoryUtil.memAlloc(this.fontData.length);
        fontBuffer.put(this.fontData);
        fontBuffer.flip();

        int size = Math.round(this.size * this.oversample);
        FT_Face ft_face = null;

        var trueTypeFontAccess = (TrueTypeFontAccessor) this._font;

        try {
            synchronized (FreeTypeUtil.LOCK) {
                try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                    PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                    FreeTypeUtil.checkFatalError(FreeType.FT_New_Memory_Face(FreeTypeUtil.initialize(), fontBuffer, 0L, pointerBuffer), "Initializing font face");
                    ft_face = FT_Face.create(pointerBuffer.get());
                }

                FreeType.FT_Set_Pixel_Sizes(ft_face, size, size);
                try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                    FT_Vector vec = FreeTypeUtil.set(FT_Vector.malloc(memoryStack), this.shiftX, this.shiftY);
                    FreeType.FT_Set_Transform(ft_face, null, vec);
                }
            }

            trueTypeFontAccess.setFace(ft_face);
            trueTypeFontAccess.setBuffer(fontBuffer);
        } catch (Throwable e) {
            synchronized (FreeTypeUtil.LOCK) {
                if (ft_face != null) {
                    FreeType.FT_Done_Face(ft_face);
                }
            }
            MemoryUtil.memFree(fontBuffer);

            throw e;
        }
    }
}
