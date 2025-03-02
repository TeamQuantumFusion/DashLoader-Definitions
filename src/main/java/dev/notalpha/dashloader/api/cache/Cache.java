package dev.notalpha.dashloader.api.cache;

import dev.notalpha.taski.builtin.StepTask;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The Cache is responsible for managing, saving and loading caches from its assigned directory.
 *
 * @see CacheFactory
 */
public interface Cache {
	/**
	 * Attempt to load the DashLoader cache with the current name if it exists,
	 * else it will set the cache into SAVE status and reset managers to be ready for caching.
	 *
	 * @param name The cache name which will be used.
	 */
	void load(String name);

	/**
	 * Create and save a cache from the modules which are currently enabled.
	 *
	 * @param taskConsumer An optional task function which allows you to track the progress.
	 * @return If the cache creation was successful
	 */
	boolean save(@Nullable Consumer<StepTask> taskConsumer);

	/**
	 * Resets the cache into an IDLE state where it resets the cache storages to save memory.
	 */
	void reset();

	/**
	 * Remove the existing cache if it exists.
	 */
	void remove();

	/**
	 * Gets the current status or state of the Cache.
	 */
	CacheStatus getStatus();

	/**
	 * Gets the current directory of the cache.
	 *
	 * @return Path to the cache directory which contains the data.
	 */
	Path getDir();
}
