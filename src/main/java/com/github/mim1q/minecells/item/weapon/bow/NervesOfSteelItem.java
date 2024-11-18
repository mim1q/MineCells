package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class NervesOfSteelItem extends CustomBowItem {
  public NervesOfSteelItem(Settings settings) {
    super(settings, CustomArrowType.NERVES_OF_STEEL);
  }

  @Override public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) return;

    var ticks = stack.getMaxUseTime() - remainingUseTicks;
    if (ticks >= 30 && ticks <= 40) {
      stack.getOrCreateNbt().putBoolean("crit", true);
    }

    super.onStoppedUsing(stack, world, user, remainingUseTicks);

    stack.getOrCreateNbt().remove("crit");
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    super.usageTick(world, user, stack, remainingUseTicks);

    if (world.isClient) return;
    var ticks = stack.getMaxUseTime() - remainingUseTicks;

    if (ticks == 29) {
      world.playSound(null, user.getBlockPos(), MineCellsSounds.CRIT, SoundCategory.PLAYERS, 0.5f, 1.2f);
    }
  }
}
