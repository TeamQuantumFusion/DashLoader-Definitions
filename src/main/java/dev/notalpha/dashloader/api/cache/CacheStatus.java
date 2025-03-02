package dev.notalpha.dashloader.api.cache;

/**
 * Status/State values for a given Cache.
 */
public enum CacheStatus {
	/**
	 * The cache is in an IDLE state where there are no temporary resources in memory.
	 */
	IDLE,
	/**
	 * The Cache is loading back an existing cache from a file.
	 */
	LOAD,
	/**
	 * The Cache is trying to create/save a cache.
	 */
	SAVE,
}
