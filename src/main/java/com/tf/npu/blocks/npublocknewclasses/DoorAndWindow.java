package com.tf.npu.blocks.npublocknewclasses;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tf.npu.blocks.NpuBlocks;
import com.tf.npu.blocks.npublocknewclasses.common.DirectionCheck;
import com.tf.npu.blocks.npublocknewclasses.common.LoadShape;
import com.tf.npu.util.register.data.template.BlockShapeData;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DoorAndWindow extends HorizontalFacingBlock implements LoadShape {
    private static final MapCodec<DoorAndWindow> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    createSettingsCodec(),
                    Codec.STRING.fieldOf("load_method").forGetter(p -> p.loadMethod.name()),
                    Codec.STRING.fieldOf("direction").forGetter(p -> p.direction.name())
            ).apply(instance, DoorAndWindow::new)
    );
    public static final BooleanProperty OPEN = Properties.OPEN;
    protected boolean open;
    private Direction direction;
    // 存储方块的形状
    List<VoxelShape> shapeList_open = new ArrayList<>(0);
    List<VoxelShape> shapeList_close = new ArrayList<>(0);
    NpuBlocks.LoadMethod loadMethod;
    VoxelShape shape;

    // 构造
    private DoorAndWindow(Settings settings, List<VoxelShape> shapeList_open, List<VoxelShape> shapeList_close, NpuBlocks.LoadMethod loadMethod) {
        super(settings);
        this.open = false;
        this.shapeList_open = shapeList_open;
        this.shapeList_close = shapeList_close;
        this.loadMethod = loadMethod;
        this.shape = null;
    }
    private DoorAndWindow(AbstractBlock.Settings settings, String loadMethod, String direction){
        super(settings);
        this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
        this.shape = null;
        this.direction = null;
    }

    @Override
    protected MapCodec<? extends DoorAndWindow> getCodec() {
        return CODEC;
    }

    public static class Factory{
        private final ArrayList<VoxelShape> shapeList_open = new ArrayList<>(0);
        private final ArrayList<VoxelShape> shapeList_close = new ArrayList<>(0);
        private NpuBlocks.LoadMethod loadMethod;
        public Factory setShape(BlockShapeData shapeData_open, BlockShapeData shapeData_close, String loadMethod){
            if (!shapeData_open.loaderIsObj()) for (List<Double> shape : shapeData_open.getShapeList()) {
                this.shapeList_open.add(VoxelShapes.cuboid(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
            }
            if (!shapeData_close.loaderIsObj()) for (List<Double> shape : shapeData_close.getShapeList()) {
                this.shapeList_close.add(VoxelShapes.cuboid(shape.get(0), shape.get(1), shape.get(2), shape.get(3), shape.get(4), shape.get(5)));
            }
            this.loadMethod = NpuBlocks.LoadMethod.valueOf(loadMethod);
            return this;
        }
        public Function<Settings, Block> build(){
            return (settings) -> new DoorAndWindow(settings, shapeList_open, shapeList_close, loadMethod);
        }
    }

    // 额外属性注册

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
        builder.add(OPEN);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState()
                .with(OPEN, false)
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    // 设置形状
    @Override
    public @NotNull VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (shape == null || open != state.get(OPEN) || direction != state.get(FACING)) {
            open = state.get(OPEN);
            shape = loadShape(open ? shapeList_open : shapeList_close, loadMethod, state.get(FACING));
        }
        return shape;
    }

    // 被点击时
    @Override
    protected @NotNull ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        state = state.cycle(OPEN);
        world.setBlockState(pos, state, 10);
        world.emitGameEvent(player, state.get(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return ActionResult.SUCCESS;
    }
}
