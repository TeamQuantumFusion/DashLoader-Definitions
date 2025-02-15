package dev.notalpha.dashloader.client.shader;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.hyphen.scan.annotations.DataNullable;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.client.render.VertexFormats;

import java.util.ArrayList;
import java.util.List;

public class DashVertexFormatElement implements DashObject<VertexFormatElement, VertexFormatElement> {
    public static final List<VertexFormatElement> BUILT_IN = new ArrayList<>();

    static {
        BUILT_IN.add(VertexFormats.POSITION_ELEMENT);
        BUILT_IN.add(VertexFormats.COLOR_ELEMENT);
        BUILT_IN.add(VertexFormats.TEXTURE_ELEMENT);
        BUILT_IN.add(VertexFormats.OVERLAY_ELEMENT);
        BUILT_IN.add(VertexFormats.LIGHT_ELEMENT);
        BUILT_IN.add(VertexFormats.NORMAL_ELEMENT);
        BUILT_IN.add(VertexFormats.PADDING_ELEMENT);
        BUILT_IN.add(VertexFormats.UV_ELEMENT);
    }

    @DataNullable
    public final DashVertexFormatElementData data;

    public final int builtin;

    public DashVertexFormatElement(@DataNullable DashVertexFormatElementData data, int builtin) {
        this.data = data;
        this.builtin = builtin;
    }

    public DashVertexFormatElement(VertexFormatElement element) {
        var builtin = -1;
        for (int i = 0; i < BUILT_IN.size(); i++) {
            if (BUILT_IN.get(i) == element) {
                builtin = i;
                break;
            }
        }
        this.data = builtin == -1 ? new DashVertexFormatElementData(element) : null;
        this.builtin = builtin;
    }


    @Override
    public VertexFormatElement export(RegistryReader reader) {
        if (this.builtin != -1) {
            return BUILT_IN.get(this.builtin);
        } else {
            return new VertexFormatElement(this.data.uvIndex, this.data.componentType, this.data.type, this.data.componentCount);
        }
    }

    public static class DashVertexFormatElementData {
        public final VertexFormatElement.ComponentType componentType;
        public final VertexFormatElement.Type type;
        public final int uvIndex;
        public final int componentCount;

        public DashVertexFormatElementData(VertexFormatElement.ComponentType componentType, VertexFormatElement.Type type, int uvIndex, int componentCount) {
            this.componentType = componentType;
            this.type = type;
            this.uvIndex = uvIndex;
            this.componentCount = componentCount;
        }

        public DashVertexFormatElementData(VertexFormatElement element) {
            this.componentType = element.getComponentType();
            this.type = element.getType();
            this.uvIndex = element.getUvIndex();
            this.componentCount = element.getComponentCount();
        }
    }
}
