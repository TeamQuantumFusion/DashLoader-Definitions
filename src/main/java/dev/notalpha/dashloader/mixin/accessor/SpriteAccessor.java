package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sprite.class)
public interface SpriteAccessor {

    @Invoker("<init>")
    static Sprite init(Identifier atlasId, SpriteContents contents, int atlasWidth, int atlasHeight, int width, int height) {
        throw new AssertionError();
    }
}
