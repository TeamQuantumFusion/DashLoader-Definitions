package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.font.FontFilterType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FontFilterType.FilterMap.class)
public interface FilterMapAccessor {
    @Accessor
    Map<FontFilterType, Boolean> getActiveFilters();
}
