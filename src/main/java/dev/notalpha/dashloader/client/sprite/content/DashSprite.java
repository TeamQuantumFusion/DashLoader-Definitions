package dev.notalpha.dashloader.client.sprite.content;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.Dazy;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;

import java.util.function.Function;

public class DashSprite implements DashObject<Sprite, DashSprite.DazyImpl> {
    public final int id;

    public DashSprite(int id) {
        this.id = id;
    }

    public DashSprite(Sprite sprite, RegistryWriter writer) {
        this.id = writer.add(new SpriteIdentifier(sprite.getAtlasId(), sprite.getContents().getId()));
    }

    @Override
    public DazyImpl export(final RegistryReader registry) {
        return new DazyImpl(registry.get(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashSprite that = (DashSprite) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static class DazyImpl extends Dazy<Sprite> {
        public final SpriteIdentifier location;

        public DazyImpl(SpriteIdentifier location) {
            this.location = location;
        }

        @Override
        protected Sprite resolve(Function<SpriteIdentifier, Sprite> spriteLoader) {
            return spriteLoader.apply(location);
        }
    }
}
