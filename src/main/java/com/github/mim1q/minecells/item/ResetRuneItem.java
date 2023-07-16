package com.github.mim1q.minecells.item;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ResetRuneItem extends Item {
  private static final ParticleEffect PARTICLE = MineCellsParticles.SPECKLE.get(0x97FFA7);

  public ResetRuneItem(Settings settings) {
    super(settings);
  }

  @Override
  public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
    if (user instanceof ServerPlayerEntity player && world instanceof ServerWorld serverWorld) {
      var data = MineCellsData.getPlayerData(player, serverWorld, null);
      var dimension = MineCellsDimension.of(world);
      var runes = data.activatedSpawnerRunes.get(dimension);
      if (runes != null) {
        runes.clear();
        MineCellsData.syncCurrentPlayerData(player, serverWorld);
      }
      var tpPos = dimension.getTeleportPosition(user.getBlockPos(), serverWorld);
      player.teleport(serverWorld, tpPos.getX(), tpPos.getY(), tpPos.getZ(), player.getYaw(), player.getPitch());
      player.getItemCooldownManager().set(this, 20 * 1);
      world.playSound(null, user.getX(), user.getY(), user.getZ(), MineCellsSounds.TELEPORT_RELEASE, SoundCategory.PLAYERS, 1.0F, 1.0F);
      stack.setCount(stack.getCount() - 1);
    }
    return stack;
  }

  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 40;
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    super.onStoppedUsing(stack, world, user, remainingUseTicks);
    if (user instanceof ServerPlayerEntity player) {
      player.getItemCooldownManager().set(this, 40);
    }
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BLOCK;
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    if (world.isClient) {
      ParticleUtils.addAura(
        (ClientWorld) world,
        user.getPos().add(0.0, 1.25, 0.0),
        PARTICLE,
        MathHelper.clamp(20 - remainingUseTicks, 5, 20),
        1.25F,
        -0.05
      );
    }
    super.usageTick(world, user, stack, remainingUseTicks);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (user.getItemCooldownManager().isCoolingDown(this) || !MineCellsDimension.isMineCellsDimension(world)) {
      return TypedActionResult.fail(user.getStackInHand(hand));
    }
    world.playSound(user.getX(), user.getY(), user.getZ(), MineCellsSounds.TELEPORT_CHARGE, SoundCategory.PLAYERS, 1.0F, 1.0F, true);
    return ItemUsage.consumeHeldItem(world, user, hand);
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {

  }
}
