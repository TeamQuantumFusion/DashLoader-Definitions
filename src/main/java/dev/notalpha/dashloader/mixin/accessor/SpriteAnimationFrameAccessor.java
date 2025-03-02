package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteContents.AnimationFrame.class)
public interface SpriteAnimationFrameAccessor {
	@Invoker("<init>")
	static SpriteContents.AnimationFrame newSpriteFrame(int index, int time) {
		throw new AssertionError();
	}

	@Accessor
	int getIndex();

	@Accessor
	int getTime();
}
