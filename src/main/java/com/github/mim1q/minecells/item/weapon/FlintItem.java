package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.nonliving.ShockwavePlacer;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FlintItem extends SwordItem implements WeaponWithAbility {
  public FlintItem(int attackDamage, float attackSpeed, Settings settings) {
    super(ToolMaterials.IRON, attackDamage, attackSpeed, settings);
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    var tick = (3600 * 20) - remainingUseTicks;
    if (world.isClient()) {
      ParticleUtils.addAura((ClientWorld) world, user.getPos().add(0.0D, 1.0D, 0.0D), ParticleTypes.FLAME, 1, 2.0, -0.1);
      if (tick == 20) {
        user.playSound(MineCellsSounds.CRIT, 0.5F, 0.9F);
      }
    }
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient()) return;

    var tick = (3600 * 20) - remainingUseTicks;
    if (user.isPlayer()) {
      var player = (PlayerEntity) user;
      player.getItemCooldownManager().set(this, tick >= 20 ? getAbilityCooldown(stack) : 20);
    } else return;

    if (tick < 20) return;

    var offset = MathUtils.vectorRotateY(new Vec3d(1.0, 0.0, 0.0), MathUtils.radians(user.getYaw()));
    user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20, 0, false, false, false));
    var placer = ShockwavePlacer.createLine(
      world,
      user.getPos(),
      user.getPos().add(offset.multiply(10)),
      1.5F,
      MineCellsBlocks.SHOCKWAVE_FLAME_PLAYER.getDefaultState(),
      user.getUuid(),
      getAbilityDamage(stack)
    );
    world.spawnEntity(placer);
    world.getEntitiesByClass(LivingEntity.class, Box.of(user.getPos(), 3.0, 2.0, 3.0), e -> e != user).forEach(entity -> {
      entity.damage(world.getDamageSources().playerAttack((PlayerEntity) user), getAbilityDamage(stack) * 2);
      if (entity.squaredDistanceTo(user) < 4.0) {
        var knockback = user.getPos().subtract(entity.getPos()).normalize();
        entity.takeKnockback(0.5, knockback.x, knockback.z);
      }
    });
    var flintHand = user.getStackInHand(Hand.MAIN_HAND).isOf(this) ? Hand.MAIN_HAND : Hand.OFF_HAND;
    user.swingHand(flintHand, true);
    world.playSound(null, user.getX(), user.getY(), user.getZ(), MineCellsSounds.FLINT_RELEASE, SoundCategory.PLAYERS, 1.0F, 1.0F);
    world.playSound(null, user.getX(), user.getY(), user.getZ(), MineCellsSounds.HIT_FLOOR, SoundCategory.PLAYERS, 1.0F, 1.0F);
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    return stack;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 3600 * 20;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    world.playSound(null, user.getX(), user.getY(), user.getZ(), MineCellsSounds.FLINT_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public float getBaseAbilityDamage(ItemStack stack) {
    return 3;
  }

  @Override
  public int getBaseAbilityCooldown(ItemStack stack) {
    return 100;
  }
}
