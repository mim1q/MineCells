package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

import static com.github.mim1q.minecells.registry.MineCellsItems.CELL_INFUSED_STEEL;

public class MultipleNocksBowItem extends CustomBowItem {
  public MultipleNocksBowItem(Settings settings) {
    super(settings, CustomArrowType.MULTIPLE_NOCKS, 3);
  }

  @Override
  protected void shoot(World world, LivingEntity user, ItemStack stack) {
    world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_RELEASE, SoundCategory.PLAYERS, 0.7f, 0.9f);

    var velocity = user.getRotationVec(1f).multiply(arrowType.getSpeed());
    var angle = 15;
    var loaded = getLoadedProjectiles(stack);

    for (int i = 0; i < loaded; i++) {
      spawnArrow(world, (PlayerEntity) user, stack, velocity.rotateY(MathUtils.radians(angle)));
      angle -= 15;
    }

    setLoadedProjectiles(stack, 0);
  }

  @Override
  public boolean canRepair(ItemStack stack, ItemStack ingredient) {
    return ingredient.isOf(CELL_INFUSED_STEEL);
  }
}
