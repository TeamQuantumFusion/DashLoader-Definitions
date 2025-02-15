package dev.notalpha.dashloader.client.shader;

import com.google.common.collect.ImmutableMap;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.mixin.accessor.VertexFormatAccessor;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashVertexFormat implements DashObject<VertexFormat, VertexFormat> {
    public static final List<VertexFormat> BUILT_IN = new ArrayList<>();

    static {
        BUILT_IN.add(VertexFormats.BLIT_SCREEN);
        BUILT_IN.add(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        BUILT_IN.add(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL);
        BUILT_IN.add(VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        BUILT_IN.add(VertexFormats.POSITION);
        BUILT_IN.add(VertexFormats.POSITION_COLOR);
        BUILT_IN.add(VertexFormats.LINES);
        BUILT_IN.add(VertexFormats.POSITION_COLOR_LIGHT);
        BUILT_IN.add(VertexFormats.POSITION_TEXTURE);
        BUILT_IN.add(VertexFormats.POSITION_COLOR_TEXTURE);
        BUILT_IN.add(VertexFormats.POSITION_TEXTURE_COLOR);
        BUILT_IN.add(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT);
        BUILT_IN.add(VertexFormats.POSITION_TEXTURE_LIGHT_COLOR);
        BUILT_IN.add(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
    }

    @DataNullable
    public final Map<String, DashVertexFormatElement> elementMap;

    public final int builtin;

    public DashVertexFormat(Map<String, DashVertexFormatElement> elementMap, int builtin) {
        this.elementMap = elementMap;
        this.builtin = builtin;
    }

    public DashVertexFormat(VertexFormat vertexFormat) {
        int builtin = -1;
        for (int i = 0; i < BUILT_IN.size(); i++) {
            VertexFormat format = BUILT_IN.get(i);
            if (format == vertexFormat) {
                builtin = i;
                break;
            }
        }
        this.builtin = builtin;
        if (builtin == -1) {
            this.elementMap = new HashMap<>();
            ((VertexFormatAccessor) vertexFormat).getElementMap().forEach((s, element) -> {
                this.elementMap.put(s, new DashVertexFormatElement(element));
            });
        } else {
            this.elementMap = null;
        }
    }

    @Override
    public VertexFormat export(RegistryReader reader) {
        if (this.builtin != -1) {
            return BUILT_IN.get(this.builtin);
        } else {
            ImmutableMap.Builder<String, VertexFormatElement> out = ImmutableMap.builderWithExpectedSize(elementMap.size());
            elementMap.forEach((s, dashVertexFormatElement) -> {
                VertexFormatElement export = dashVertexFormatElement.export(reader);
                out.put(s, export);
            });
            return new VertexFormat(out.build());
        }
    }
}
