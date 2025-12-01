package com.infinomat.minecraft.mod.npu.entities;

import com.mojang.logging.LogUtils;
import com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.mob.GoldenChicken;
import com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.vehicle.Bike;
import com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.vehicle.NpuVehicle;
import com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.vehicle.SchoolBus;
import com.infinomat.minecraft.mod.npu.util.Reference;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NpuEntities {
    public static final Logger LOGGER = LogUtils.getLogger();

    private NpuEntities() {
    }

    private static final String GOLDEN_CHICKEN_ID = "golden_chicken";
    private static final String SCHOOL_BUS_ID = "school_bus";
    private static final List<String> BIKE_IDS = List.of("bike1", "bike2", "bike3");

    public static EntityType<GoldenChicken> GOLDEN_CHICKEN;
    public static EntityType<SchoolBus> SCHOOL_BUS;
    public static List<EntityType<Bike>> BIKES;

    public static HashMap<String, EntityType<? extends MobEntity>> MobEntityMap = new HashMap<>();
    public static HashMap<String, EntityType<? extends NpuVehicle>> VehicleMap = new HashMap<>();

    public static void register() {
        GOLDEN_CHICKEN = registerMobEntity(GOLDEN_CHICKEN_ID, EntityType.Builder
                .create(GoldenChicken.goldenChickenFactory(GOLDEN_CHICKEN_ID).factory(), SpawnGroup.CREATURE)
                .dimensions(1.0f, 1.0f)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Reference.MOD_ID, GOLDEN_CHICKEN_ID))));
        SCHOOL_BUS = registerVehicleEntity(SCHOOL_BUS_ID, EntityType.Builder
                .create(SchoolBus.schoolBusFactory(SCHOOL_BUS_ID).factory(), SpawnGroup.CREATURE)
                .dimensions(6.0f, 5.0f)
                .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Reference.MOD_ID, SCHOOL_BUS_ID))));
        ArrayList<EntityType<Bike>> bikes = new ArrayList<>();
        BIKE_IDS.forEach(id -> bikes.add(
                registerVehicleEntity(id, EntityType.Builder
                        .create(Bike.bikeFactory(id).factory(), SpawnGroup.CREATURE)
                        .dimensions(1.2f, 1.0f)
                        .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Reference.MOD_ID, id))))
        ));

        FabricDefaultAttributeRegistry.register(GOLDEN_CHICKEN, GoldenChicken.createMobAttributes().add(EntityAttributes.TEMPT_RANGE, 10.0));

        BIKES = bikes.stream().toList();
    }

    public static <V extends MobEntity> EntityType<V> registerMobEntity(String id, EntityType<V> entityType) {
        EntityType<V> res = Registry.register(Registries.ENTITY_TYPE, Identifier.of(Reference.MOD_ID, id), entityType);

        MobEntityMap.put(id + "_spawn_egg", res);
        return res;
    }

    public static <V extends NpuVehicle> EntityType<V> registerVehicleEntity(String id, EntityType<V> entityType) {
        EntityType<V> res = Registry.register(Registries.ENTITY_TYPE, Identifier.of(Reference.MOD_ID, id), entityType);

        VehicleMap.put(id, res);
        return res;
    }
}
