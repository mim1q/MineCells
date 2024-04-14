package com.github.mim1q.minecells.item.weapon.bow;

import com.github.mim1q.minecells.entity.nonliving.projectile.CustomArrowEntity;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission.DISALLOWED;

public class HeavyCrossbowItem extends CustomCrossbowItem {
  public HeavyCrossbowItem(Settings settings) {
    super(settings, CustomArrowType.HEAVY_BOLT, 3);
  }

  @Override
  protected void shoot(World world, LivingEntity user, ItemStack stack) {
    world.playSound(null, user.getBlockPos(), MineCellsSounds.BOW_RELEASE, SoundCategory.PLAYERS, 0.7f, 0.9f);
    var velocity = user.getRotationVec(1f);

    var loaded = getLoadedProjectiles(stack);
    for (int y = 0; y < loaded * 3; y++) {
      spawnArrow(world, (PlayerEntity) user, stack, velocity);
    }

    setLoadedProjectiles(stack, 0);
  }

  @Override
  protected CustomArrowEntity spawnArrow(World world, PlayerEntity user, ItemStack stack, Vec3d velocity) {
    var arrow = super.spawnArrow(world, user, stack, velocity);
    arrow.pickupType = DISALLOWED;
    return arrow;
  }
}
