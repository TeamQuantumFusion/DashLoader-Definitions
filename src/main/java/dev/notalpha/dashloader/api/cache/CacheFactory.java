package dev.notalpha.dashloader.api.cache;

import dev.notalpha.dashloader.CacheFactoryImpl;
import dev.notalpha.dashloader.api.DashModule;
import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryWriter;

import java.nio.file.Path;
import java.util.function.BiFunction;

/**
 * The CacheFactory is used to construct a {@link Cache}
 */
public interface CacheFactory {
	/**
	 * Creates a new Factory
	 *
	 * @return CacheFactory
	 */
	static CacheFactory create() {
		return new CacheFactoryImpl();
	}

	/**
	 * Adds a DashObject to the Cache, this will allow the Cache to cache the DashObject's target.
	 *
	 * @param dashClass The class
	 */
	void addDashObject(Class<? extends DashObject<?, ?>> dashClass);

	/**
	 * Adds a module to the Cache. Please note only enabled Modules will actually be cached.
	 */
	void addModule(DashModule<?> module);

	/**
	 * Adds a missing handler to the Cache, a missing handler is used when an Object does not have a DashObject directly bound to it.
	 * The registry will go through every missing handler until it finds one which does not return {@code null}.
	 *
	 * @param rClass The class which the object needs to implement.
	 *               If you want to go through any object you can insert {@code Object.class} because every java object inherits this.
	 * @param func   The consumer function for an object which fits the {@code rClass}.
	 *               If this function returns a non-null value, it will use that DashObject for serialization of that object.
	 * @param <R>    The super class of the objects being missed.
	 */
	<R> void addMissingHandler(Class<R> rClass, BiFunction<R, RegistryWriter, DashObject<? extends R, ?>> func);

	/**
	 * Builds the cache object.
	 *
	 * @param path The directory which contains the caches.
	 * @return A DashLoader cache object.
	 */
	Cache build(Path path);
}
