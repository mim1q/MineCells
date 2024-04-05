package com.github.mim1q.minecells.entity.nonliving.projectile;

import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType.ArrowBlockHitContext;
import com.github.mim1q.minecells.item.weapon.bow.CustomArrowType.ArrowEntityHitContext;
import com.github.mim1q.minecells.registry.MineCellsEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.entity.data.TrackedDataHandlerRegistry.STRING;

public class CustomArrowEntity extends PersistentProjectileEntity {
  public static final TrackedData<String> ARROW_TYPE = DataTracker.registerData(CustomArrowEntity.class, STRING);

  private CustomArrowType arrowType = CustomArrowType.DEFAULT;
  private Vec3d shotFromPos;
  private ItemStack bow;

  public CustomArrowEntity(EntityType<? extends CustomArrowEntity> entityType, World world) {
    super(entityType, world);
  }

  public CustomArrowEntity(World world, PlayerEntity owner, CustomArrowType arrowType, Vec3d shotFromPos, ItemStack bow) {
    super(MineCellsEntities.CUSTOM_ARROW, world);
    this.dataTracker.set(ARROW_TYPE, arrowType.getName());
    setOwner(owner);
    setPosition(owner.getEyePos().subtract(0.0, 0.2, 0.0));
    this.shotFromPos = shotFromPos;
    this.bow = bow.copy();
  }

  @Override
  protected void initDataTracker() {
    super.initDataTracker();
    this.dataTracker.startTracking(ARROW_TYPE, CustomArrowType.DEFAULT.getName());
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient) return;
  }

  @Override
  protected void onEntityHit(EntityHitResult entityHitResult) {
    if (getWorld().isClient || this.getOwner() == null) return;

    if (entityHitResult.getEntity() instanceof LivingEntity target) {
      var entityHitContext = new ArrowEntityHitContext(
        (ServerWorld) getWorld(),
        bow,
        (PlayerEntity) getOwner(),
        target,
        shotFromPos,
        entityHitResult.getPos(),
        this
      );

      var damageAndCrit = arrowType.getDamageAndCrit(entityHitContext);

      target.damage(
        getWorld().getDamageSources().mobProjectile(this, (PlayerEntity) this.getOwner()),
        damageAndCrit.getLeft()
      );

      if (damageAndCrit.getRight()) {
        getWorld().playSound(null, getOwner().getBlockPos(), MineCellsSounds.CRIT, SoundCategory.PLAYERS, 1.0f, 1.0f);
      }

      this.getArrowType().onEntityHit(entityHitContext);
    }
  }

  @Override
  protected void onBlockHit(BlockHitResult blockHitResult) {
    super.onBlockHit(blockHitResult);

    if (getWorld().isClient) return;

    var blockHitContext = new ArrowBlockHitContext(
      (ServerWorld) getWorld(),
      bow,
      (PlayerEntity) getOwner(),
      shotFromPos,
      blockHitResult.getBlockPos(),
      blockHitResult.getPos(),
      this
    );

    this.getArrowType().onBlockHit(blockHitContext);
  }

  public CustomArrowType getArrowType() {
    return arrowType;
  }

  @Override
  protected ItemStack asItemStack() {
    return new ItemStack(Items.ARROW);
  }

  @Override
  public void onTrackedDataSet(TrackedData<?> data) {
    super.onTrackedDataSet(data);

    if (data.equals(ARROW_TYPE)) {
      this.arrowType = CustomArrowType.get(this.dataTracker.get(ARROW_TYPE));
    }
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putString("arrowType", arrowType.getName());
  }

  @Override
  public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    this.dataTracker.set(ARROW_TYPE, nbt.getString("arrowType"));
  }
}
