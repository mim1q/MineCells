package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.KingdomPortalCoreBlock;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KingdomPortalCoreBlockEntity extends MineCellsBlockEntity {
  private static final ParticleEffect PARTICLE = new DustParticleEffect(new Vec3f(1.0F, 0.5F, 0.0F), 1.0F);
  private Vec3d offset = null;
  private Vec3d widthVector = null;
  private Box box = null;
  private boolean upstream = false;
  private RegistryKey<World> dimension = RegistryKey.of(Registry.WORLD_KEY, MineCells.createId("prison"));

  public final AnimationProperty litProgress = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);
  public final AnimationProperty portalOpacity = new AnimationProperty(0.0F, MathUtils::easeInOutQuad);

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

  public void update() {
    offset = calculateOffset();
    widthVector = calculateWidthVector();
    box = calculateBox();
  }

  public Vec3d calculateWidthVector() {
    return Vec3d.of(getDirection().rotateYCounterclockwise().getVector());
  }

  public Box calculateBox() {
    Vec3d pos = Vec3d.of(this.getPos()).add(getOffset());
    Vec3d size = getWidthVector();
    return Box.of(pos, size.x * 1.5D, 1.5D, size.z * 1.5D).expand(0.2D);
  }

  public static void tick(World world, BlockPos pos, BlockState state, KingdomPortalCoreBlockEntity blockEntity) {
    blockEntity.tick(world, pos, state);
  }

  public void tick(World world, BlockPos pos, BlockState state) {
    if (world.isClient()) {
      tickClient((ClientWorld) world);
    } else {
      tickServer((ServerWorld) world);
    }
  }

  protected void tickClient(ClientWorld world) {
    this.litProgress.update(world.getTime());
    if (this.getCachedState().get(KingdomPortalCoreBlock.LIT)) {
      this.litProgress.setupTransitionTo(1.0F, 25.0F);
    } else {
      this.litProgress.setupTransitionTo(0.0F, 0.1F);
    }
    this.spawnParticles(this.litProgress.getProgress());
  }

  protected void spawnParticles(float progress) {
    if (progress > 0.0F && progress < 0.85F) {
      this.spawnParticleCircle(
        (ClientWorld) this.world,
        Vec3d.of(this.getPos()).add(this.getOffset()),
        1.25D,
        progress
      );
    }
    if (progress > 0.8F && progress < 1.0F) {
      this.spawnParticleSphere(
        (ClientWorld) this.world,
        Vec3d.of(this.getPos()).add(this.getOffset())
      );
    }
  }

  protected void tickServer(ServerWorld world) {
    if (this.getCachedState().get(KingdomPortalCoreBlock.LIT)) {
      tryTeleportPlayer(world);
    } else {
      tryActivatePortal(world);
    }
  }

  private void tryActivatePortal(ServerWorld world) {
    Vec3d offset = Vec3d.of(this.getDirection().getVector());
    Box newBox = this.getBox().expand(offset.getX() * 8.0D, 0.0D, offset.getZ() * 8.0D);

    List<ServerPlayerEntity> players = world
      .getNonSpectatingEntities(ServerPlayerEntity.class, newBox);

    var advancementLoader = world.getServer().getAdvancementLoader();

    boolean canActivate = false;
    for (ServerPlayerEntity player : players) {
      if (player.getAdvancementTracker().getProgress(
        advancementLoader.get(new Identifier("minecraft:story/mine_diamond"))
      ).isDone()) {
        canActivate = true;
        Vec3d pos = Vec3d.ofCenter(this.getPos());
        world.playSound(null, pos.x, pos.y, pos.z, MineCellsSounds.PORTAL_ACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        break;
      }
    }
    if (!canActivate) {
      return;
    }
    world.setBlockState(pos, this.getCachedState().with(KingdomPortalCoreBlock.LIT, true));
  }

  private void tryTeleportPlayer(ServerWorld world) {
    ServerPlayerEntity player = (ServerPlayerEntity) world.getClosestPlayer(
      TargetPredicate.createNonAttackable(),
      this.getPos().getX(),
      this.getPos().getY(),
      this.getPos().getZ()
    );
    if (player == null) {
      return;
    }
    PlayerEntityAccessor accessor = (PlayerEntityAccessor) player;
    if (this.box.intersects(player.getBoundingBox())) {
//      if (accessor.canUseKingdomPortal()) {
//        if (this.upstream) {
//          MineCellsPortal.teleportPlayerUpstream(player, world);
//        } else {
//          MineCellsPortal.teleportPlayerDownstream(player, world, this.getPos(), this.getDirection(), this.getDimensionKey());
//        }
//      } else {
//        accessor.setKingdomPortalCooldown(50);
//      }
    }
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    if (nbt.contains("dimensionKey")) {
      this.dimension = RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("dimensionKey")));
    }
    this.upstream = nbt.getBoolean("upstream");
    this.update();
    World world = this.getWorld();
    if (world != null) {
      world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
    }
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    if (this.dimension != null) {
      nbt.putString("dimensionKey", this.dimension.getValue().toString());
    }
    nbt.putBoolean("upstream", this.upstream);
  }

  @Nullable
  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return createNbt();
  }

  private void spawnParticleSphere(ClientWorld world, Vec3d pos) {
    ParticleUtils.addAura(world, pos, PARTICLE, 10, 1.5, 1.0F);
  }

  public void spawnParticleCircle(ClientWorld world, Vec3d center, double radius, double circleFraction) {
    double angle = Math.PI * 2.0D * circleFraction * 1.25D;
    double xz = -Math.cos(angle) * radius;
    double y = -Math.sin(angle) * radius;
    Vec3d width = getWidthVector();
    ParticleUtils.addParticle(world, PARTICLE, center.add(xz * width.x, y, xz * width.z), Vec3d.ZERO);
  }

  public Direction getDirection() {
    return this.getCachedState().get(KingdomPortalCoreBlock.DIRECTION);
  }

  public Vec3d getOffset() {
    if (offset == null) {
      update();
    }
    return offset;
  }

  public Vec3d getWidthVector() {
    if (widthVector == null) {
      update();
    }
    return widthVector;
  }

  public Box getBox() {
    if (box == null) {
      update();
    }
    return box;
  }

  public RegistryKey<World> getDimensionKey() {
    return this.dimension;
  }

  public boolean isUpstream() {
    return this.upstream;
  }
}
