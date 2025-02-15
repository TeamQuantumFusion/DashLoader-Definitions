package dev.notalpha.dashloader.mixin.option;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.WallShape;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin extends Block implements Waterloggable {
    private static final int LENGTH = WallShape.values().length;
    @Shadow
    @Final
    public static BooleanProperty UP;
    @Shadow
    @Final
    public static EnumProperty<WallShape> EAST_SHAPE;
    @Shadow
    @Final
    public static EnumProperty<WallShape> NORTH_SHAPE;
    @Shadow
    @Final
    public static EnumProperty<WallShape> WEST_SHAPE;
    @Shadow
    @Final
    public static EnumProperty<WallShape> SOUTH_SHAPE;
    @Shadow
    @Final
    public static BooleanProperty WATERLOGGED;
    private static VoxelShape[][][][][] SHAPE_CACHE;
    private static VoxelShape[][][][][] COLLISION_CACHE;

    public WallBlockMixin(Settings settings) {
        super(settings);
    }


    @Inject(method = "getShapeMap", at = @At(value = "HEAD"), cancellable = true)
    private void getShapeMapCache(float f, float g, float h, float i, float j, float k, CallbackInfoReturnable<Map<BlockState, VoxelShape>> cir) {
        if (this.isCommon(f, g, i)) {
            if (this.isShape(h, j, k)) {
                if (SHAPE_CACHE != null) {
                    cir.setReturnValue(this.createFromCache(SHAPE_CACHE));
                }
            } else if (this.isCollision(h, j, k)) {
                if (COLLISION_CACHE != null) {
                    cir.setReturnValue(this.createFromCache(COLLISION_CACHE));
                }
            }
        }
    }

    @Inject(method = "getShapeMap", at = @At(value = "RETURN"))
    private void getShapeMapCacheCreate(float f, float g, float h, float i, float j, float k, CallbackInfoReturnable<Map<BlockState, VoxelShape>> cir) {
        if (SHAPE_CACHE == null || COLLISION_CACHE == null) {
            if (this.isCommon(f, g, i)) {
                if (this.isShape(h, j, k)) {
                    if (SHAPE_CACHE == null) {
                        SHAPE_CACHE = new VoxelShape[2][LENGTH][LENGTH][LENGTH][LENGTH];
                        this.createCache(SHAPE_CACHE, cir.getReturnValue());
                    }
                } else if (this.isCollision(h, j, k)) {
                    if (COLLISION_CACHE == null) {
                        COLLISION_CACHE = new VoxelShape[2][LENGTH][LENGTH][LENGTH][LENGTH];
                        this.createCache(COLLISION_CACHE, cir.getReturnValue());
                    }
                }
            }
        }
    }

    private ImmutableMap<BlockState, VoxelShape> createFromCache(VoxelShape[][][][][] rawCache) {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();
        for (Boolean up : UP.getValues()) {
            VoxelShape[][][][] cache = up ? rawCache[1] : rawCache[0];
            for (WallShape east : EAST_SHAPE.getValues()) {
                for (WallShape north : NORTH_SHAPE.getValues()) {
                    for (WallShape west : WEST_SHAPE.getValues()) {
                        for (WallShape south : SOUTH_SHAPE.getValues()) {
                            final VoxelShape cached = this.getCached(cache, east, north, west, south);

                            BlockState blockState = this.getDefaultState()
                                .with(UP, up)
                                .with(EAST_SHAPE, east)
                                .with(WEST_SHAPE, west)
                                .with(NORTH_SHAPE, north)
                                .with(SOUTH_SHAPE, south);

                            builder.put(blockState.with(WATERLOGGED, false), cached);
                            builder.put(blockState.with(WATERLOGGED, true), cached);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    private void createCache(VoxelShape[][][][][] rawCache, Map<BlockState, VoxelShape> map) {
        for (Boolean up : UP.getValues()) {
            VoxelShape[][][][] cache = up ? rawCache[1] : rawCache[0];
            for (WallShape east : EAST_SHAPE.getValues()) {
                for (WallShape north : NORTH_SHAPE.getValues()) {
                    for (WallShape west : WEST_SHAPE.getValues()) {
                        for (WallShape south : SOUTH_SHAPE.getValues()) {

                            BlockState blockState = this.getDefaultState()
                                .with(UP, up)
                                .with(EAST_SHAPE, east)
                                .with(WEST_SHAPE, west)
                                .with(NORTH_SHAPE, north)
                                .with(SOUTH_SHAPE, south)
                                .with(WATERLOGGED, false);

                            this.setCached(cache, east, north, west, south, map.get(blockState));
                        }
                    }
                }
            }
        }
    }

    //shape 4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F
    //collision 4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F
    private boolean isShape(float h, float j, float k) {
        return h == 16.0F && j == 14.0F && k == 16.0F;
    }

    private boolean isCollision(float h, float j, float k) {
        return h == 24.0F && j == 24.0F && k == 24.0F;
    }

    private boolean isCommon(float f, float g, float i) {
        return f == 4.0F && g == 3.0F && i == 0.0F;
    }

    private VoxelShape getCached(VoxelShape[][][][] cache, WallShape east, WallShape north, WallShape west, WallShape south) {
        return cache[east.ordinal()][north.ordinal()][west.ordinal()][south.ordinal()];
    }

    private void setCached(VoxelShape[][][][] cache, WallShape east, WallShape north, WallShape west, WallShape south, VoxelShape shape) {
        cache[east.ordinal()][north.ordinal()][west.ordinal()][south.ordinal()] = shape;
    }

}
