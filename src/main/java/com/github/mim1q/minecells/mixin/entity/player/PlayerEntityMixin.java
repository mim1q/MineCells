package com.github.mim1q.minecells.mixin.entity.player;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.github.mim1q.minecells.entity.nonliving.CellEntity;
import com.github.mim1q.minecells.entity.player.MineCellsPortalData;
import com.github.mim1q.minecells.item.weapon.interfaces.CrittingWeapon;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
  private static final TrackedData<String> LAST_DIMENSION_TRANSLATION_KEY = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.STRING);
  private int kingdomPortalCooldown = 0;
  private final MineCellsPortalData mineCellsPortalData = new MineCellsPortalData(this);
  private int balancedBladeStacks = 0;
  private int balancedBladeTimer = 0;

  protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "initDataTracker", at = @At("TAIL"))
  public void initDataTracker(CallbackInfo ci) {
    this.dataTracker.startTracking(CELL_AMOUNT, 0);
    this.dataTracker.startTracking(LAST_DIMENSION_TRANSLATION_KEY, "dimension.minecraft.overworld");
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
  }

  @ModifyVariable(
    method = "attack",
    at = @At(value = "STORE", ordinal = 1),
    ordinal = 0
  )
  private float modifyDamage(float original, Entity target) {
    ItemStack stack = this.getMainHandStack();
    if (stack.getItem() instanceof CrittingWeapon critWeapon
        && target instanceof LivingEntity livingTarget
        && critWeapon.canCrit(stack, livingTarget, this)
    ) {
      if (critWeapon.shouldPlayCritSound(stack, livingTarget, this)) {
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), MineCellsSounds.CRIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
      }
      return original + critWeapon.getAdditionalCritDamage(stack, livingTarget, this);
    }
    return original;
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
    nbt.put("mineCellsPortalData", this.mineCellsPortalData.toNbt());

  }

  @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
  protected void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
    this.setCells(nbt.getInt("cells"));
    kingdomPortalCooldown = nbt.getInt("kingdomPortalCooldown");
    this.mineCellsPortalData.fromNbt(nbt.getCompound("mineCellsPortalData"));
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

  @Override
  public MineCellsPortalData getMineCellsPortalData() {
    return this.mineCellsPortalData;
  }

  @Override
  public void setLastDimensionTranslationKey(String key) {
    this.dataTracker.set(LAST_DIMENSION_TRANSLATION_KEY, key);
  }

  @Override
  public String getLastDimensionTranslationKey() {
    return this.dataTracker.get(LAST_DIMENSION_TRANSLATION_KEY);
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
}
