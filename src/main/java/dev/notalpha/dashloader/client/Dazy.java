package dev.notalpha.dashloader.client;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

// its lazy, but dash! Used for resolution of sprites.
public abstract class Dazy<V> {
    @Nullable
    private transient V loaded;

    protected abstract V resolve(Function<SpriteIdentifier, Sprite> spriteLoader);

    public V get(Function<SpriteIdentifier, Sprite> spriteLoader) {
        if (loaded != null) {
            return loaded;
        }

        loaded = resolve(spriteLoader);
        return loaded;
    }
}
