package dev.notalpha.dashloader.api;

import dev.notalpha.dashloader.api.registry.RegistryReader;

/**
 * A DashObject is responsible for making normal objects serializable
 * by mapping them to a more serializable format and deduplicating inner objects through the registry.
 *
 * @param <I> The target object which it's adding support to.
 */
@SuppressWarnings("unused")
public interface DashObject<I, O> {
	/**
	 * Runs before export on the main thread.
	 *
	 * @see DashObject#export(RegistryReader)
	 */
	@SuppressWarnings("unused")
	default void preExport(RegistryReader reader) {
	}

	/**
	 * The export method converts the DashObject into the original counterpart which was provided on save.
	 * <br><br>
	 * Note: This runs in parallel meaning that it does not run on the Main thread. If you need to load things on the main thread use {@link DashObject#postExport(RegistryReader)}
	 */
	@SuppressWarnings("unused")
	O export(RegistryReader reader);

	/**
	 * Runs after export on the main thread.
	 *
	 * @see DashObject#export(RegistryReader)
	 */
	@SuppressWarnings("unused")
	default void postExport(RegistryReader reader) {
	}
}
