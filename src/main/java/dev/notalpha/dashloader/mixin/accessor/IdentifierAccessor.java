package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Identifier.class)
public interface IdentifierAccessor {
    @Invoker("<init>")
    static Identifier init(String namespace, String path, @Nullable Identifier.ExtraData extraData) {
        throw new AssertionError();
    }
}
