package dev.notalpha.dashloader.io.fragment;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
	public final long size;
	int elementPos = 0;

	protected Piece(long size) {
		this.size = size;
	}

	public abstract Piece[] getInner();

	public boolean isDone() {
		Piece[] inner = this.getInner();
		return inner == null || !(elementPos < inner.length);
	}

	public Fragment fragment(long sizeRemaining) {
		Piece[] inner = this.getInner();
		if (inner == null) {
			throw new RuntimeException("Non splitting piece requested fragmentation");
		} else {
			int rangeStart = elementPos;
			long currentSize = 0;

			List<Fragment> innerOut = new ArrayList<>();
			int rangeEnd = 0;
			// Add until we reach the intended size, or we hit the last element.
			while ((currentSize < sizeRemaining) && elementPos < inner.length) {
				var piece = inner[elementPos];
				rangeEnd = elementPos + 1;
				if (piece.getInner() == null) {
					currentSize += piece.size;
					elementPos += 1;
				} else {
					Fragment fragment = piece.fragment(sizeRemaining);
					innerOut.add(fragment);
					currentSize += fragment.size;
					if (piece.isDone()) {
						elementPos += 1;
					}
				}
			}
			return new Fragment(currentSize, rangeStart, rangeEnd, innerOut);
		}
	}
}
