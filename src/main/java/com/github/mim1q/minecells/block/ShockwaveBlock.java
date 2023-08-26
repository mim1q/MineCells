package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.network.s2c.ShockwaveClientEventS2CPacket;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class ShockwaveBlock extends Block {
  public ShockwaveBlock(Settings settings) {
    super(settings);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    super.scheduledTick(state, world, pos, random);
    PlayerLookup.tracking(world, pos).forEach(player -> ShockwaveClientEventS2CPacket.send(player, this, pos,true));
    world.breakBlock(pos, false);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    var downState = world.getBlockState(pos.down());
    var posState = world.getBlockState(pos);
    return posState.isReplaceable()
      && posState.getFluidState().isEmpty()
      && (downState.isSideSolidFullSquare(world, pos.down(), Direction.UP) || downState.isFullCube(world, pos.down()));
  }

  public abstract void onClientStartShockwave(ClientWorld world, BlockPos pos);
  public abstract void onClientEndShockwave(ClientWorld world, BlockPos pos);

  public static class ShockwaveFlame extends ShockwaveBlock {
    private final boolean playerPlaced;

    public ShockwaveFlame(Settings settings, boolean playerPlaced) {
      super(settings);
      this.playerPlaced = playerPlaced;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
      super.onEntityCollision(state, world, pos, entity);
      var canApply = playerPlaced || !(entity instanceof MineCellsEntity);
      var fireImmune = entity.isFireImmune() || (entity instanceof LivingEntity living && living.hasStatusEffect(StatusEffects.FIRE_RESISTANCE));
      if (canApply && !fireImmune && new Box(pos).offset(0.0, -0.75, 0.0).intersects(entity.getBoundingBox())) {
        entity.setOnFireFor(4);
      }
    }

    @Override
    public void onClientStartShockwave(ClientWorld world, BlockPos pos) {
      world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.15f, 0.6f + world.getRandom().nextFloat() * 0.4F, true);
      ParticleUtils.addAura(
        world,
        Vec3d.ofBottomCenter(pos),
        ParticleTypes.FLAME,
        2,
        1.0,
        0.02
      );
    }

    @Override
    public void onClientEndShockwave(ClientWorld world, BlockPos pos) {
      world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.1f, 0.6f + world.getRandom().nextFloat() * 0.4F, true);
      ParticleUtils.addAura(
        world,
        Vec3d.ofBottomCenter(pos),
        ParticleTypes.FLAME,
        2,
        0.7,
        0.05
      );
      ParticleUtils.addAura(
        world,
        Vec3d.ofBottomCenter(pos),
        ParticleTypes.SMOKE,
        3,
        0.75,
        0.02
      );
    }
  }
}
