package dev.notalpha.dashloader.io.fragment;

import java.util.List;

public class Fragment {
	public final long size;
	public final int startIndex;
	public final int endIndex;
	public final List<Fragment> inner;

	public Fragment(long size, int startIndex, int endIndex, List<Fragment> inner) {
		this.size = size;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.inner = inner;
	}

	@Override
	public String toString() {
		return "Fragment{" +
				"size=" + size +
				", startIndex=" + startIndex +
				", endIndex=" + endIndex +
				", inner=" + inner +
				'}';
	}
}
