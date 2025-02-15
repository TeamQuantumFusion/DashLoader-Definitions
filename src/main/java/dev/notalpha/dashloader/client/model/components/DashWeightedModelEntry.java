package dev.notalpha.dashloader.client.model.components;

import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.client.model.DashWeightedBakedModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.collection.Weighted;

public final class DashWeightedModelEntry {
    public final int model;
    public final int weight;

    public DashWeightedModelEntry(int model, int weight) {
        this.model = model;
        this.weight = weight;
    }

    public DashWeightedModelEntry(Weighted.Present<BakedModel> entry, RegistryWriter writer) {
        this(writer.add(entry.getData()), entry.getWeight().getValue());
    }


    public DashWeightedBakedModel.DazyImpl.Entry export(RegistryReader handler) {
        return new DashWeightedBakedModel.DazyImpl.Entry(this.weight, handler.get(this.model));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DashWeightedModelEntry that = (DashWeightedModelEntry) o;

        if (model != that.model) return false;
        return weight == that.weight;
    }

    @Override
    public int hashCode() {
        int result = model;
        result = 31 * result + weight;
        return result;
    }
}
