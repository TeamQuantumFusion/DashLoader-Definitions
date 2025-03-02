package dev.notalpha.dashloader.api.registry;

/**
 * The RegistryReader is used to read objects from the cache's registry.
 *
 * @see RegistryWriter
 */
public interface RegistryReader {
	/**
	 * Gets an object from the Cache.
	 *
	 * @param pointer The registry pointer to the object.
	 * @param <R>     Target object class.
	 * @return The object that got cached.
	 * @see RegistryWriter#add(Object)
	 */
	<R> R get(final int pointer);
}
