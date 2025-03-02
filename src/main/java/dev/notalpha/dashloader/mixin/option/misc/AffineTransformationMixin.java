package dev.notalpha.dashloader.mixin.option.misc;

import net.minecraft.util.math.AffineTransformation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(value = AffineTransformation.class, priority = 999)
public class AffineTransformationMixin {
    @Shadow
    @Final
    private Matrix4f matrix;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AffineTransformationMixin that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(matrix, that.matrix);
    }

    @Override
    public int hashCode() {
        return matrix.hashCode();
    }
}
