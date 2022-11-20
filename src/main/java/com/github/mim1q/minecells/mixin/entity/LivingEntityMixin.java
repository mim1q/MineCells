package com.github.mim1q.minecells.mixin.entity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.effect.MineCellsStatusEffect;
import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import com.github.mim1q.minecells.entity.nonliving.ElevatorEntity;
import com.github.mim1q.minecells.registry.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccessor {

  protected int ticksInSewage = 0;
  protected int droppedCellAmount = 1;
  protected float droppedCellChance = 0.75F;

  @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

  @Shadow public abstract ItemStack getMainHandStack();

  @Shadow public abstract boolean addStatusEffect(StatusEffectInstance effect);

  @Shadow public abstract boolean damage(DamageSource source, float amount);

  @Shadow public abstract void kill();

  @Shadow public abstract void setHealth(float health);

  @Shadow public abstract Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects();

  @Shadow public abstract Identifier getLootTable();

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
    if (!this.world.isClient()) {
      if (this.getMainHandStack().isOf(MineCellsItems.CURSED_SWORD)) {
        this.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.CURSED, 210, 0, false, false, true));
      }
      if (this.checkIfInSewageAndUpdate()) {
        this.ticksInSewage++;
      } else {
        this.ticksInSewage = 0;
      }
    }
  }

  @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
  public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    if (source == MineCellsDamageSource.CURSED) {
      return;
    }
    if (this.hasStatusEffect(MineCellsStatusEffects.CURSED)) {
      world.playSound(null, this.getX(), this.getY(), this.getZ(), MineCellsSounds.CURSE_DEATH, this.getSoundCategory(), 1.0F, 1.0F);
      this.setHealth(0.5f);
      this.damage(MineCellsDamageSource.CURSED, 10.0f);
      cir.setReturnValue(true);
      cir.cancel();
    }
  }

  @Override
  public void clearCurableStatusEffects() {
    Iterator<StatusEffectInstance> iterator = this.getActiveStatusEffects().values().iterator();
    while(iterator.hasNext()) {
      StatusEffectInstance statusEffectInstance = iterator.next();
      if (statusEffectInstance.getEffectType() instanceof MineCellsStatusEffect effect && !effect.isCurable()) {
        continue;
      }
      ((LivingEntityInvoker) this).invokeOnStatusEffectRemoved(statusEffectInstance);
      iterator.remove();
    }
  }

  @Override
  public boolean hasIncurableStatusEffects() {
    for (StatusEffectInstance statusEffectInstance : this.getActiveStatusEffects().values()) {
      if (statusEffectInstance.getEffectType() instanceof MineCellsStatusEffect effect && !effect.isCurable()) {
        return true;
      }
    }
    return false;
  }

  private boolean checkIfInSewageAndUpdate() {
    List<BlockState> states = this.world.getStatesInBoxIfLoaded(this.getBoundingBox()).toList();
    for (BlockState state : states) {
      boolean isAncientSewage = state.getBlock() == MineCellsBlocks.ANCIENT_SEWAGE;
      if (state.getBlock() == MineCellsBlocks.SEWAGE || isAncientSewage) {
        if (this.ticksInSewage % (isAncientSewage ? 10 : 20) == 0) {
          ((LivingEntity)(Object)this).addStatusEffect(new StatusEffectInstance(
            StatusEffects.POISON,
            40,
            isAncientSewage ? 1 : 0)
          );
        }
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onSwimmingStart() {
    if (!this.checkIfInSewageAndUpdate()) {
      super.onSwimmingStart();
    } else {
      this.emitGameEvent(GameEvent.SPLASH);
    }
  }


  @Override
  protected boolean canStartRiding(Entity entity) {
    return entity instanceof ElevatorEntity || super.canStartRiding(entity);
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
        CellEntity.spawn(this.world, this.getPos(), 1);
      }
    }
  }

  @SuppressWarnings("deprecation")
  protected boolean canDropCells() {
    if (!this.world.getGameRules().getBoolean(MineCellsGameRules.MOBS_DROP_CELLS)) {
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
