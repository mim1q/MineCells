package com.github.mim1q.minecells.entity.nonliving;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.registry.EntityRegistry;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CellEntity extends Entity {

  protected static final TrackedData<Integer> AMOUNT = DataTracker.registerData(CellEntity.class, TrackedDataHandlerRegistry.INTEGER);

  protected PlayerEntity target = null;

  public CellEntity(EntityType<CellEntity> entityType, World world) {
    super(entityType, world);
    this.setPosition(this.getX() + this.random.nextFloat(), this.getY(), this.getZ() + this.random.nextFloat());
  }

  public static void spawn(World world, Vec3d position, int amount) {
    CellEntity cell = new CellEntity(EntityRegistry.CELL, world);
    cell.setPosition(position);
    cell.setVelocity(
      (world.random.nextFloat() - 0.5F),
      world.random.nextFloat() - 0.5F,
      (world.random.nextFloat() - 0.5F)
    );
    cell.setAmount(amount);
    world.spawnEntity(cell);
  }

  @Override
  protected void initDataTracker() {
    this.dataTracker.startTracking(AMOUNT, 1);
  }

  @Override
  public void tick() {
    super.tick();
    this.prevX = this.getX();
    this.prevY = this.getY();
    this.prevZ = this.getZ();
    if (!this.world.isClient()) {
      if (this.age % 20 == 1) {
        this.target = this.world.getClosestPlayer(this, 10.0D);
      }
      if (this.target != null && this.target.isAlive() && this.target.distanceTo(this) <= 10.0D) {
        double distance = this.target.distanceTo(this);
        double multiplier = distance == 0.0D ? 1.0D : 0.01D / distance;
        this.addVelocity(
          (this.target.getX() - this.getX()) * multiplier,
          (this.target.getY() - this.getY()) * multiplier,
          (this.target.getZ() - this.getZ()) * multiplier
        );
        if (this.target.getBoundingBox().contains(this.getBoundingBox().getCenter())) {
          PlayerEntityAccessor target = (PlayerEntityAccessor) this.target;
          target.setCells(target.getCells() + this.getAmount());
          this.playSound(SoundRegistry.CELL_ABSORB, 0.25F, 1.0F);
          this.discard();
        }
      }
      this.setVelocity(this.getVelocity().multiply(0.95D));
      this.addVelocity(0.0D, -0.01D, 0.0D);
      this.velocityModified = true;
    }
    this.move(MovementType.SELF, this.getVelocity());
  }

  @Override
  protected MoveEffect getMoveEffect() {
    return MoveEffect.EVENTS;
  }

  public int getAmount() {
    return this.dataTracker.get(AMOUNT);
  }

  public void setAmount(int amount) {
    this.dataTracker.set(AMOUNT, amount);
  }

  @Override
  protected void readCustomDataFromNbt(NbtCompound nbt) {
//    if (nbt.contains("target")) {
//      this.target = this.world.getPlayerByUuid(nbt.getUuid("target"));
//    }
    this.dataTracker.set(AMOUNT, nbt.getInt("amount"));
  }

  @Override
  protected void writeCustomDataToNbt(NbtCompound nbt) {
//    if (this.target != null) {
//      nbt.putUuid("target", this.target.getUuid());
//    }
    nbt.putInt("amount", this.dataTracker.get(AMOUNT));
  }

  @Override
  public Packet<?> createSpawnPacket() {
    return new EntitySpawnS2CPacket(this);
  }
}
