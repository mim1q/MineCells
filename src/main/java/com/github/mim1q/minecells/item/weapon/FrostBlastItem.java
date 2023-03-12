package com.github.mim1q.minecells.item.weapon;

import com.github.mim1q.minecells.registry.MineCellsStatusEffects;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class FrostBlastItem extends Item {
  public FrostBlastItem(Settings settings) {
    super(settings);
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    stack.damage(1, user, e -> e.sendToolBreakStatus(user.getActiveHand()));
    if (user.world.isClient()) {
      for (int i = 0; i < 20; i++) {
        Vec3d pos = user.getPos().add(0.0D, 1.25D, 0.0D);
        Vec3d vel = Vec3d.fromPolar(
          user.getPitch() + (user.getRandom().nextFloat() - 0.5F) * 45.0F,
          user.getYaw() + (user.getRandom().nextFloat() - 0.5F) * 45.0F
        );
        ParticleUtils.addParticle(
          (ClientWorld) world,
          ParticleTypes.SNOWFLAKE,
          pos,
          vel.multiply(0.25D + user.getRandom().nextDouble() * 0.25D).add(0.0D, 0.1D, 0.0D)
        );
      }
      return stack;
    }
    if (user.isPlayer()) {
      ((PlayerEntity)user).getItemCooldownManager().set(this, 20 * 15);
    }
    Set<LivingEntity> entities = new HashSet<>();
    for (int i = 1; i <= 3; ++i) {
      Vec3d searchPos = user.getPos().add(user.getRotationVector().multiply(i * 1.5F));
      Box searchBox = Box.of(searchPos, 1.0D + 0.75D * i, 1.5D, 1.0D + 0.75D * i);
      entities.addAll(world.getEntitiesByClass(LivingEntity.class, searchBox, e -> e != user));
    }
    for (LivingEntity entity : entities) {
      applyFreeze(entity);
      entity.damage(DamageSource.FREEZE, 6.0F);
    }
    return stack;
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 20;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  public static void applyFreeze(LivingEntity entity) {
    int duration = 20 * 5;
    World world = entity.getWorld();
    if (world.getBlockState(entity.getBlockPos()).getFluidState().isIn(FluidTags.WATER)) {
      world.setBlockState(entity.getBlockPos(), Blocks.FROSTED_ICE.getDefaultState());
      duration = 20 * 10;
    }
    entity.addStatusEffect(new StatusEffectInstance(MineCellsStatusEffects.FROZEN, duration, 0, false, false, true));
  }
}
