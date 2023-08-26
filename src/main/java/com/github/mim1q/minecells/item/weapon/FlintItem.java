package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.nonliving.ShockwavePlacer;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
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
    var tick = (3600 * 20) - remainingUseTicks;
    if (user.isPlayer()) {
      PlayerEntity player = (PlayerEntity) user;
      player.getItemCooldownManager().set(this, tick >= 20 ? getAbilityCooldown(stack) : 20);
    }
    if (tick < 20) {
      return;
    }

    var placer = ShockwavePlacer.createLine(
      world,
      user.getPos(),
      user.getPos()
        .add(MathUtils.vectorRotateY(new Vec3d(10.0, 0.0, 0.0), MathUtils.radians(user.getYaw()))),
      1.5F,
      MineCellsBlocks.SHOCKWAVE_FLAME.getDefaultState(),
      user.getUuid(),
      10.0F
    );
    world.spawnEntity(placer);
    user.swingHand(Hand.MAIN_HAND);
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
    user.playSound(MineCellsSounds.CHARGE, 1.0F, 1.0F);
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public float getBaseAbilityDamage(ItemStack stack) {
    return 10;
  }

  @Override
  public int getBaseAbilityCooldown(ItemStack stack) {
    return 100;
  }
}
