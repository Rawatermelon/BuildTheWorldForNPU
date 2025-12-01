package com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.CreakingEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class SchoolBus extends NpuVehicle implements GeoEntity {
    // 动画数据
    private final RawAnimation MOVE_ANIM;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // 实体工厂
    private SchoolBus(EntityType<? extends SchoolBus> type, World world, String id) {
        super(type, world);
        MOVE_ANIM = RawAnimation.begin().thenLoop("animation." + id + ".move");
    }

    public static class SchoolBusFactory {
        private final String id;

        private SchoolBusFactory(String id) {
            this.id = id;
        }

        public EntityType.EntityFactory<SchoolBus> factory() {
            return (EntityType<SchoolBus> type, World world) -> new SchoolBus(type, world, this.id);
        }
    }

    public static SchoolBusFactory schoolBusFactory(String id) {
        return new SchoolBusFactory(id);
    }

    ;

    // 动画控制器
    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("Moving", 5,
                (animTest) -> animTest.isMoving() ? animTest.setAndContinue(MOVE_ANIM) : PlayState.STOP)
        );
    }

    // 动画实例
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
    // 属性

    @Override
    public boolean isPushable() {
        return false;
    }

    // 移动
    @Override
    public void tick() {
        super.tick();

        // 让周围实体上车
        List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand((double) 0.2F, (double) -0.01F, (double) 0.2F), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.getWorld().isClient && !(this.getControllingPassenger() instanceof PlayerEntity);

            for (Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    if (bl && this.getPassengerList().size() < this.getMaxPassengers() && !entity.hasVehicle()
                            && entity instanceof LivingEntity
                            && !(entity instanceof WaterCreatureEntity)
                            && !(entity instanceof PlayerEntity)
                            && !(entity instanceof CreakingEntity)) {
                        entity.startRiding(this);
                    } else {
                        this.pushAwayFrom(entity);
                    }
                }
            }
        }
    }

    @Override
    void updateMovement() {
        PlayerEntity player = this.getControllingPassenger();

        if (player != null) {
            // 获取载具当前朝向
            float yaw = this.getYaw();
            Vec3d forwardDirection = new Vec3d(
                    -Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)))
                    .normalize();
            
            // 计算玩家输入的横向方向
            Vec3d inputVelocity = player.getVelocity();
            Vec3d rightDirection = new Vec3d(-forwardDirection.getZ(), 0, forwardDirection.getX()).normalize();
            
            // 计算输入在横向方向上的分量
            double lateralInput = inputVelocity.dotProduct(rightDirection);
            
            // 仅当纵向速度不为零时才可转向
            Vec3d longitudinalVelocity = forwardDirection.multiply(this.getVelocity().dotProduct(forwardDirection));
            if (longitudinalVelocity.length() > 0.01) {
                this.setYaw((float) (yaw + lateralInput * 40.0));
            }
            
            // 计算输入在朝向方向上的分量
            Vec3d addedVelocity = forwardDirection.multiply(inputVelocity.dotProduct(forwardDirection));
            
            // 更新速度：当前速度分量 + 输入产生的加速度
            Vec3d newVelocity = longitudinalVelocity.add(addedVelocity);
            
            // 对垂直于运动方向的速度分量有阻尼
            Vec3d lateralVelocity = this.getVelocity().subtract(longitudinalVelocity);
            lateralVelocity = lateralVelocity.multiply(0.8); // 垂直方向阻尼
            
            this.setVelocity(newVelocity.add(lateralVelocity));
        }
        
        // 校车巴士有速度衰减
        this.setVelocity(this.getVelocity().multiply(0.95));
        
        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        List<Double> xz = getPosition(
                getPassengerList().indexOf(passenger) % 2 == 0 ? 1 : -1,
                6.0 - ((double) (getPassengerList().indexOf(passenger) + 1) / 2) * 25 / 16.0);
        positionUpdater.accept(passenger, xz.get(0), this.getY() + 1.2, xz.get(1));
    }
    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        List<Double> xz = getPosition(3.5F, 0.0F);
        return new Vec3d(xz.get(0), this.getY(), xz.get(1));
    }

    @Override
    public int getMaxPassengers() {
        return 15;
    }
}