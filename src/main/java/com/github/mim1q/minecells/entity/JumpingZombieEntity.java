package com.github.mim1q.minecells.entity;

import com.github.mim1q.minecells.entity.ai.goal.JumpAttackGoal;
import com.github.mim1q.minecells.entity.interfaces.IJumpAttackEntity;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;


public class JumpingZombieEntity extends MineCellsEntity implements IJumpAttackEntity {

    // Animation Data
    @Environment(EnvType.CLIENT)
    public float additionalRotation = 0.0F;

    public JumpingZombieEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public void tick() {
        super.tick();
        this.decrementCooldown(JUMP_COOLDOWN, "jump");
    }

    //region Goals and Tracked Data

    private static final TrackedData<Integer> JUMP_COOLDOWN = DataTracker.registerData(JumpingZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Override
    public void initGoals() {
        this.goalSelector.add(3, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 1.0d));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0d));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 0, false, false, null));

        this.goalSelector.add(0, new JumpingZombieJumpAttackGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.3D, false));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();

        this.dataTracker.startTracking(JUMP_COOLDOWN, 50);
    }

    //endregion
    //region IJumpAttackEntity Implementation

    public void setJumpAttackCooldown(int ticks) {
        this.dataTracker.set(JUMP_COOLDOWN, ticks);
    }

    public int getJumpAttackCooldown() {
        return this.dataTracker.get(JUMP_COOLDOWN);
    }

    public int getJumpAttackMaxCooldown() {
        return 20 + this.getRandom().nextInt(40);
    }

    //endregion

    //region Sounds

    @Override
    public SoundEvent getJumpAttackChargeSoundEvent() {
        return SoundRegistry.JUMPING_ZOMBIE_JUMP_CHARGE;
    }

    @Override
    public SoundEvent getJumpAttackReleaseSoundEvent() {
        return SoundRegistry.JUMPING_ZOMBIE_JUMP_RELEASE;
    }

    //endregion

    public static DefaultAttributeContainer.Builder createJumpingZombieAttributes() {
        return createLivingAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0D)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0D)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 1.0D);
    }

    static class JumpingZombieJumpAttackGoal extends JumpAttackGoal<JumpingZombieEntity> {

        public JumpingZombieJumpAttackGoal(JumpingZombieEntity entity) {
            super(entity, 10, 15);
        }

        @Override
        public boolean canStart() {
            boolean canJump = super.canStart() && this.entity.getRandom().nextFloat() < 0.03f;
            if (!canJump)
                return false;
            double d = this.entity.distanceTo(this.entity.getTarget());
            return d >= 4.0d && d <= 12.0d;
        }
    }
}
