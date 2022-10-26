package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import com.github.mim1q.minecells.registry.MineCellsItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

  @Shadow public abstract float getAttackCooldownProgress(float baseTime);

  private static final TrackedData<Integer> CELL_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
  private int kingdomPortalCooldown = 0;

  protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "initDataTracker", at = @At("TAIL"))
  public void initDataTracker(CallbackInfo ci) {
    this.dataTracker.startTracking(CELL_AMOUNT, 0);
  }

  public int getCells() {
    return this.dataTracker.get(CELL_AMOUNT);
  }

  public void setCells(int amount) {
    this.dataTracker.set(CELL_AMOUNT, amount);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  public void tick(CallbackInfo ci) {
    if (kingdomPortalCooldown > 0) {
      kingdomPortalCooldown--;
    }
  }

  @Inject(method = "resetLastAttackedTicks", at = @At("HEAD"), cancellable = true)
  public void resetLastAttackedTicks(CallbackInfo ci) {
    if (this.shouldCancelSwing()) {
      ci.cancel();
    }
  }

  @Inject(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
  public void attack(Entity target, CallbackInfo ci) {
    if (this.shouldCancelSwing()) {
      ci.cancel();
    }
  }

  @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
  protected void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
    nbt.putInt("cells", this.getCells());
    nbt.putInt("kingdomPortalCooldown", kingdomPortalCooldown);
  }

  @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
  protected void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
    this.setCells(nbt.getInt("cells"));
    kingdomPortalCooldown = nbt.getInt("kingdomPortalCooldown");
  }

  public boolean shouldCancelSwing() {
    float cooldown = this.getAttackCooldownProgress(0.0f);
    return this.getMainHandStack().isOf(MineCellsItems.CURSED_SWORD) && cooldown < 1.0f && cooldown > 0.0f;
  }

  public void setKingdomPortalCooldown(int cooldown) {
    kingdomPortalCooldown = cooldown;
  }

  public int getKingdomPortalCooldown() {
    return kingdomPortalCooldown;
  }

  public boolean canUseKingdomPortal() {
    return kingdomPortalCooldown == 0;
  }

  @Override
  protected void drop(DamageSource source) {
    super.drop(source);
    int amount = this.getCells() / 2;
    if (amount > 0) {
      CellEntity.spawn(this.world, this.getPos(), amount);
    }
  }
}
