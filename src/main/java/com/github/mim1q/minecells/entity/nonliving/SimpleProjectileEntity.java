package com.github.mim1q.minecells.entity.nonliving;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

import java.util.UUID;

import static net.minecraft.util.hit.HitResult.Type.BLOCK;
import static net.minecraft.util.hit.HitResult.Type.ENTITY;

public class SimpleProjectileEntity extends Entity {
  private UUID ownerUuid;

  public SimpleProjectileEntity(EntityType<?> type, World world) {
    super(type, world);
    this.ownerUuid = null;
  }

  public SimpleProjectileEntity(EntityType<?> type, World world, LivingEntity owner) {
    super(type, world);
    this.ownerUuid = owner.getUuid();
  }

  @Override
  public void tick() {
    super.tick();

    var nextPos = getPos().add(this.getVelocity());
    if (!getWorld().isClient()) {
      // Check for entity collision
      var entityColision = ProjectileUtil.getEntityCollision(
        getWorld(),
        this,
        getPos(),
        nextPos,
        getBoundingBox().stretch(getVelocity()).expand(1.0),
        it -> it.getUuid() != ownerUuid && it instanceof LivingEntity
      );
      if (entityColision != null && entityColision.getType() == ENTITY) {
        onHitEntity((LivingEntity) entityColision.getEntity());
      }
      // Check for block collision
      var blockCollision = getWorld().raycast(new RaycastContext(
        getPos(),
        nextPos,
        ShapeType.COLLIDER,
        FluidHandling.NONE,
        this
      ));
      if (blockCollision != null && blockCollision.getType() == BLOCK) {
        var blockPos = blockCollision.getBlockPos();
        onHitBlock(blockCollision.getPos(), blockPos, getWorld().getBlockState(blockPos), blockCollision.getSide());
      }

      if (this.age >= 20 * 60) discard();
    }

    this.setPosition(nextPos);
  }

  public void onHitEntity(LivingEntity target) {
    var owner = ((ServerWorld)getWorld()).getEntity(ownerUuid);
    target.damage(getWorld().getDamageSources().mobProjectile(this, (LivingEntity) owner), getDamage());
    discard();
  }

  public void onHitBlock(Vec3d pos, BlockPos blockPos, BlockState state, Direction side) {
    discard();
  }

  public float getDamage() {
    return 3.0f;
  }

  @Override
  public void setVelocity(Vec3d velocity) {
    super.setVelocity(velocity);

    double e = this.getVelocity().x;
    double f = this.getVelocity().y;
    double g = this.getVelocity().z;
    double l = this.getVelocity().horizontalLength();
    this.setYaw((float) (-MathHelper.atan2(e, g) * MathHelper.DEGREES_PER_RADIAN));
    this.setPitch((float) (-MathHelper.atan2(f, l) * MathHelper.DEGREES_PER_RADIAN));

    this.prevYaw = this.getYaw();
    this.prevPitch = this.getPitch();
  }

  @Override
  protected void initDataTracker() {

  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
    this.ownerUuid = nbt.getUuid("owner");
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
    nbt.putUuid("owner", ownerUuid);
  }
}
