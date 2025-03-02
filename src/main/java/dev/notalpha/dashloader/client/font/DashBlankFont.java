package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import net.minecraft.client.font.BlankFont;

public final class DashBlankFont implements DashObject<BlankFont, BlankFont> {
	@Override
	public BlankFont export(RegistryReader exportHandler) {
		return new BlankFont();
	}
}
