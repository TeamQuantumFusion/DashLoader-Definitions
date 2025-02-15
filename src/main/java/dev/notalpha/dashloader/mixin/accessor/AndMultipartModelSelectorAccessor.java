package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AndMultipartModelSelector.class)
public interface AndMultipartModelSelectorAccessor {

    @Accessor
    Iterable<? extends MultipartModelSelector> getSelectors();

}
