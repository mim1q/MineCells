package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsParticles;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.github.mim1q.minecells.util.MathUtils.radians;

public class RiftBlockEntity extends MineCellsBlockEntity {
  public RiftBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.RIFT, pos, state);
  }

  public float getRotation(float deltaTime) {
    if (getWorld() == null) return 0.0f;
    return (getWorld().getTime() + deltaTime) * 0.5f;
  }

  private void tick(World world, BlockPos pos, BlockState state) {
    if (!world.isClient) return;

    var center = Vec3d.ofBottomCenter(pos).add(0.0, 0.5, 0.0);
    var theta = world.getTime() * 0.1f;

    for (int i = 0; i < 2; ++i) {
      var xzPos = Math.cos(theta) * 0.51;
      var particlePos = new Vec3d(
        xzPos * Math.cos(radians(getRotation(0))),
        Math.sin(theta) * 1.02,
        xzPos * -Math.sin(radians(getRotation(0)))
      );
      ParticleUtils.addParticle(
        (ClientWorld) world,
        MineCellsParticles.SPECKLE.get(0x33DDFF),
        center.add(particlePos),
        particlePos.normalize().multiply(world.getRandom().nextFloat() * 0.01)
      );
      theta += MathHelper.PI;
    }
  }

  public static void tick(World world, BlockPos pos, BlockState state, RiftBlockEntity blockEntity) {
    blockEntity.tick(world, pos, state);
  }
}
