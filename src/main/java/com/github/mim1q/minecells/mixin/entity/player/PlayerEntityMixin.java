package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.world.state.MineCellsData;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityAccessor {

  @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);
  @Shadow public abstract int getXpToDrop();

  private static final TrackedData<Integer> CELL_AMOUNT = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
  private int kingdomPortalCooldown = 0;
  private int balancedBladeStacks = 0;
  private int balancedBladeTimer = 0;
  private int minecells$invincibilityFrames = 0;
  private PlayerSpecificMineCellsData mineCellsPlayerData = new PlayerSpecificMineCellsData(new NbtCompound());

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
    if (balancedBladeTimer > 0) {
      balancedBladeTimer--;
    } else {
      balancedBladeStacks = 0;
    }
    if (minecells$invincibilityFrames > 0) {
      minecells$invincibilityFrames--;
    }
  }

  @Override
  public PlayerSpecificMineCellsData getMineCellsData() {
    return mineCellsPlayerData;
  }

  @Override
  public MineCellsData.PlayerData getCurrentMineCellsPlayerData() {
    return mineCellsPlayerData.get(this.getBlockPos());
  }

  @Override
  public void setMineCellsData(PlayerSpecificMineCellsData data) {
    mineCellsPlayerData = data;
  }

  @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
  private void minecells$isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
    if (minecells$invincibilityFrames > 0) {
      cir.setReturnValue(true);
    }
  }

  @ModifyVariable(
    method = "attack",
    at = @At(value = "STORE", ordinal = 1),
    ordinal = 0
  )
  private float modifyDamage(float original, Entity target) {
    var stack = this.getMainHandStack();
    var damage = original;
    if (stack.getItem() instanceof CrittingWeapon critWeapon
      && target instanceof LivingEntity livingTarget
    ) {
      damage += critWeapon.getExtraDamage(stack, livingTarget, this);

      if (critWeapon.canCrit(stack, livingTarget, this)) {
        if (critWeapon.shouldPlayCritSound(stack, livingTarget, this)) {
          getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), MineCellsSounds.CRIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        damage += critWeapon.getAdditionalCritDamage(stack, livingTarget, this);
      }
    }
    return damage;
  }

  @Inject(method = "damage", at = @At("HEAD"))
  private void minecells$injectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
    this.balancedBladeStacks = 0;
  }

  @Inject(method = "isBlockBreakingRestricted", at = @At("HEAD"), cancellable = true)
  public void isBlockBreakingRestricted(World world, BlockPos pos, GameMode gameMode, CallbackInfoReturnable<Boolean> cir) {
    if (((LivingEntityAccessor) this).getMineCellsFlag(MineCellsEffectFlags.DISARMED)) {
      cir.setReturnValue(true);
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

  @Override
  public void addBalancedBladeStack() {
    if (balancedBladeStacks < 9) {
      balancedBladeStacks++;
    }
    balancedBladeTimer = 20 * 5;
  }

  @Override
  public int getBalancedBladeStacks() {
    return balancedBladeStacks;
  }

  @Override
  public void setInvincibilityFrames(int frames) {
    this.minecells$invincibilityFrames = frames;
  }
}
