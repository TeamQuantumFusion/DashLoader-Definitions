package dev.notalpha.dashloader.io.data.fragment;

import dev.notalpha.dashloader.io.fragment.Fragment;

import java.util.ArrayList;
import java.util.List;

public class StageFragment {
	public final List<ChunkFragment> chunks;
	public final FragmentSlice info;

	public StageFragment(List<ChunkFragment> chunks, FragmentSlice info) {
		this.chunks = chunks;
		this.info = info;
	}

	public StageFragment(Fragment fragment) {
		this.info = new FragmentSlice(fragment);
		this.chunks = new ArrayList<>();
		for (Fragment inner : fragment.inner) {
			this.chunks.add(new ChunkFragment(inner));
		}
	}
}
