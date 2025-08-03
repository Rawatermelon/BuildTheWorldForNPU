package com.tf.npu.blocks.npublocknewclasses;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class HorizontalDirectionalHalfSlab extends SlabBlock {
    // 额外属性
    public static final EnumProperty<Direction> FACING = Properties.FACING;
    protected boolean canBeDouble;

    // 构造
    public HorizontalDirectionalHalfSlab(Settings settings, boolean canBeDouble) {
        super(settings);
        this.canBeDouble = canBeDouble;
    }
    public static class Factory{
        private boolean canBeDouble;
        public Factory setCanBeDouble(boolean canBeDouble){
            this.canBeDouble = canBeDouble;
            return this;
        }
        public Function<Settings, Block> build(){
            return (settings) -> new HorizontalDirectionalHalfSlab(settings, canBeDouble);
        }
    }

    // 额外属性注册

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    // 放置时状态
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = ctx.getWorld().getBlockState(blockPos);
        if (blockState.isOf(this)) {
            if (this.canBeDouble) return blockState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
            else return blockState.with(TYPE, getOppositeSlabType(blockState.get(TYPE)));
        } else {
            FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
            BlockState blockState2 = this.getDefaultState().with(TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
            Direction direction = ctx.getSide();
            return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > (double)0.5F)) ? blockState2 : blockState2
                    .with(TYPE, SlabType.TOP)
                    .with(FACING, ctx.getPlayer() != null ? ctx.getPlayer().getHorizontalFacing().getOpposite() : Direction.NORTH);
        }
    }

    // 辅助函数
    // 获取相反状态
    private SlabType getOppositeSlabType(SlabType current) {
        return switch (current) {
            case BOTTOM -> SlabType.TOP;
            case TOP -> SlabType.BOTTOM;
            case DOUBLE -> null;
        };
    }
}
