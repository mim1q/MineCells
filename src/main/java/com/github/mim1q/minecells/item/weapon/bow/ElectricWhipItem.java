package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.valuecalculators.ModValueCalculators;
import dev.mim1q.gimm1q.valuecalculators.ValueCalculator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class ElectricWhipItem extends Item implements WeaponWithAbility {
  private static final ValueCalculator ABILITY_DAMAGE_CALCULATOR = ModValueCalculators.of("spells/electric_whip", "damage", 0.0);
  private static final ValueCalculator ABILITY_COOLDOWN_CALCULATOR = ModValueCalculators.of("spells/electric_whip", "cooldown", 0.0);

  public ElectricWhipItem(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

    if (world.isClient()) {
      return TypedActionResult.success(user.getStackInHand(hand));
    }

    var userEyePos = user.getEyePos().subtract(0, 0.5, 0);
    var direction = user.getRotationVector();
    var maxDistance = 5.5;
    var maxPos = userEyePos.add(direction.multiply(maxDistance));
    var raycast = world.raycast(new RaycastContext(
      userEyePos,
      maxPos,
      RaycastContext.ShapeType.COLLIDER,
      RaycastContext.FluidHandling.NONE,
      user
    ));

    var particleLength = 4;

    if (raycast.getType() == HitResult.Type.BLOCK) {
      maxPos = raycast.getPos();
      particleLength = (int) raycast.getPos().distanceTo(userEyePos);
    }

    ((ServerWorld) world).spawnParticles(
      MineCellsParticles.ELECTRICITY.get(direction, particleLength, 0x95ddff, 1f),
      userEyePos.getX(),
      userEyePos.getY(),
      userEyePos.getZ(),
      1,
      0.0D,
      0.0D,
      0.0D,
      0.0D
    );

    user.getStackInHand(hand).damage(1, user, e -> e.sendToolBreakStatus(hand));

    var length = maxPos.subtract(userEyePos).length();
    for (var delta = 0.0; delta < length; delta += 0.5) {
      var center = userEyePos.add(direction.multiply(delta));
      var box = Box.of(center, 1.0, 1.0, 1.0);
      var entities = world.getOtherEntities(
        user,
        box,
        it -> it instanceof LivingEntity
      );
      for (var entity : entities) {
        entity.damage(
          MineCellsDamageSource.ELECTRICITY.get(world, user),
          getAbilityDamage(user.getStackInHand(hand), user, (LivingEntity) entity)
        );
        ((LivingEntity) entity).addStatusEffect(
          new StatusEffectInstance(
            MineCellsStatusEffects.ELECTRIFIED,
            20 * 3 + 1,
            2,
            false,
            false,
            true
          ),
          user
        );
      }
    }

    world.playSound(null, user.getBlockPos(), MineCellsSounds.SHOCK, SoundCategory.PLAYERS, 1f, 1f);
    user.getItemCooldownManager().set(this, getAbilityCooldown(user.getStackInHand(hand), user));

    return TypedActionResult.success(user.getStackInHand(hand));
  }

  @Override
  public ValueCalculator getAbilityDamageCalculator() {
    return ABILITY_DAMAGE_CALCULATOR;
  }

  @Override
  public ValueCalculator getAbilityCooldownCalculator() {
    return ABILITY_COOLDOWN_CALCULATOR;
  }
}
