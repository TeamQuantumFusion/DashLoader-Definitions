package dev.notalpha.dashloader.io.fragment;

import java.util.Arrays;

public class SimplePiece extends Piece {
	public final Piece[] value;

	public SimplePiece(Piece[] value) {
		super(Arrays.stream(value).mapToLong(dEntry -> dEntry.size).sum());
		this.value = value;
	}

	@Override
	public Piece[] getInner() {
		return value;
	}

	@Override
	public String toString() {
		return Arrays.toString(value);
	}
}
