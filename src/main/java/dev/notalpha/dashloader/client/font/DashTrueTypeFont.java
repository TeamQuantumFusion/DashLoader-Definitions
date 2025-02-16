package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.cache.CacheStatus;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.io.IOHelper;
import dev.notalpha.dashloader.misc.UnsafeHelper;
import dev.notalpha.dashloader.mixin.accessor.TrueTypeFontAccessor;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TrueTypeFont;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DashTrueTypeFont implements DashObject<TrueTypeFont, TrueTypeFont> {
	public final byte[] ttfBuffer;
	public final float oversample;
	public final List<Integer> excludedCharacters;

    public DashTrueTypeFont(byte[] ttfBuffer, float oversample, List<Integer> excludedCharacters) {
		this.ttfBuffer = ttfBuffer;
		this.oversample = oversample;
		this.excludedCharacters = excludedCharacters;
	}

    public DashTrueTypeFont(TrueTypeFont font) {
        TrueTypeFontAccessor fontAccess = (TrueTypeFontAccessor) font;
        final Identifier ttFont = FontModule.FONT_TO_IDENT.get(CacheStatus.SAVE).get(fontAccess.getFace());
		byte[] data = null;
		try {
			Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(ttFont.getNamespace(), "font/" + ttFont.getPath()));
			if (resource.isPresent()) {
				data = IOHelper.streamToArray(resource.get().getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.ttfBuffer = data;
		this.oversample = fontAccess.getOversample();
		this.excludedCharacters = new ArrayList<>(fontAccess.getExcludedCharacters());
	}

    @Override
    public TrueTypeFont export(RegistryReader handler) {
        ByteBuffer byteBuffer2 = MemoryUtil.memAlloc(this.ttfBuffer.length);
		byteBuffer2.put(this.ttfBuffer);
		byteBuffer2.flip();
		FT_Face ft_face = new FT_Face(byteBuffer2);

        TrueTypeFont trueTypeFont = UnsafeHelper.allocateInstance(TrueTypeFont.class);
        TrueTypeFontAccessor trueTypeFontAccess = (TrueTypeFontAccessor) trueTypeFont;
        trueTypeFontAccess.setFace(ft_face);
		trueTypeFontAccess.setOversample(this.oversample);
		trueTypeFontAccess.setBuffer(byteBuffer2);
		trueTypeFontAccess.setExcludedCharacters(new IntArraySet(this.excludedCharacters));
        return trueTypeFont;
    }
}
