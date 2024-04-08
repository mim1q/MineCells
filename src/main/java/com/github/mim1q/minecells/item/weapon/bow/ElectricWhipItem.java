package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElectricWhipItem extends Item {
  public ElectricWhipItem(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

    if (world.isClient()) {
      world.addImportantParticle(
        MineCellsParticles.ELECTRICITY.get(user.getRotationVector(), 4, 0x95ddff, 1f),
        user.getX(),
        user.getEyeY() - 0.1,
        user.getZ(),
        0.0D,
        0.0D,
        0.0D
      );
      return TypedActionResult.success(user.getStackInHand(hand));
    }

    world.playSound(null, user.getBlockPos(), MineCellsSounds.SHOCK, SoundCategory.PLAYERS, 1f, 1f);
    user.getItemCooldownManager().set(this, 20);


    return TypedActionResult.success(user.getStackInHand(hand));
  }
}
