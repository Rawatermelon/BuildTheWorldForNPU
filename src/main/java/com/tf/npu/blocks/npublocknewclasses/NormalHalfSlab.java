package com.tf.npu.blocks.npublocknewclasses;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class NormalHalfSlab extends SlabBlock {
    // 额外属性
    protected boolean canBeDouble;
    // 构造
    public NormalHalfSlab(Settings settings, boolean canBeDouble) {
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
            return (settings) -> new NormalHalfSlab(settings, canBeDouble);
        }
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
            return direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > (double)0.5F)) ? blockState2 : blockState2.with(TYPE, SlabType.TOP);
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
