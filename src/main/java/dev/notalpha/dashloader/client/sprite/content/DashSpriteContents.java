package dev.notalpha.dashloader.client.sprite.content;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.misc.UnsafeHelper;
import dev.notalpha.dashloader.mixin.accessor.SpriteContentsAccessor;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class DashSpriteContents implements DashObject<SpriteContents, SpriteContents> {
    public final int id;
    public final int image;
    @Nullable
    @DataNullable
    public final DashSpriteAnimation animation;

    public final int width;
    public final int height;

    public DashSpriteContents(int id, int image, @Nullable DashSpriteAnimation animation, int width, int height) {
        this.id = id;
        this.image = image;
        this.animation = animation;
        this.width = width;
        this.height = height;
    }

    public DashSpriteContents(SpriteContents contents, RegistryWriter writer) {
        var access = (SpriteContentsAccessor) contents;
        this.id = writer.add(contents.getId());
        this.image = writer.add(access.getImage());
        this.width = contents.getWidth();
        this.height = contents.getHeight();
        SpriteContents.Animation animation = access.getAnimation();
        this.animation = animation == null ? null : new DashSpriteAnimation(animation);
    }

    public SpriteContents export(RegistryReader reader) {
        final SpriteContents out = UnsafeHelper.allocateInstance(SpriteContents.class);
        var access = (SpriteContentsAccessor) out;
        access.setId(reader.get(this.id));

        NativeImage image = reader.get(this.image);
        access.setImage(image);
        access.setHeight(height);
        access.setWidth(width);
        access.setMipmapLevelsImages(new NativeImage[]{image});
        access.setAnimation(this.animation == null ? null : animation.export(out, reader));
        return out;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashSpriteContents that = (DashSpriteContents) o;

        if (id != that.id) return false;
        if (image != that.image) return false;
        if (width != that.width) return false;
        if (height != that.height) return false;
        return Objects.equals(animation, that.animation);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + image;
        result = 31 * result + (animation != null ? animation.hashCode() : 0);
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
