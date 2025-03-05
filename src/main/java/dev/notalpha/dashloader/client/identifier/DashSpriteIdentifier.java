package dev.notalpha.dashloader.client.identifier;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import net.minecraft.client.util.SpriteIdentifier;

public class DashSpriteIdentifier implements DashObject<SpriteIdentifier, SpriteIdentifier> {
	public final int atlas;
	public final int texture;

	public DashSpriteIdentifier(int atlas, int texture) {
		this.atlas = atlas;
		this.texture = texture;
	}

	public DashSpriteIdentifier(SpriteIdentifier identifier, RegistryWriter writer) {
		this.atlas = writer.add(identifier.getAtlasId());
		this.texture = writer.add(identifier.getTextureId());
	}

	@Override
	public SpriteIdentifier export(RegistryReader reader) {
		return new SpriteIdentifier(reader.get(atlas), reader.get(texture));
	}
}
