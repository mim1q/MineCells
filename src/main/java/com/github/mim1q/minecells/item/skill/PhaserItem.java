package com.github.mim1q.minecells.item.skill;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.item.weapon.interfaces.WeaponWithAbility;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class PhaserItem extends Item implements WeaponWithAbility {
  public PhaserItem(Settings settings) {
    super(settings);
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
      world.getBlockState(BlockPos.ofFloored(targetPos)).isOpaque()
        || world.getBlockState(BlockPos.ofFloored(targetPos).up()).isOpaque()
    ) {
      return false;
    }
    if (world.isClient()) {
      return true;
    }
    world.playSound(null, player.getX(), player.getY(), player.getZ(), MineCellsSounds.TELEPORT_RELEASE, SoundCategory.PLAYERS, 1.0F, 1.0F);
    target.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.STUNNED, 30, 0, false, false, true));
    ((PlayerEntityAccessor) player).setInvincibilityFrames(10);
    player.teleport((ServerWorld) world, targetPos.x, targetPos.y, targetPos.z, Set.of(), target.headYaw, player.getPitch());
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
      user.getItemCooldownManager().set(this, getAbilityCooldown(stack));
      return TypedActionResult.success(stack);
    }
    user.getItemCooldownManager().set(this, 20);
    return TypedActionResult.fail(stack);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    super.appendTooltip(stack, world, tooltip, context);
    fillTooltip(tooltip, false, "item.minecells.phaser.description", stack);
  }

  @Override
  public float getAbilityDamageCalculator(ItemStack stack) {
    return 0.0F;
  }

  @Override
  public int getAbilityCooldownCalculator(ItemStack stack) {
    return 20 * 5;
  }
}
