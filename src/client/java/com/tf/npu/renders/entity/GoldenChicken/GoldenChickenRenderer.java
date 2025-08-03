package com.tf.npu.renders.entity.GoldenChicken;

import com.tf.npu.entities.npuentitynewclasses.mob.GoldenChicken;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;


//GoldenChicken的Renderer

public class GoldenChickenRenderer extends MobEntityRenderer<GoldenChicken, GoldenChickenRenderState, GoldenChickenModel> {
    private static final Identifier TEXTURE = Identifier.of("npu","textures/entity/g/golden_chicken.png");

    public GoldenChickenRenderer(EntityRendererFactory.Context context) {
        super(context, new GoldenChickenModel(context.getPart(EntityModelLayers.CHICKEN)), 0.3f);
    }

    @Override
    public GoldenChickenRenderState createRenderState() {
        return new GoldenChickenRenderState();
    }

    @Override
    public Identifier getTexture(GoldenChickenRenderState state) {
        return TEXTURE;
    }
}
