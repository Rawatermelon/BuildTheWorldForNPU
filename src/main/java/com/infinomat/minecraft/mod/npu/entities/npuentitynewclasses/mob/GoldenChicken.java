package com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.mob;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.world.World;

public class GoldenChicken extends ChickenEntity {
    private GoldenChicken(EntityType<GoldenChicken> type, World world, String id) {
        super(type, world);
    }
    public static class GoldenChickenFactory{
        private final String id;
        private GoldenChickenFactory (String id){
            this.id = id;
        }
        public EntityType.EntityFactory<GoldenChicken> factory(){
            return (EntityType<GoldenChicken> type, World world) -> new GoldenChicken(type, world, id);
        }
    }
    public static GoldenChickenFactory goldenChickenFactory(String id){
        return new GoldenChickenFactory(id);
    }
}