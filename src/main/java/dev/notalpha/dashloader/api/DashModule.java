package dev.notalpha.dashloader.api;

import dev.notalpha.dashloader.api.cache.Cache;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.taski.builtin.StepTask;

/**
 * A DashModule is a manager of data in a Cache.
 * It's responsible for providing and consuming objects from/to the registry and saving the resulting id's and/or other data into the data class.
 * <br><br>
 * These may conditionally be disabled by {@link DashModule#isActive()}.
 *
 * @param <D> The Data class which will be saved
 */
public interface DashModule<D> {
	/**
	 * This runs when the module gets reset by dashloader.
	 * This is used to reset CachingData instances to their correct state.
	 *
	 * @param cache The cache object which is resetting.
	 */
	void reset(Cache cache);

	/**
	 * Runs when DashLoader is creating a save.
	 * This should fill the RegistryFactory with objects that it wants available on next load.
	 *
	 * @param writer RegistryWriter to provide objects to.
	 * @param task   Task to track progress of the saving.
	 * @return The DataObject which will be saved for next load.
	 */
	D save(RegistryWriter writer, StepTask task);

	/**
	 * Runs when DashLoader is loading back a save.
	 * This should read back the objects from the RegistryReading with the ids commonly saved in the DataObject.
	 *
	 * @param data   DataObject which got saved in {@link DashModule#save(RegistryWriter, StepTask)}
	 * @param reader RegistryReader which contains objects which got cached.
	 * @param task   Task to track progress of the loading.
	 */
	void load(D data, RegistryReader reader, StepTask task);

	/**
	 * Gets the DataClass which the module uses to save data for the cache load.
	 */
	Class<D> getDataClass();

	/**
	 * Returns if the module is currently active.
	 * <br><br>
	 * When saving, if the module is active it will run the save method and then save the data object to the cache.
	 * <br><br>
	 * When loading back the cache. If the cache did not have the module in the same state as now, it will force a recache.
	 */
	default boolean isActive() {
		return true;
	}

	/**
	 * The weight of the module in the progress task.
	 * The bigger the value the more space the module will use in the progress.
	 */
	default float taskWeight() {
		return 100;
	}
}
