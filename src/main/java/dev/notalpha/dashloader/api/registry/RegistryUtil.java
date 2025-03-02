package dev.notalpha.dashloader.api.registry;

/**
 * Contains utilities for handling RegistryIds
 */
public final class RegistryUtil {
	/**
	 * Creates a new registry id.
	 *
	 * @param objectPos The chunk object position.
	 * @param chunkPos  The index to the chunk the object is in.
	 * @return Registry ID
	 */
	public static int createId(int objectPos, byte chunkPos) {
		if (chunkPos > 0b111111) {
			throw new IllegalStateException("Chunk pos is too big. " + chunkPos + " > " + 0x3f);
		}
		if (objectPos > 0x3ffffff) {
			throw new IllegalStateException("Object pos is too big. " + objectPos + " > " + 0x3ffffff);
		}
		return objectPos << 6 | (chunkPos & 0x3f);
	}

	/**
	 * Gets the chunk id portion of the Registry ID
	 *
	 * @param id Registry ID
	 * @return Chunk index.
	 */
	public static byte getChunkId(int id) {
		return (byte) (id & 0x3f);
	}

	/**
	 * Gets the object id portion of the Registry ID
	 *
	 * @param id Registry ID
	 * @return The index of the object in the chunk.
	 */
	public static int getObjectId(int id) {
		return id >>> 6;
	}
}
