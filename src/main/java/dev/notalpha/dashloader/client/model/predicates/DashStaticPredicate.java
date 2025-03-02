package dev.notalpha.dashloader.client.model.predicates;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;

public final class DashStaticPredicate implements DashObject<BooleanSelector, BooleanSelector> {
	public final boolean value;

	public DashStaticPredicate(boolean value) {
		this.value = value;
	}

	public DashStaticPredicate(BooleanSelector multipartModelSelector) {
		this.value = multipartModelSelector.selector;
	}

	@Override
	public BooleanSelector export(RegistryReader exportHandler) {
		return new BooleanSelector(value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashStaticPredicate that = (DashStaticPredicate) o;

		return value == that.value;
	}

	@Override
	public int hashCode() {
		return (value ? 1 : 0);
	}
}
