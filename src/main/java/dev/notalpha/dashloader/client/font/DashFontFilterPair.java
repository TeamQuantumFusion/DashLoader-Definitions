package dev.notalpha.dashloader.client.font;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.collection.IntIntList;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.mixin.accessor.FilterMapAccessor;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontFilterType;

import java.util.HashMap;
import java.util.Map;

public class DashFontFilterPair implements DashObject<Font.FontFilterPair, Font.FontFilterPair> {
    public final int provider;
    public final IntIntList filter;

    public DashFontFilterPair(int provider, IntIntList filter) {
        this.provider = provider;
        this.filter = filter;
    }

    public DashFontFilterPair(Font.FontFilterPair fontFilterPair, RegistryWriter writer) {
        this.provider = writer.add(fontFilterPair.provider());

        filter = new IntIntList();
        ((FilterMapAccessor) fontFilterPair.filter()).getActiveFilters().forEach(
            (key, value) -> {
                filter.put(key.ordinal(), value ? 1 : 0);
            });
    }

    @Override
    public Font.FontFilterPair export(RegistryReader reader) {
        Map<FontFilterType, Boolean> activeFilters = new HashMap<>();
        filter.forEach((key, value) -> {
            activeFilters.put(FontFilterType.values()[key], value == 1);
        });
        return new Font.FontFilterPair(reader.get(provider), new FontFilterType.FilterMap(activeFilters));
    }
}
