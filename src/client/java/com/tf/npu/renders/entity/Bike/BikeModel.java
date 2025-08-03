package com.tf.npu.renders.entity.Bike;

import com.tf.npu.entities.npuentitynewclasses.vehicle.Bike;
import com.tf.npu.util.Reference;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;


public class BikeModel extends GeoModel<Bike> {
    private final Identifier model;
    private final Identifier animations;
    private final Identifier texture;
    public BikeModel(String id) {
        model = Identifier.of(Reference.MOD_ID, id);
        animations = Identifier.of(Reference.MOD_ID, id);
        texture = Identifier.of(Reference.MOD_ID, "textures/entity/b/" + id + ".png");
    }



    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return this.model;
    }

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return this.texture;
    }

    @Override
    public Identifier getAnimationResource(Bike bike) {
        return animations;
    }
}