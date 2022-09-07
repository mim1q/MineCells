package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.BlockEntityRegistry;
import com.github.mim1q.minecells.util.ParticleUtils;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import javax.swing.text.html.parser.Entity;
import java.util.List;

public class KingdomPortalCoreBlockEntity extends BlockEntity {

  private static final ParticleEffect PARTICLE = new DustParticleEffect(new Vec3f(1.0F, 0.5F, 0.0F), 1.0F);
  private Vec3d offset = Vec3d.ZERO;
  private Direction direction = Direction.NORTH;
  private Vec3d widthVector = new Vec3d(1.0D, 0.0D, 0.0D);
  private Box box = Box.of(Vec3d.of(this.pos), 1.0D, 1.0D, 1.0D);


  public KingdomPortalCoreBlockEntity(BlockPos pos, BlockState state) {
    super(BlockEntityRegistry.KINGDOM_PORTAL_CORE_BLOCK_ENTITY, pos, state);
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


  // TODO: clean this mess up
  // Move dimension transport to a separate util class

  public static void tick(World world, BlockPos pos, BlockState state, KingdomPortalCoreBlockEntity blockEntity) {
    blockEntity.update(state);
    if (world.isClient()) {
      Vec3d position = Vec3d.of(pos).add(blockEntity.getOffset());
      float time = (world.getTime() * 0.1F);
      time %= MathHelper.PI * 2.0F;
      double y = MathHelper.sin(time);
      double xz = MathHelper.cos(time);
      Vec3d vector = blockEntity.getWidthVector()
       .multiply(xz)
       .add(0.0D, y, 0.0D)
       .multiply(1.25D);
      ParticleUtils.addParticle((ClientWorld) world, PARTICLE, position.add(vector), Vec3d.ZERO);
    } else {
      List<PlayerEntity> list = world.getPlayers(TargetPredicate.DEFAULT, null, blockEntity.getBox());
      ServerWorld serverWorld = (ServerWorld) world;
      MinecraftServer server = serverWorld.getServer();
      list.forEach(player -> {
        ServerWorld kingdom = server.getWorld(RegistryKey.of(Registry.WORLD_KEY, MineCells.createId("kingdom")));
        assert kingdom != null;
        System.out.println(kingdom.getTopY(Heightmap.Type.MOTION_BLOCKING, 1, 1));
        FabricDimensionInternals.changeDimension(player, kingdom, new TeleportTarget(
          new Vec3d(0.5D, 75.0D, 0.5D),
          Vec3d.ZERO,
          0.0F,
          0.0F
          ));
      });
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
}
