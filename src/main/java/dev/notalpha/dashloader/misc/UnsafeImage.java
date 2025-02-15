package dev.notalpha.dashloader.misc;

import dev.notalpha.dashloader.mixin.accessor.NativeImageAccessor;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.system.MemoryUtil;

public final class UnsafeImage {
    public final NativeImage image;
    public final int width;
    public final int height;
    public final long pointer;

    public UnsafeImage(NativeImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pointer = ((NativeImageAccessor) (Object) image).getPointer();
    }

    public int get(int x, int y) {
        return MemoryUtil.memGetInt(this.pointer + ((long) x + (long) y * (long) width) * 4L);
    }

    public void set(int x, int y, int value) {
        MemoryUtil.memPutInt(this.pointer + ((long) x + (long) y * (long) width) * 4L, value);
    }
}
