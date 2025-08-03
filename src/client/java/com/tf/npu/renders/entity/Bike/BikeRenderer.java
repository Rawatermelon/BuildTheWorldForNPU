package com.tf.npu.renders.entity.Bike;

import com.tf.npu.entities.npuentitynewclasses.vehicle.Bike;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

//Bike1的Renderer

public class BikeRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<Bike, R> {
    public BikeRenderer(EntityRendererFactory.Context context, String id) {
        super(context, new BikeModel(id));
        this.shadowRadius = 0.5f;
    }
}
