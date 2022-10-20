package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.registry.MineCellsPointOfInterestTypes;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.jetbrains.annotations.Nullable;

// Everything here works for now, somehow
// TODO: Clean this mess up

public class KingdomDimensionUtils {
//  public static final RegistryKey<World> KINGDOM = RegistryKey.of(Registry.WORLD_KEY, MineCells.createId("kingdom"));
//  public static final RegistryKey<World> OVERWORLD = RegistryKey.of(Registry.WORLD_KEY, new Identifier("minecraft", "overworld"));
//  public static final Identifier PORTAL_STRUCTURE = MineCells.createId("lit_portal");
//
//  public static void teleportPlayer(ServerPlayerEntity player, ServerWorld currentWorld, KingdomPortalCoreBlockEntity portal) {
//    boolean canUsePortal = ((PlayerEntityAccessor) player).canUseKingdomPortal();
//    ((PlayerEntityAccessor) player).setKingdomPortalCooldown(10);
//
//    if (!canUsePortal) {
//      return;
//    }
//    ServerWorld newWorld = getOppositeWorld(currentWorld);
//    if (newWorld == null) {
//      return;
//    }
//    BlockPos portalPos = portal.getPos();
//    BlockPos boundPos = portal.getBoundPos();
//    BlockPos newPortalPos = findOrPlacePortal(newWorld, boundPos == null ? portalPos : boundPos);
//    if (newPortalPos != null) {
//      FabricDimensions.teleport(player, newWorld, createTeleportTarget(newPortalPos.up()));
//    }
//  }
//
//  public static BlockPos findOrPlacePortal(ServerWorld world, BlockPos pos) {
//    BlockPos newPos = findExistingPortal(world, pos, 32);
//    BlockPos biomePos = null;
//    if (newPos == null) {
//      biomePos = findSuitableBiomePosition(world, pos);
//      if (biomePos == null) {
//        return null;
//      }
//      newPos = findExistingPortal(world, biomePos, 32);
//    }
//    if (newPos == null) {
//      placePortal(world, biomePos.withY(getTopY(world, biomePos)));
//      newPos = findExistingPortal(world, biomePos, 64);
//    }
//    if (newPos == null) {
//      return null;
//    }
//    BlockEntity entity = world.getBlockEntity(newPos.up(2));
//    if (entity instanceof KingdomPortalCoreBlockEntity portalEntity) {
//      portalEntity.setBoundPos(pos);
//    }
//    return newPos.up(1);
//  }
//
//  @Nullable
//  public static BlockPos findExistingPortal(ServerWorld world, BlockPos position, int range) {
//    if (world == null) return null;
//    var pois = world.getPointOfInterestStorage().getInCircle(
//      (poiType) -> poiType.value() == MineCellsPointOfInterestTypes.KINGDOM_PORTAL,
//      position,
//      range,
//      PointOfInterestStorage.OccupationStatus.ANY
//    );
//
//    var poi = pois.findFirst();
//    if (poi.isPresent()) {
//      BlockPos poiPos = poi.get().getPos();
//      poiPos = poiPos.withY(getTopY(world, poiPos));
//      return poiPos;
//    }
//    return null;
//  }
//
//  @Nullable
//  public static BlockPos findSuitableBiomePosition(ServerWorld world, BlockPos position) {
//    if (isKingdom(world)) {
//      var pos =  world.locateBiome(
//        biome -> biome.getKey().get().getValue().equals(MineCells.createId("promenade")),
//        position,
//        1000,
//        32,
//        32
//      );
//      return pos == null ? null : pos.getFirst();
//    }
//    return position;
//  }
//
//  public static int getTopY(ServerWorld world, BlockPos startingPos) {
//    int i = world.getTopY();
//    while (i > 0) {
//      BlockPos pos = startingPos.withY(i);
//      BlockState state = world.getBlockState(pos);
//      if (state.isFullCube(world, pos) || state.getMaterial().isLiquid()) {
//        return i;
//      }
//      i--;
//    }
//    return startingPos.getY();
//  }
//
//  public static void placePortal(ServerWorld world, BlockPos position) {
//    var template = world.getStructureTemplateManager().getTemplate(PORTAL_STRUCTURE);
//    BlockPos centeredPosition = position.add(-4, 0, -2);
//    StructurePlacementData data = new StructurePlacementData();
//    template.ifPresent(structureTemplate ->
//      structureTemplate.place(world, centeredPosition, centeredPosition, data, world.getRandom(), 2)
//    );
//  }
//
//  public static TeleportTarget createTeleportTarget(BlockPos pos) {
//    return new TeleportTarget(
//      Vec3d.ofCenter(pos),
//      Vec3d.ZERO,
//      0.0F,
//      0.0F
//    );
//  }
//
//  public static boolean isKingdom(World world) {
//    return world.getRegistryKey() == KINGDOM;
//  }
//
//  public static ServerWorld getOppositeWorld(ServerWorld world) {
//    Identifier key = world.getRegistryKey().getValue();
//    if (key.equals(KINGDOM.getValue())) {
//      return getOverworld(world);
//    } else if (key.equals(OVERWORLD.getValue())) {
//      return getKingdom(world);
//    }
//    return null;
//  }
//
//  public static ServerWorld getKingdom(ServerWorld world) {
//    return world.getServer().getWorld(KINGDOM);
//  }
//  public static ServerWorld getOverworld(ServerWorld world) {
//    return world.getServer().getWorld(World.OVERWORLD);
//  }
}
