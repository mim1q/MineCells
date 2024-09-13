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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
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
    this.setSilent(true);

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
    if (getWorld().isClient && !inGround) {
      var particle = this.getArrowType().getParticle();
      if (particle != null) {
        var reverseVelocity = getVelocity().multiply(-0.1);
        getWorld().addParticle(
          particle,
          getX(), getY(), getZ(),
          reverseVelocity.x, reverseVelocity.y, reverseVelocity.z
        );
      }
    }

    if (getWorld() instanceof ServerWorld serverWorld && age > arrowType.getMaxAge()) {
      this.discard();
      var particle = this.getArrowType().getParticle();
      if (particle == null) particle = ParticleTypes.CRIT;
      serverWorld.spawnParticles(
        particle,
        getX(), getY(), getZ(),
        3, 0.05, 0.05, 0.05, 0.05
      );
    }
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

      var damage = arrowType.getDamage();
      if (arrowType.shouldCrit(entityHitContext)) {
        getWorld().playSound(null, getOwner().getBlockPos(), MineCellsSounds.CRIT, SoundCategory.PLAYERS, 1f, 1f);
        damage += arrowType.getAdditionalCritDamage();
      }
      target.damage(arrowType.getDamageSource(getWorld(), this, (LivingEntity) getOwner()), damage);

      this.getArrowType().onEntityHit(entityHitContext);
      this.discard();
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
      blockHitResult.getSide(),
      this
    );

    this.getArrowType().onBlockHit(blockHitContext);
  }

  public CustomArrowType getArrowType() {
    return arrowType;
  }

  @Override
  protected ItemStack asItemStack() {
    return arrowType.getAmmoItem().map(ItemStack::new).orElse(ItemStack.EMPTY);
  }

  @Override
  protected Text getDefaultName() {
    return arrowType.getTranslation();
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
