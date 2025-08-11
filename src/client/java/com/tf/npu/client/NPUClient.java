package com.tf.npu.client;

import com.tf.npu.blocks.NpuBlocks;
import com.tf.npu.entities.NpuEntities;
import com.tf.npu.renders.entity.Bike.BikeRenderer;
import com.tf.npu.renders.entity.GoldenChicken.GoldenChickenRenderer;
import com.tf.npu.renders.entity.SchoolBus.SchoolBusRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.BlockRenderLayer;

public class NPUClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(NpuEntities.GOLDEN_CHICKEN, GoldenChickenRenderer::new);
        EntityRendererRegistry.register(NpuEntities.SCHOOL_BUS, SchoolBusRenderer::new);
        NpuEntities.BIKES.forEach(bike -> EntityRendererRegistry.register(bike,
                (context) -> new BikeRenderer<>(context,
                        bike.getTranslationKey().substring(bike.getTranslationKey().lastIndexOf('.') + 1)))
        );
        NpuBlocks.CutoutBlocks.forEach(block -> BlockRenderLayerMap.putBlock(block, BlockRenderLayer.CUTOUT));
        NpuBlocks.TransparentBlocks.forEach(block -> BlockRenderLayerMap.putBlock(block, BlockRenderLayer.TRANSLUCENT));
    }
}
