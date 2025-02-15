package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.json.SimpleMultipartModelSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleMultipartModelSelector.class)
public interface SimpleMultipartModelSelectorAccessor {

    @Accessor
    String getKey();

    @Accessor
    String getValueString();

}
