package com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses;

import com.infinomat.minecraft.mod.npu.blocks.NpuBlocks;
import com.infinomat.minecraft.mod.npu.blocks.npublocknewclasses.common.LoadShape;
import com.infinomat.minecraft.mod.npu.util.register.data.template.BlockShapeData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NormalStructure extends Block implements LoadShape {
    // 加载方式
    private final NpuBlocks.LoadMethod loadMethod;
    // 体积
    public List<VoxelShape> shapeList;
    public VoxelShape shape;

    // 构造
    private NormalStructure(Settings settings, List<VoxelShape> shapeList, NpuBlocks.LoadMethod loadMethod) {
        super(settings);
        this.shapeList = shapeList;
        this.shape = null;
        this.loadMethod = loadMethod;
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
        public Function<Settings, Block> build(){
            return (settings) -> new NormalStructure(settings, shapeList, loadMethod);
        }
    }

    // 设置形状
    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (shape == null) {
            shape = loadShape(shapeList, loadMethod);
        }
        return shape;
    }
}
