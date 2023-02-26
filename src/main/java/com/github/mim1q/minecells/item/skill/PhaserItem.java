package com.github.mim1q.minecells.item.skill;

import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class PhaserItem extends Item {
  public PhaserItem() {
    super(new FabricItemSettings().maxCount(1).maxDamage(32));
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    if (!user.isPlayer()) {
      return stack;
    }
    PlayerEntity player = (PlayerEntity) user;
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

    boolean canTeleport = teleportBehindTarget(world, player, closestEntity);
    if (world.isClient()) {
      return stack;
    }

    if (canTeleport) {
      stack.damage(1, user, e -> e.sendEquipmentBreakStatus(user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
      player.getItemCooldownManager().set(this, 20 * 5);
    } else {
      player.getItemCooldownManager().set(this, 20);
    }
    return stack;
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
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (user instanceof PlayerEntity player) {
      player.getItemCooldownManager().set(this, 20);
    }
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    if (!world.isClient) {
      return;
    }
    ParticleUtils.addParticle((ClientWorld) world, MineCellsParticles.CHARGE, user.getPos().add(0.0D, 1.25D, 0.0D), Vec3d.ZERO);
    ParticleUtils.addParticle((ClientWorld) world, MineCellsParticles.CHARGE, user.getPos().add(0.0D, 1.25D, 0.0D), Vec3d.ZERO);
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 20;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BLOCK;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    user.playSound(MineCellsSounds.TELEPORT_CHARGE, 1.0F, 1.0F);
    return ItemUsage.consumeHeldItem(world, user, hand);
  }
}
