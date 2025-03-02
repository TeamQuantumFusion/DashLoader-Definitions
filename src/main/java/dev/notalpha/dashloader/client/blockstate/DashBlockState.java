package dev.notalpha.dashloader.client.blockstate;

import dev.notalpha.dashloader.api.DashObject;
import dev.notalpha.dashloader.api.registry.RegistryReader;
import dev.notalpha.dashloader.api.registry.RegistryWriter;
import dev.notalpha.dashloader.mixin.accessor.ModelLoaderAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class DashBlockState implements DashObject<BlockState, BlockState> {
	public static final Identifier ITEM_FRAME = new Identifier("dashloader:itemframewhy");
	public final int owner;
	public final int pos;

	public DashBlockState(int owner, int pos) {
		this.owner = owner;
		this.pos = pos;
	}

	public DashBlockState(BlockState blockState, RegistryWriter writer) {
		var block = blockState.getBlock();
		int pos = -1;

		Identifier owner = null;
		{
			var states = ModelLoaderAccessor.getTheItemFrameThing().getStates();
			for (int i = 0; i < states.size(); i++) {
				BlockState state = states.get(i);
				if (state.equals(blockState)) {
					pos = i;
					owner = ITEM_FRAME;
					break;
				}
			}
		}

		if (pos == -1) {
			var states = block.getStateManager().getStates();
			for (int i = 0; i < states.size(); i++) {
				BlockState state = states.get(i);
				if (state.equals(blockState)) {
					pos = i;
					owner = Registries.BLOCK.getId(block);
					break;
				}
			}
		}

		if (owner == null) {
			throw new RuntimeException("Could not find a blockstate for " + blockState);
		}

		this.owner = writer.add(owner);
		this.pos = pos;
	}

	@Override
	public BlockState export(final RegistryReader reader) {
		final Identifier id = reader.get(this.owner);
		// if its item frame get its state from the model loader as mojank is mojank
		if (id.equals(ITEM_FRAME)) {
			return ModelLoaderAccessor.getTheItemFrameThing().getStates().get(this.pos);
		} else {
			return Registries.BLOCK.get(id).getStateManager().getStates().get(this.pos);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DashBlockState that = (DashBlockState) o;

		if (owner != that.owner) return false;
		return pos == that.pos;
	}

	@Override
	public int hashCode() {
		int result = owner;
		result = 31 * result + pos;
		return result;
	}
}
