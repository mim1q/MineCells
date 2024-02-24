package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.effect.MineCellsStatusEffect;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import com.github.mim1q.minecells.registry.MineCellsGameRules;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {

  protected int droppedCellAmount = 1;
  protected float droppedCellChance = 0.75F;

  @Shadow public abstract ItemStack getMainHandStack();
  @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);
  @Shadow public abstract boolean damage(DamageSource source, float amount);
  @Shadow public abstract void kill();
  @Shadow public abstract Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects();
  @Shadow public abstract Identifier getLootTable();
  @Shadow public abstract ItemStack getOffHandStack();
  @Shadow public abstract boolean removeStatusEffect(StatusEffect type);
  @Shadow public abstract LivingEntity getLastAttacker();

  @Shadow private long lastDamageTime;

  @Unique
  @SuppressWarnings("WrongEntityDataParameterClass")
  private static final TrackedData<Integer> MINECELLS_FLAGS = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);

  public LivingEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  @Inject(method = "initDataTracker()V", at = @At("TAIL"))
  public void initDataTracker(CallbackInfo ci) {
    this.dataTracker.startTracking(MINECELLS_FLAGS, 0);
  }

  @Inject(method = "tick()V", at = @At("HEAD"))
  public void tick(CallbackInfo ci) {
    if (!getWorld().isClient()) {
      if (
        this.getMainHandStack().isOf(MineCellsItems.CURSED_SWORD)
          || this.getOffHandStack().isOf(MineCellsItems.CURSED_SWORD)
      ) {
        this.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.CURSED, 30, 0, false, false, true));
      }
    }
  }

  @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
  private void minecells$injectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (!source.isIn(DamageTypeTags.IS_FREEZING)) {
      this.removeStatusEffect(MineCellsStatusEffects.FROZEN);
    }
    if (getMineCellsFlag(MineCellsEffectFlags.CURSED) && !source.isOf(MineCellsDamageSource.CURSED.key)) {
      getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), MineCellsSounds.CURSE_DEATH, this.getSoundCategory(), 1.0F, 1.0F);
      this.damage(MineCellsDamageSource.CURSED.get(getWorld(), getLastAttacker()), Float.MAX_VALUE);
      cir.setReturnValue(false);
    }
  }

  @Override
  public void clearCurableStatusEffects() {
    Iterator<StatusEffectInstance> iterator = this.getActiveStatusEffects().values().iterator();
    while (iterator.hasNext()) {
      StatusEffectInstance statusEffectInstance = iterator.next();
      if (statusEffectInstance.getEffectType() instanceof MineCellsStatusEffect effect && effect.isIncurable()) {
        continue;
      }
      ((LivingEntityInvoker) this).invokeOnStatusEffectRemoved(statusEffectInstance);
      iterator.remove();
    }
  }

  @Override
  public boolean hasIncurableStatusEffects() {
    for (StatusEffectInstance statusEffectInstance : this.getActiveStatusEffects().values()) {
      if (statusEffectInstance.getEffectType() instanceof MineCellsStatusEffect effect && effect.isIncurable()) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected boolean canStartRiding(Entity entity) {
    return entity instanceof ElevatorEntity || super.canStartRiding(entity);
  }

  @Override
  public long getLastDamageTime() {
    return lastDamageTime;
  }

  public boolean getMineCellsFlag(MineCellsEffectFlags flag) {
    return (this.dataTracker.get(MINECELLS_FLAGS) & flag.getOffset()) != 0;
  }

  public void setMineCellsFlag(MineCellsEffectFlags flag, boolean value) {
    int flags = this.dataTracker.get(MINECELLS_FLAGS);
    if (value) {
      flags |= flag.getOffset();
    } else {
      flags &= ~flag.getOffset();
    }
    this.dataTracker.set(MINECELLS_FLAGS, flags);
  }

  @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
  public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
    this.dataTracker.set(MINECELLS_FLAGS, nbt.getInt("MineCellsFlags"));
  }

  @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
  public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
    nbt.putInt("MineCellsFlags", this.dataTracker.get(MINECELLS_FLAGS));
  }

  @Inject(method = "dropXp()V", at = @At("HEAD"))
  public void dropXp(CallbackInfo ci) {
    if (!canDropCells()) {
      return;
    }
    float chance = this.droppedCellChance * MineCells.COMMON_CONFIG.entities.cellDropChanceModifier;
    for (int i = 0; i < this.droppedCellAmount; i++) {
      if (this.random.nextFloat() < chance) {
        CellEntity.spawn(getWorld(), this.getPos(), 1);
      }
    }
  }

  @SuppressWarnings("deprecation")
  protected boolean canDropCells() {
    if (!getWorld().getGameRules().getBoolean(MineCellsGameRules.MOBS_DROP_CELLS)) {
      return false;
    }
    if (MineCells.COMMON_CONFIG.entities.allMobsDropCells || this.getLootTable().getNamespace().equals("minecells")) {
      return true;
    }
    var key = this.getType().getRegistryEntry().getKey();
    if (key.isEmpty()) {
      return false;
    }
    String id = key.get().getValue().toString();
    return MineCells.COMMON_CONFIG.entities.cellDropWhitelist.contains(id);
  }

  public void mixinSetCellAmountAndChance(int amount, float chance) {
    this.droppedCellAmount = amount;
    this.droppedCellChance = chance;
  }
}
