package com.infinomat.minecraft.mod.npu.entities.npuentitynewclasses.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
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

public class Bike extends NpuVehicle implements GeoEntity {
    protected final RawAnimation FRONT_ANIM;
    protected final RawAnimation BACK_ANIM;

    // 动画数据
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    // 构造函数
    private Bike(EntityType<Bike> type, World world, String id) {
        super(type, world);
        FRONT_ANIM = RawAnimation.begin().thenLoop("animation." + id + ".front");
        BACK_ANIM = RawAnimation.begin().thenLoop("animation." + id + ".back");
    }

    public static class BikeFactory {
        private final String id;

        private BikeFactory(String id) {
            this.id = id;
        }

        public EntityType.EntityFactory<Bike> factory() {
            return (EntityType<Bike> type, World world) -> new Bike(type, world, this.id);
        }
    }

    public static BikeFactory bikeFactory(String id) {
        return new BikeFactory(id);
    }

    ;

    // 动画控制器
    @Override
    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("Move", 5,
                (animTest) -> {
                    if (animTest.isMoving()) {
                        // 获取实体的运动方向和朝向
                        Vec3d movement = this.getVelocity();
                        float yaw = this.getYaw() + 90; // 加90度因为模型初始朝向问题
                        Vec3d forwardDirection = new Vec3d(
                                -Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)))
                                .normalize();
                        
                        // 计算运动方向和朝向的点积，判断是否同向
                        double dotProduct = movement.dotProduct(forwardDirection);
                        
                        // 当运动方向与载具方向相同时播放Front动画，否则播放Back动画
                        if (dotProduct > 0) {
                            return animTest.setAndContinue(getFrontAnim());
                        } else {
                            return animTest.setAndContinue(getBackAnim());
                        }
                    } else {
                        return PlayState.STOP;
                    }
                })
        );
    }

    protected RawAnimation getFrontAnim() {
        return FRONT_ANIM;
    }

    protected RawAnimation getBackAnim() {
        return BACK_ANIM;
    }

    // 互动


    @Override
    void updateMovement() {
        PlayerEntity player = this.getControllingPassenger();

        if (player != null) {
            this.setYaw(player.getYaw() - 90);
            
            // 获取载具当前朝向
            float yaw = this.getYaw() + 90;
            Vec3d forwardDirection = new Vec3d(
                    -Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)))
                    .normalize();
            
            // 将当前速度投影到朝向方向上
            Vec3d projectedVelocity = forwardDirection.multiply(this.getVelocity().dotProduct(forwardDirection));
            
            // 获取玩家输入
            Vec3d inputVelocity = player.getVelocity();
            
            // 计算输入在朝向方向上的分量
            Vec3d addedVelocity = forwardDirection.multiply(inputVelocity.dotProduct(forwardDirection));
            
            // 更新速度：当前速度分量 + 输入产生的加速度 - 阻尼
            this.setVelocity(projectedVelocity.add(addedVelocity).multiply(0.95)); // 添加速度衰减
        } else {
            // 添加更自然的阻尼效果
            this.setVelocity(this.getVelocity().multiply(0.95, 1.0, 0.95));
        }
        this.move(MovementType.SELF, this.getVelocity());
    }

    // 动画实例
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    // 属性
    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public Vec3d getPassengerRidingPos(Entity passenger) {
        return super.getPassengerRidingPos(passenger);
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        List<Double> xz = getPosition(0, 0);
       positionUpdater.accept(passenger, xz.get(0), this.getY() + 0.5, xz.get(1));
    }
    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        List<Double> xz = getPosition(0.0F, 1.2F);
        return new Vec3d(xz.get(0), this.getY(), xz.get(1));
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }
}
