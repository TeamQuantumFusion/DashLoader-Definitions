package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelIdentifier.class)
public interface ModelIdentifierAccessor {
	@Invoker("<init>")
	static ModelIdentifier init(String namespace, String path, String variant, @Nullable Identifier.ExtraData extraData) {
		throw new AssertionError();
	}
}
