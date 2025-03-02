package dev.notalpha.dashloader.api.registry;

/**
 * A RegistryWriter is provided to DashObjects and Modules on save minecraft objects to the cache by converting them into DashObjects.
 * On cache load, a RegistryReader is provided so you can read back the objects from the cache.
 *
 * @see RegistryReader
 */
public interface RegistryWriter {
	/**
	 * Adds an object to the Cache, the object needs to have a DashObject backing it else it will fail.
	 *
	 * @param object The Object to add to the cache.
	 * @param <R>    The target class being cached.
	 * @return A registry id which points to the object.
	 * @see RegistryReader#get(int)
	 */
	<R> int add(R object);
}
