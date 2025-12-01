package com.infinomat.minecraft.mod.npu.items.npuitemnewclasses;

import com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.vehicle.NpuVehicle;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.Spawner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;

public class VehicleItem extends Item {
    private final EntityType<? extends NpuVehicle> type;

    public VehicleItem(EntityType<? extends NpuVehicle> entityType, Settings settings) {
        super(settings);
        this.type = entityType;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();

        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            // 获取基本信息
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity var8 = world.getBlockEntity(blockPos);

            // 生成载具
            if (var8 instanceof Spawner spawner) {
                spawner.setEntityType(type, world.getRandom());
                world.updateListeners(blockPos, blockState, blockState, 3);
                world.emitGameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, blockPos);
                itemStack.decrement(1);
                return ActionResult.SUCCESS;
            } else {
                BlockPos blockPos2;
                if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                    blockPos2 = blockPos;
                } else {
                    blockPos2 = blockPos.offset(direction);
                }

                if (type.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockPos2, SpawnReason.SPAWN_ITEM_USE,
                        true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP) != null) {
                    itemStack.decrement(1);
                    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
                }

                return ActionResult.SUCCESS;
            }
        }
    }
}
