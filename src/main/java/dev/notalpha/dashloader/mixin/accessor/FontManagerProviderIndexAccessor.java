package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

@Mixin(FontManager.ProviderIndex.class)
public interface FontManagerProviderIndexAccessor {

	@Invoker("<init>")
	static FontManager.ProviderIndex create(Map<Identifier, List<Font.FontFilterPair>> providers, List<Font> allProviders) {
		throw new AssertionError();
	}

}
