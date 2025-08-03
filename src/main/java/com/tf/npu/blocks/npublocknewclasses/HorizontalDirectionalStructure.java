package com.tf.npu.blocks.npublocknewclasses;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tf.npu.blocks.NpuBlocks;
import com.tf.npu.blocks.npublocknewclasses.common.LoadShape;
import com.tf.npu.util.register.data.template.BlockShapeData;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HorizontalDirectionalStructure extends HorizontalFacingBlock implements LoadShape {
    private static final MapCodec<HorizontalDirectionalStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    createSettingsCodec(),
                    Codec.STRING.fieldOf("load_method").forGetter(p -> p.loadMethod.name()),
                    Codec.STRING.fieldOf("direction").forGetter(p -> p.direction.name())
            ).apply(instance, HorizontalDirectionalStructure::new)
    );
    // 额外属性
    protected NpuBlocks.LoadMethod loadMethod;
    // 存储方块的形状
    List<VoxelShape> shapeList;
    protected VoxelShape shape;
    // 当前方向
    protected Direction direction;

    // 构造
    private HorizontalDirectionalStructure(AbstractBlock.Settings settings, List<VoxelShape> shapeList, NpuBlocks.LoadMethod loadMethod) {
        super(settings);
        this.loadMethod = loadMethod;
        this.shapeList = shapeList;
        this.shape = null;
        this.direction = null;
    }
    private HorizontalDirectionalStructure(AbstractBlock.Settings settings, String loadMethod, String direction){
        super(settings);
        this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
        this.shape = null;
        this.direction = null;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    public static class Factory{
        private final ArrayList<VoxelShape> shapeList = new ArrayList<>(0);
        private NpuBlocks.LoadMethod loadMethod;
        public Factory setShape(BlockShapeData shapeData, String loadMethod){
            if (!shapeData.loaderIsObj()) for (List<Double> shape : shapeData.getShapeList()) {
                this.shapeList.add(VoxelShapes.cuboid(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
            }
            this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
            return this;
        }
        public Function<AbstractBlock.Settings, Block> build(){
            return (settings) -> new HorizontalDirectionalStructure(settings, shapeList, loadMethod);
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
        return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // 设置形状
    @Override
    public @NotNull VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (shape == null || direction != state.get(FACING)) {
            direction = state.get(FACING);
            shape = loadShape(shapeList, loadMethod, direction);
        }
        return shape;
    }
}
