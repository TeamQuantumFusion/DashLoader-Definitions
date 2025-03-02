package dev.notalpha.dashloader.client.sprite.content;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.mixin.accessor.SpriteAnimationFrameAccessor;
import net.minecraft.client.texture.SpriteContents;

public final class DashSpriteAnimationFrame implements DashObject<SpriteContents.AnimationFrame, SpriteContents.AnimationFrame> {
	public final int index;
	public final int time;

	public DashSpriteAnimationFrame(int index, int time) {
		this.index = index;
		this.time = time;
	}

	public DashSpriteAnimationFrame(SpriteContents.AnimationFrame animationFrame) {
		SpriteAnimationFrameAccessor access = ((SpriteAnimationFrameAccessor) animationFrame);
		this.index = access.getIndex();
		this.time = access.getTime();
	}

	@Override
	public SpriteContents.AnimationFrame export(RegistryReader exportHandler) {
		return SpriteAnimationFrameAccessor.newSpriteFrame(this.index, this.time);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashSpriteAnimationFrame that = (DashSpriteAnimationFrame) o;

		if (index != that.index) return false;
		return time == that.time;
	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + time;
		return result;
	}
}
