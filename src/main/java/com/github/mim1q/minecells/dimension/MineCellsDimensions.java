package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Set;

public class MineCellsDimensions {
  public static final RegistryKey<World> OVERWORLD = RegistryKey.of(Registry.WORLD_KEY, new Identifier("minecraft", "overworld"));
  public static final RegistryKey<World> PRISON = RegistryKey.of(Registry.WORLD_KEY, MineCells.createId("prison"));
  public static final RegistryKey<World> INSUFFERABLE_CRYPT = RegistryKey.of(Registry.WORLD_KEY, MineCells.createId("insufferable_crypt"));
  public static final RegistryKey<World> PROMENADE = RegistryKey.of(Registry.WORLD_KEY, MineCells.createId("promenade"));

  public static World getWorld(World world, RegistryKey<World> key) {
    MinecraftServer server = world.getServer();
    if (server == null) {
      return null;
    }
    return server.getWorld(key);
  }

  public static boolean isDimension(World world, RegistryKey<World> key) {
    return world.getRegistryKey().equals(key);
  }

  public static boolean isMineCellsDimension(World world) {
    return Set.of(
      PRISON,
      INSUFFERABLE_CRYPT,
      PROMENADE
    ).contains(world.getRegistryKey());
  }

  public static Vec3d getTeleportPos(RegistryKey<World> dimension, BlockPos pos, ServerWorld world) {
    BlockPos multiple = new BlockPos(MathUtils.getClosestMultiplePosition(pos, 256));
    BlockPos multiple512 = new BlockPos(MathUtils.getClosestMultiplePosition(pos, 512));
    if (dimension.equals(PRISON)) {
      return new Vec3d(multiple.getX() + 8, 43, multiple.getZ() + 5.5);
    }
    if (dimension.equals(INSUFFERABLE_CRYPT)) {
      return new Vec3d(multiple.getX() + 6, 41, multiple.getZ() + 3.5);
    }
    if (dimension.equals(PROMENADE)) {
      int y = world.getChunk(multiple512).sampleHeightmap(Heightmap.Type.WORLD_SURFACE, multiple512.getX(), multiple512.getZ());
      BlockPos groundPos = multiple512.withY(y);
      if (groundPos.getY() == 0) return null;
      BlockPos tpPos = MineCellsPortal.placeUpstreamPortal(world, groundPos.down());
      return Vec3d.ofCenter(tpPos).add(3.5D, 0.0D, 3.5D);
    }
    return null;
  }

  public static String getTranslationKey(RegistryKey<World> dimension) {
    Identifier id = dimension.getValue();
    return "dimension." + id.getNamespace() + "." + id.getPath();
  }

  public static String getTranslationKey(String key) {
    Identifier id = new Identifier(key);
    return "dimension." + id.getNamespace() + "." + id.getPath();
  }
}
