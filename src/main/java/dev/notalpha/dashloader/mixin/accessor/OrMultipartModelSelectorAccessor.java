package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.client.render.model.json.OrMultipartModelSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OrMultipartModelSelector.class)
public interface OrMultipartModelSelectorAccessor {

    @Accessor
    Iterable<? extends MultipartModelSelector> getSelectors();

}
