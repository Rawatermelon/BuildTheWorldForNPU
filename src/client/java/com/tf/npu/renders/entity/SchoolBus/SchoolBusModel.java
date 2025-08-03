package com.tf.npu.renders.entity.SchoolBus;

import com.tf.npu.entities.npuentitynewclasses.vehicle.SchoolBus;
import com.tf.npu.util.Reference;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class SchoolBusModel extends GeoModel<SchoolBus> {
    private final Identifier model = Identifier.of(Reference.MOD_ID, "school_bus");
    private final Identifier animations = Identifier.of(Reference.MOD_ID, "school_bus");
    private final Identifier texture = Identifier.of(Reference.MOD_ID, "textures/entity/s/school_bus.png");

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return this.model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return this.texture;
    }

    @Override
    public Identifier getAnimationResource(SchoolBus schoolBus) {
        return this.animations;
    }
}