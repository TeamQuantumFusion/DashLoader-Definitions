package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(SpriteContents.Animation.class)
public interface SpriteAnimationAccessor {


    @Invoker("<init>")
    static SpriteContents.Animation init(SpriteContents parent, List<SpriteContents.AnimationFrame> frames, int frameCount, boolean interpolation) {
        throw new AssertionError();
    }

    @Accessor
    List<SpriteContents.AnimationFrame> getFrames();

    @Accessor
    int getFrameCount();

    @Accessor
    boolean getInterpolation();

}
