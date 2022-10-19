package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.block.KingdomPortalCoreBlock;
import com.github.mim1q.minecells.dimension.KingdomDimensionUtils;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class KingdomPortalCoreBlockEntity extends BlockEntity {

  private static final ParticleEffect PARTICLE = new DustParticleEffect(new Vec3f(1.0F, 0.5F, 0.0F), 1.0F);
  private Vec3d offset = Vec3d.ZERO;
  private Direction direction = Direction.NORTH;
  private Vec3d widthVector = new Vec3d(1.0D, 0.0D, 0.0D);
  private Box box = Box.of(Vec3d.of(this.pos), 1.0D, 1.0D, 1.0D);

  private BlockPos boundPos = null;

  public final AnimationProperty litProgress = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);
  public final AnimationProperty portalOpacity = new AnimationProperty(0.0F, AnimationProperty.EasingType.IN_OUT_QUAD);

  public KingdomPortalCoreBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.KINGDOM_PORTAL_CORE_BLOCK_ENTITY, pos, state);
  }

  private Vec3d calculateOffset() {
    Direction dir = getDirection();

    if (dir == Direction.NORTH) {
      return new Vec3d(0.0F, 1.0F, 0.5F);
    }
    if (dir == Direction.SOUTH) {
      return new Vec3d(1.0F, 1.0F, 0.5F);
    }
    if (dir == Direction.EAST) {
      return new Vec3d(0.5F, 1.0F, 0.0F);
    }
    return new Vec3d(0.5F, 1.0F, 1.0F);
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
    return Box.of(pos, size.x * 1.8D, 2.5D, size.z * 1.8D).expand(0.25D);
  }

  public static void tick(World world, BlockPos pos, BlockState state, KingdomPortalCoreBlockEntity blockEntity) {
    if (world.getTime() % 20 == 1) {
      blockEntity.update(state);
    }
    if (!world.isClient()) {
      if (!state.get(KingdomPortalCoreBlock.LIT)) {
        List<ServerPlayerEntity> list = world.getEntitiesByClass(ServerPlayerEntity.class, blockEntity.getBox().expand(7.5D), Objects::nonNull);
        for (ServerPlayerEntity player : list) {
          if (player.getAdvancementTracker().getProgress(
            player.getServer().getAdvancementLoader().get(new Identifier("minecraft:story/mine_diamond"))
          ).isDone()) {
            world.setBlockState(pos, state.with(KingdomPortalCoreBlock.LIT, true));
            return;
          }
        }
        return;
      }
      List<PlayerEntity> list = world.getEntitiesByClass(PlayerEntity.class, blockEntity.getBox(), Objects::nonNull);
      ServerWorld serverWorld = (ServerWorld) world;
      for (PlayerEntity player : list) {
        KingdomDimensionUtils.teleportPlayer((ServerPlayerEntity) player, serverWorld, blockEntity);
      }
    } else {
      ClientWorld clientWorld = (ClientWorld) world;
      blockEntity.litProgress.update(world.getTime());
      if (state.get(KingdomPortalCoreBlock.LIT)) {
        blockEntity.litProgress.setupTransitionTo(1.0F, 25);
      } else {
        blockEntity.litProgress.setupTransitionTo(0.0F, 1);
      }
      float progress = blockEntity.litProgress.getProgress();
      float value = blockEntity.litProgress.getValue();
      if (progress < 1.0F) {
        blockEntity.spawnParticleCircle(
          clientWorld,
          Vec3d.of(pos).add(blockEntity.getOffset()),
          1.25F,
          progress
        );
      }
      if (value >= 0.9F && value < 1.0F) {
        spawnParticleSphere(
          clientWorld,
          Vec3d.of(pos).add(blockEntity.getOffset()),
          1.75D
        );
        spawnParticleSphere(
          clientWorld,
          Vec3d.of(pos).add(blockEntity.getOffset()),
          1.0D
        );
      }
    }
  }

  public void setBoundPos(BlockPos pos) {
    boundPos = pos;
    markDirty();
  }

  public BlockPos getBoundPos() {
    return boundPos;
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    int[] boundPosArray = nbt.getIntArray("boundPos");
    if (boundPosArray.length == 3) {
      boundPos = new BlockPos(boundPosArray[0], boundPosArray[1], boundPosArray[2]);
    }
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    if (boundPos != null) {
      nbt.putIntArray("boundPos", new int[]{boundPos.getX(), boundPos.getY(), boundPos.getZ()});
    }
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return createNbt();
  }

  @Nullable
  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  private static void spawnParticleSphere(ClientWorld world, Vec3d pos, double radius) {
    ParticleUtils.addAura(
      world,
      pos,
      PARTICLE,
      10,
      radius,
      1.0F
    );
  }

  public void spawnParticleCircle(ClientWorld world, Vec3d center, double radius, double circleFraction) {
    double angle = Math.PI * 2.0D * circleFraction * 1.25D;
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
