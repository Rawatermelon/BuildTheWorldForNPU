package com.tf.npu.entities.npuentitynewclasses.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

abstract public class NpuVehicle extends VehicleEntity {
    protected PositionInterpolator positionInterpolator = new PositionInterpolator(this);
    // 生成
    public NpuVehicle(EntityType<? extends NpuVehicle> entityType, World world) {
        super(entityType, world);
    }
    @Override
    protected Item asItem() {
        return Items.IRON_NUGGET;
    }
    @Override
    protected void readCustomData(ReadView view) {}
    @Override
    protected void writeCustomData(WriteView view) {}

    // 碰撞
    @Override
    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable(entity) || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    @Override
    public boolean isCollidable(@Nullable Entity entity) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }


    // 互动
    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ActionResult actionResult = super.interact(player, hand);
        if (actionResult != ActionResult.PASS) {
            return actionResult;
        } else {
            return (player.shouldCancelInteraction()
                    || !this.getWorld().isClient && !player.startRiding(this)
                    ? ActionResult.PASS : ActionResult.SUCCESS);
        }
    }

    @Override
    protected boolean canAddPassenger(@NotNull Entity entity) {
        return entity instanceof PlayerEntity && getPassengerList().size() < getMaxPassengers();
    }

    protected abstract int getMaxPassengers();

    @Override
    @Nullable
    public PlayerEntity getControllingPassenger() {
        if (this.getFirstPassenger() instanceof PlayerEntity player) {
            return player;
        } else {
            return null;
        }
    }

    @Override
    public void tick() {
        super.tick();
        positionInterpolator.tick();

        if (this.isLogicalSideForUpdatingMovement()) {
            updateMovement();
        } else {
            this.setVelocity(Vec3d.ZERO);
        }
    }

    abstract void updateMovement();

    protected final List<Double> getPosition(double relative_X, double relative_Z) {
        double x = this.getX() - relative_Z * MathHelper.sin(getYaw() * MathHelper.RADIANS_PER_DEGREE)
                + relative_X * MathHelper.cos(getYaw() * MathHelper.RADIANS_PER_DEGREE);
        double z = this.getZ() + relative_Z * MathHelper.cos(getYaw() * MathHelper.RADIANS_PER_DEGREE)
                + relative_X * MathHelper.sin(getYaw() * MathHelper.RADIANS_PER_DEGREE);
        return List.of(x, z);
    }
}
