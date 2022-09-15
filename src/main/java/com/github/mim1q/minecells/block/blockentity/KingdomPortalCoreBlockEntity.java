package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.dimenion.KingdomDimensionUtils;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class KingdomPortalCoreBlockEntity extends BlockEntity {

  private static final ParticleEffect PARTICLE = new DustParticleEffect(new Vec3f(1.0F, 0.5F, 0.0F), 1.0F);
  private Vec3d offset = Vec3d.ZERO;
  private Direction direction = Direction.NORTH;
  private Vec3d widthVector = new Vec3d(1.0D, 0.0D, 0.0D);
  private Box box = Box.of(Vec3d.of(this.pos), 1.0D, 1.0D, 1.0D);

  public final AnimationProperty litProgress = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public KingdomPortalCoreBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.KINGDOM_PORTAL_CORE_BLOCK_ENTITY, pos, state);
  }

  private Vec3d calculateOffset() {
    Direction dir = getDirection();
    float offsetX = 0.0F;
    float offsetZ = 0.0F;

    if (dir == Direction.NORTH) {
      offsetX = 0.0F;
      offsetZ = 0.5F;
    } else if (dir == Direction.SOUTH) {
      offsetX = 1.0F;
      offsetZ = 0.5F;
    } else if (dir == Direction.EAST) {
      offsetX = 0.5F;
      offsetZ = 0.0F;
    } else if (dir == Direction.WEST) {
      offsetX = 0.5F;
      offsetZ = 1.0F;
    }

    return new Vec3d(offsetX, 1.0F, offsetZ);
  }

  public void update(BlockState state) {
    direction = state.get(KingdomPortalCoreBlock.DIRECTION);
    offset = calculateOffset();
    widthVector = calculateWidthVector();
    box = calculateBox();
  }

  public Vec3d calculateWidthVector() {
    return Vec3d.of(getDirection().rotateYClockwise().getVector());
  }

  public Box calculateBox() {
    Vec3d pos = Vec3d.of(this.getPos()).add(offset);
    Vec3d size = widthVector;
    return Box.of(pos, size.x * 2.5D, 2.5D, size.z * 1.8D).expand(0.25D);
  }

  public static void tick(World world, BlockPos pos, BlockState state, KingdomPortalCoreBlockEntity blockEntity) {
    blockEntity.update(state);
    if (!world.isClient()) {
      List<PlayerEntity> list = world.getEntitiesByClass(PlayerEntity.class, blockEntity.getBox(), Objects::nonNull);
      ServerWorld serverWorld = (ServerWorld) world;
      for (PlayerEntity player : list) {
        player.setVelocity(player.getVelocity().multiply(0.0D, 1.0D, 0.0D));
        KingdomDimensionUtils.teleportPlayer((ServerPlayerEntity) player, serverWorld, blockEntity);
      }
    } else {
      ClientWorld clientWorld = (ClientWorld) world;
      blockEntity.litProgress.update(world.getTime());
      if (state.get(KingdomPortalCoreBlock.LIT)) {
        blockEntity.litProgress.setupTransitionTo(1.0F, 20);
      } else {
        blockEntity.litProgress.setupTransitionTo(0.0F, 1);
      }
      float progress = blockEntity.litProgress.getProgress();
      float value = blockEntity.litProgress.getValue();
      if (progress < 1.0F) {
        blockEntity.spawnParticleCircle(
          clientWorld,
          Vec3d.of(blockEntity.getPos()).add(blockEntity.getOffset()),
          1.25F,
          progress
        );
      }
      if (value >= 0.9F && value < 1.0F) {
        ParticleUtils.addAura(
          clientWorld,
          Vec3d.of(blockEntity.getPos()).add(blockEntity.getOffset()),
          PARTICLE,
          10,
          1.0F,
          1.0F
        );
        ParticleUtils.addAura(
          clientWorld,
          Vec3d.of(blockEntity.getPos()).add(blockEntity.getOffset()),
          PARTICLE,
          10,
          1.75,
          1.0F
        );
      }
    }
  }

  public void spawnParticleCircle(ClientWorld world, Vec3d center, double radius, double circleFraction) {
    double angle = Math.PI * 2.0D * circleFraction;
    double xz = -Math.cos(angle) * radius;
    double y = -Math.sin(angle) * radius;
    Vec3d width = getWidthVector();
    ParticleUtils.addParticle(world, PARTICLE, center.add(xz * width.x, y, xz * width.z), Vec3d.ZERO);
  }
  public Direction getDirection() {
    return direction;
  }

  public Vec3d getOffset() {
    return offset;
  }

  public Vec3d getWidthVector() {
    return widthVector;
  }

  public Box getBox() {
    return box;
  }
}
