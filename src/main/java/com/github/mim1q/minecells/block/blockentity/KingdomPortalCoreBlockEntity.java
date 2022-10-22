package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.KingdomPortalCoreBlock;
import com.github.mim1q.minecells.dimension.MineCellsDimensions;
import com.github.mim1q.minecells.dimension.MineCellsPortal;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.ParticleUtils;
import com.github.mim1q.minecells.util.animation.AnimationProperty;
import com.github.mim1q.minecells.world.state.OverworldPortals;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class KingdomPortalCoreBlockEntity extends BlockEntity {

  private static final ParticleEffect PARTICLE = new DustParticleEffect(new Vec3f(1.0F, 0.5F, 0.0F), 1.0F);
  private Vec3d offset = null;
  private Direction direction = null;
  private Vec3d widthVector = null;
  private Box box = null;

  private Integer portalId = null;

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
    if (direction == null) {
      direction = state.get(KingdomPortalCoreBlock.DIRECTION);
    }
    if (offset == null) {
      offset = calculateOffset();
    }
    if (widthVector == null) {
      widthVector = calculateWidthVector();
    }
    if (box == null) {
      box = calculateBox();
    }
  }

  public Vec3d calculateWidthVector() {
    return Vec3d.of(getDirection().rotateYCounterclockwise().getVector());
  }

  public Box calculateBox() {
    Vec3d pos = Vec3d.of(this.getPos()).add(offset);
    Vec3d size = widthVector;
    return Box.of(pos, size.x * 1.8D, 2.5D, size.z * 1.8D).expand(0.25D);
  }

  public static void tick(World world, BlockPos pos, BlockState state, KingdomPortalCoreBlockEntity blockEntity) {
    blockEntity.tick(world, pos, state);
  }

  public void tick(World world, BlockPos pos, BlockState state) {
    this.update(state);
    if (world.isClient()) {
      tickClient();
    } else {
      tickServer();
    }
  }

  protected void tickClient() {
    this.litProgress.update(Objects.requireNonNull(this.world).getTime());
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
        Vec3d.of(this.getPos()).add(this.getOffset()),
        1.5D
      );
    }
  }

  protected void tickServer() {
    if (this.getCachedState().get(KingdomPortalCoreBlock.LIT)) {
      teleportPlayer();
    } else {
      activatePortal((ServerWorld) Objects.requireNonNull(this.world));
    }
  }

  private void activatePortal(ServerWorld world) {
    Vec3d offset = Vec3d.of(this.direction.getVector());
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
        break;
      }
    }
    if (!canActivate) {
      return;
    }
    if (this.portalId == null) {
      var state = world.getPersistentStateManager().get(
        OverworldPortals::new,
        "minecells:overworld_portals"
      );
      if (state == null) {
        world.getPersistentStateManager().set("minecells:overworld_portals", new OverworldPortals());
        return;
      }
      world.setBlockState(pos, this.getCachedState().with(KingdomPortalCoreBlock.LIT, true));
      this.portalId = state.addPortal(this.pos);
    }
  }

  private void teleportPlayer() {
    PlayerEntity player = Objects.requireNonNull(this.getWorld()).getClosestPlayer(
      TargetPredicate.createNonAttackable(),
      this.getPos().getX(),
      this.getPos().getY(),
      this.getPos().getZ()
    );
    if (player == null) {
      return;
    }
    if (this.box.contains(player.getPos())) {
      if (((PlayerEntityAccessor) player).canUseKingdomPortal()) {

        if (MineCellsDimensions.isDimension(this.world, MineCellsDimensions.OVERWORLD)) {
          MineCellsPortal.teleportPlayerFromOverworld(
            (ServerPlayerEntity) player,
            (ServerWorld) this.world,
            this
          );
        } else {
          MineCellsPortal.teleportPlayerToOverworld(
            (ServerPlayerEntity) player,
            (ServerWorld) this.world,
            this
          );
        }

      } else {
        ((PlayerEntityAccessor) player).setKingdomPortalCooldown(50);
      }
    }
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    if (nbt.contains("portalId")) {
      this.portalId = nbt.getInt("portalId");
    }
    if (nbt.contains("direction")) {
      this.direction = Direction.byId(nbt.getInt("direction"));
    }
    this.update(this.getCachedState());
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    if (this.portalId != null) {
      nbt.putInt("portalId", this.portalId);
    }
    if (this.direction != null) {
      nbt.putInt("direction", this.direction.getId());
    }
  }

  private void spawnParticleSphere(ClientWorld world, Vec3d pos, double radius) {
    ParticleUtils.addAura(world, pos, PARTICLE, 10, radius, 1.0F);
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

  public Integer getPortalId() {
    return portalId;
  }
}
