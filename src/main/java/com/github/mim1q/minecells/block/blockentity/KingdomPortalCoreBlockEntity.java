package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.dimenion.KingdomDimensionUtils;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class KingdomPortalCoreBlockEntity extends BlockEntity {

  private static final ParticleEffect PARTICLE = new DustParticleEffect(new Vec3f(1.0F, 0.5F, 0.0F), 1.0F);
  private Vec3d offset = Vec3d.ZERO;
  private Direction direction = Direction.NORTH;
  private Vec3d widthVector = new Vec3d(1.0D, 0.0D, 0.0D);
  private Box box = Box.of(Vec3d.of(this.pos), 1.0D, 1.0D, 1.0D);

  private TeleportTarget teleportTarget = null;


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
        if (blockEntity.teleportTarget == null && !KingdomDimensionUtils.isKingdom(world)) {
          ServerWorld kingdom = KingdomDimensionUtils.getKingdom(serverWorld);
          blockEntity.teleportTarget = KingdomDimensionUtils.findTeleportTarget(pos, kingdom);
          System.out.println(blockEntity.teleportTarget);
          if (blockEntity.teleportTarget != null) {
            KingdomDimensionUtils.spawnPortal(kingdom, new BlockPos(blockEntity.teleportTarget.position));
            KingdomDimensionUtils.teleportPlayer((ServerPlayerEntity) player, serverWorld, blockEntity);
          }
        } else {
          KingdomDimensionUtils.teleportPlayer((ServerPlayerEntity) player, serverWorld, blockEntity);
        }
      }
    }
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

  public TeleportTarget getTeleportTarget() {
    return teleportTarget;
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    if (!nbt.getBoolean("hasTarget")) {
      return;
    }
    teleportTarget = new TeleportTarget(
      new Vec3d(
        nbt.getDouble("targetX"),
        nbt.getDouble("targetY"),
        nbt.getDouble("targetZ")
      ),
      Vec3d.ZERO,
      0.0F,
      0.0F
    );
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    nbt.putBoolean("hasTarget", teleportTarget != null);
    System.out.println(teleportTarget);
    if (teleportTarget == null) {
      return;
    }
    nbt.putDouble("targetX", teleportTarget.position.x);
    nbt.putDouble("targetY", teleportTarget.position.y);
    nbt.putDouble("targetZ", teleportTarget.position.z);
  }
}
