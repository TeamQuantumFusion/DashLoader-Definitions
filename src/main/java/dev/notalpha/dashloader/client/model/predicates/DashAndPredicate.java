package dev.notalpha.dashloader.client.model.predicates;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.mixin.accessor.AndMultipartModelSelectorAccessor;
import net.minecraft.client.render.model.json.AndMultipartModelSelector;
import net.minecraft.client.render.model.json.MultipartModelSelector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DashAndPredicate implements DashObject<AndMultipartModelSelector, AndMultipartModelSelector> {
	public final int[] selectors;

	public DashAndPredicate(int[] selectors) {
		this.selectors = selectors;
	}

	public DashAndPredicate(AndMultipartModelSelector selector, RegistryWriter writer) {
		AndMultipartModelSelectorAccessor access = ((AndMultipartModelSelectorAccessor) selector);

		Iterable<? extends MultipartModelSelector> accessSelectors = access.getSelectors();
		int count = 0;
		for (MultipartModelSelector ignored : accessSelectors) {
			count += 1;
		}
		this.selectors = new int[count];

		int i = 0;
		for (MultipartModelSelector accessSelector : accessSelectors) {
			this.selectors[i++] = writer.add(accessSelector);
		}
	}

	@Override
	public AndMultipartModelSelector export(RegistryReader handler) {
		final List<MultipartModelSelector> selectors = new ArrayList<>(this.selectors.length);
		for (int accessSelector : this.selectors) {
			selectors.add(handler.get(accessSelector));
		}

		return new AndMultipartModelSelector(selectors);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashAndPredicate that = (DashAndPredicate) o;

		return Arrays.equals(selectors, that.selectors);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(selectors);
	}
}
