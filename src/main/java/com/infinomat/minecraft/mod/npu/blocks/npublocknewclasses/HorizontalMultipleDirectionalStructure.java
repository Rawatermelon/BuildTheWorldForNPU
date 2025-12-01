package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.LoadShape;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HorizontalMultipleDirectionalStructure extends Block implements LoadShape {
    // 额外属性
    public static final IntProperty ANGEL = IntProperty.of("angel", 0, 11);
    public NpuBlocks.LoadMethod loadMethod;
    protected VoxelShape shape;
    protected int angle;
    // 体积映射
    private final List<List<VoxelShape>> angleShapeList;

    // 构造
    private HorizontalMultipleDirectionalStructure(Settings settings, List<List<VoxelShape>> shapeList, NpuBlocks.LoadMethod loadMethod) {
        super(settings);
        this.angleShapeList = shapeList;
        this.shape = null;
        this.loadMethod = loadMethod;
        this.angle = 0;
    }
    public static class Factory{
        private final List<List<VoxelShape>> shapeLists = new ArrayList<>(6);
        private NpuBlocks.LoadMethod loadMethod;
        public Factory setShape(List<BlockShapeData> shapeDatas, String loadMethod){
            shapeDatas.forEach(shapeData -> {
                if (!shapeData.loaderIsObj()) for (List<Double> shape : shapeData.getShapeList()) {
                    shapeLists.get(shapeDatas.indexOf(shapeData))
                            .add(VoxelShapes.cuboid(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
                }
            });
            this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
            return this;
        }
        public Function<Settings, Block> build(){
            return (settings) -> new HorizontalMultipleDirectionalStructure(settings, shapeLists, loadMethod);
        }
    }

    // 额外属性注册
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ANGEL);
    }

    // 放置时状态
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(ANGEL, switch (ctx.getHorizontalPlayerFacing().getOpposite()) {
            case WEST, EAST -> 6;
            default -> 0;
        });
    }

    // 设置形状
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (shape == null || angle != state.get(ANGEL)) {
            angle = state.get(ANGEL);
            shape = loadShape(angleShapeList.get(angle % 6), loadMethod);
        }
        return shape;
    }

    // 被空手点击时

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        state = state.with(ANGEL, (state.get(ANGEL) + 1) % 12);
        world.setBlockState(pos, state, 10);
        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return ActionResult.SUCCESS;
    }
}
