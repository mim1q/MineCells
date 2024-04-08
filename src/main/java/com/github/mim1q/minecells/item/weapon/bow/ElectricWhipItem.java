package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
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
import net.minecraft.world.World;

public class ElectricWhipItem extends Item {
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
    var raycast = user.raycast(maxDistance, 1, false);

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
          MineCellsDamageSource.AURA.get(world, user),
          1.0f
        );
        ((LivingEntity) entity).addStatusEffect(
          new StatusEffectInstance(
            MineCellsStatusEffects.ELECTRIFIED,
            20 * 5,
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
    user.getItemCooldownManager().set(this, 20);

    return TypedActionResult.success(user.getStackInHand(hand));
  }
}
