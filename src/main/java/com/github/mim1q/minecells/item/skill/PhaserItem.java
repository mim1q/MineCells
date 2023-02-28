package com.github.mim1q.minecells.item.skill;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PhaserItem extends Item {
  public PhaserItem() {
    super(new FabricItemSettings().maxCount(1).maxDamage(32));
  }

  private static boolean teleportBehindTarget(World world, PlayerEntity player, LivingEntity target) {
    if (target == null) {
      return false;
    }
    var raycast = world.raycast(new RaycastContext(player.getEyePos(), target.getEyePos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
    if (raycast.isInsideBlock()) {
      return false;
    }
    Vec3d targetPos = target.getPos().add(target.getRotationVec(0.0F).multiply(-1.0D, 0.0D, -1.0D).multiply(0.5D + target.getWidth()));
    if (
      world.getBlockState(new BlockPos(targetPos)).isOpaque()
      || world.getBlockState(new BlockPos(targetPos).up()).isOpaque()
    ) {
      return false;
    }
    if (world.isClient()) {
      return true;
    }
    player.setYaw(target.getYaw(0.0F));
    world.playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.TELEPORT_RELEASE, SoundCategory.PLAYERS, 1.0F, 1.0F);
    player.teleport(targetPos.x, targetPos.y, targetPos.z);
    return true;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    Vec3d searchPos = user.getPos().add(user.getRotationVector().multiply(4.0D));
    var closestEntity = world.getClosestEntity(
      LivingEntity.class,
      TargetPredicate.createAttackable(),
      user,
      searchPos.x,
      searchPos.y,
      searchPos.z,
      Box.of(searchPos, 8.0D, 8.0D, 8.0D)
    );

    boolean canTeleport = teleportBehindTarget(world, user, closestEntity);
    if (world.isClient()) {
      return TypedActionResult.success(stack);
    }

    if (canTeleport) {
      stack.damage(1, user, e -> e.sendEquipmentBreakStatus(user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
      user.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.ASSASSINS_STRENGTH, 20 * 5));
      user.getItemCooldownManager().set(this, 20 * 5);
      return TypedActionResult.success(stack);
    }
    user.getItemCooldownManager().set(this, 20);
    return TypedActionResult.fail(stack);
  }
}
