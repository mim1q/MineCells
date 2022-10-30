package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.entity.nonliving.TentacleWeaponEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TentacleItem extends ToolItem {
  public TentacleItem(Settings settings) {
    super(ToolMaterials.IRON, settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (!world.isClient) {
      Entity entity = TentacleWeaponEntity.create(world, user);
      if (entity != null) {
        world.spawnEntity(entity);
      }
      return TypedActionResult.success(stack, true);
    }
    return super.use(world, user, hand);
  }
}
