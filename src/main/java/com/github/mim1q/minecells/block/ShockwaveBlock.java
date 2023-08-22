package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.network.s2c.ShockwaveParticlesS2CPacket;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

public class ShockwaveBlock extends Block {
  public ShockwaveBlock(Settings settings) {
    super(settings);
  }

  @Override
  @SuppressWarnings("deprecation")
  public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
    super.scheduledTick(state, world, pos, random);
    PlayerLookup.tracking(world, pos).forEach(player -> ShockwaveParticlesS2CPacket.send(player, this, pos,true));
    world.breakBlock(pos, false);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    var downState = world.getBlockState(pos.down());
    return world.getBlockState(pos).isReplaceable() && downState.isSideSolidFullSquare(world, pos.down(), Direction.UP);
  }

  public void spawnShockwaveStartParticles(ClientWorld world, BlockPos pos) {
    ParticleUtils.addAura(
      world,
      Vec3d.ofBottomCenter(pos),
      ParticleTypes.FLAME,
      10,
      1.0,
      0.05
    );
  }

  public void spawnShockwaveEndParticles(ClientWorld world, BlockPos pos) {
    ParticleUtils.addAura(
      world,
      Vec3d.ofBottomCenter(pos),
      ParticleTypes.FLAME,
      3,
      0.7,
      0.1
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
