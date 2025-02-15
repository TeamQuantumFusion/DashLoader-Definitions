package dev.notalpha.dashloader.io.data;


import dev.notalpha.dashloader.io.data.fragment.CacheFragment;

import java.util.List;

public class CacheInfo {
    /**
     * Information about the different file fragments the cache contains.
     */
    public final List<CacheFragment> fragments;
    /**
     * Information about the output chunks.
     */
    public final List<ChunkInfo> chunks;

    /**
     * A two dimensional array containing the sizes of the stages and chunks.
     * The first index is the stage index which will yield an array of the chunk sizes,
     * The size of this array is the amount of chunks in that stage.
     */
    public final int[][] stageSizes;

    public CacheInfo(List<CacheFragment> fragments, List<ChunkInfo> chunks, int[][] stageSizes) {
        this.fragments = fragments;
        this.chunks = chunks;
        this.stageSizes = stageSizes;
    }
}
