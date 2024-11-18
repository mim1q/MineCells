package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorContext;
import dev.mim1q.gimm1q.valuecalculators.parameters.ValueCalculatorParameter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SingleUseProjectileItem extends Item implements CustomArrowShooter {
  private final CustomArrowType arrowType;

  public SingleUseProjectileItem(Settings settings, CustomArrowType arrowType) {
    super(settings);
    this.arrowType = arrowType;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    var stack = user.getStackInHand(hand);
    if (!world.isClient) {
      shoot(world, user, stack);
      var context = ValueCalculatorContext.create()
        .with(ValueCalculatorParameter.HOLDER, user)
        .with(ValueCalculatorParameter.HOLDER_STACK, stack);
      user.getItemCooldownManager().set(this, arrowType.getCooldown(context));
    }
    stack.decrement(1);
    return TypedActionResult.consume(stack);
  }

  protected void shoot(World world, LivingEntity user, ItemStack stack) {
    world.playSound(null, user.getBlockPos(), MineCellsSounds.LEAPING_ZOMBIE_RELEASE, SoundCategory.PLAYERS, 0.5f, 1.3f);

    var velocity = user.getRotationVec(1f);
    spawnArrow(world, (PlayerEntity) user, stack, velocity);
  }

  protected void spawnArrow(World world, PlayerEntity user, ItemStack stack, Vec3d velocity) {
    var arrow = new CustomArrowEntity(world, user, arrowType, user.getEyePos(), stack);
    var context = ValueCalculatorContext.create()
      .with(ValueCalculatorParameter.HOLDER, user)
      .with(ValueCalculatorParameter.HOLDER_STACK, stack);
    arrow.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ(), arrowType.getSpeed(context), arrowType.getSpread(context));
    world.spawnEntity(arrow);
  }

  @Override
  public CustomArrowType getArrowType() {
    return arrowType;
  }
}
