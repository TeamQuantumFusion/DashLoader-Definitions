package dev.notalpha.dashloader.registry;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryWriter;

import java.util.function.BiFunction;

public class MissingHandler<R> {
	public final Class<R> parentClass;
	public final BiFunction<R, RegistryWriter, DashObject<? extends R, ?>> func;

	public MissingHandler(Class<R> parentClass, BiFunction<R, RegistryWriter, DashObject<? extends R, ?>> func) {
		this.parentClass = parentClass;
		this.func = func;
	}
}
