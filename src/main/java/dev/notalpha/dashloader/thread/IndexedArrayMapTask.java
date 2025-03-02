package dev.notalpha.dashloader.thread;

import dev.notalpha.dashloader.registry.data.ChunkData;

import java.util.concurrent.RecursiveAction;
import java.util.function.Function;

public final class IndexedArrayMapTask<I, O> extends RecursiveAction {
	private final int threshold;
	private final int start;
	private final int stop;
	private final ChunkData.Entry<I>[] inArray;
	private final O[] outArray;
	private final Function<I, O> function;

	private IndexedArrayMapTask(ChunkData.Entry<I>[] inArray, O[] outArray, Function<I, O> function, int threshold, int start, int stop) {
		this.threshold = threshold;
		this.start = start;
		this.stop = stop;
		this.inArray = inArray;
		this.outArray = outArray;
		this.function = function;
	}

	public IndexedArrayMapTask(ChunkData.Entry<I>[] inArray, O[] outArray, Function<I, O> function) {
		this.start = 0;
		this.stop = inArray.length;
		this.threshold = ThreadHandler.calcThreshold(this.stop);
		this.inArray = inArray;
		this.outArray = outArray;
		this.function = function;
	}

	@Override
	protected void compute() {
		final int size = this.stop - this.start;
		if (size < this.threshold) {
			for (int i = this.start; i < this.stop; i++) {
				var entry = this.inArray[i];
				this.outArray[entry.pos] = this.function.apply(entry.data);
			}
		} else {
			final int middle = this.start + (size / 2);
			invokeAll(new IndexedArrayMapTask<>(this.inArray, this.outArray, this.function, this.threshold, this.start, middle),
					new IndexedArrayMapTask<>(this.inArray, this.outArray, this.function, this.threshold, middle, this.stop));
		}
	}
}
