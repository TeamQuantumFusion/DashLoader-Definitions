package dev.notalpha.dashloader.mixin.accessor;

import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelOverrideList.class)
public interface ModelOverrideListAccessor {
    @Invoker("<init>")
    static ModelOverrideList newModelOverrideList() {
        throw new AssertionError();
    }

    @Accessor
    ModelOverrideList.BakedOverride[] getOverrides();

    @Accessor
    @Mutable
    void setOverrides(ModelOverrideList.BakedOverride[] overrides);

    @Accessor
    Identifier[] getConditionTypes();

    @Accessor
    @Mutable
    void setConditionTypes(Identifier[] conditionTypes);
}
