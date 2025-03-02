package dev.notalpha.dashloader.io.fragment;

public class SizePiece extends Piece {
	public SizePiece(long size) {
		super(size);
	}

	@Override
	public Piece[] getInner() {
		return null;
	}

	@Override
	public String toString() {
		return "";
	}
}
