package dev.notalpha.dashloader.mixin.accessor;

import dev.quantumfusion.hyphen.thr.HyphenException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelLoader.class)
public interface ModelLoaderAccessor {

    @Accessor("ITEM_FRAME_STATE_FACTORY")
    static StateManager<Block, BlockState> getTheItemFrameThing() {
        throw new HyphenException("froge", "your dad");
    }

    @Accessor("STATIC_DEFINITIONS")
    static Map<Identifier, StateManager<Block, BlockState>> getStaticDefinitions() {
        throw new HyphenException("froge", "your dad");
    }
}
