package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class HeavyCrossbowItem extends CustomCrossbowItem {
  public HeavyCrossbowItem(Settings settings) {
    super(settings, CustomArrowType.HEAVY_BOLT);
  }

  @Override
  protected void shoot(World world, LivingEntity user, ItemStack stack) {
    world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_RELEASE, SoundCategory.PLAYERS, 0.7f, 0.9f);
    var velocity = user.getRotationVec(1f);
    for (int y = 0; y < 8; y++) {
      spawnArrow(world, (PlayerEntity) user, stack, velocity);
    }
  }
}
