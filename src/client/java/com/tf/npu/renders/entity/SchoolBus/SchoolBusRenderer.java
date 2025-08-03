package com.tf.npu.renders.entity.SchoolBus;

import com.tf.npu.entities.npuentitynewclasses.vehicle.SchoolBus;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

//SchoolBus的Renderer

public class SchoolBusRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<SchoolBus, R> {
    public SchoolBusRenderer(EntityRendererFactory.Context context) {
        super(context, new SchoolBusModel());
        this.shadowRadius = 4.5f;
    }
}
