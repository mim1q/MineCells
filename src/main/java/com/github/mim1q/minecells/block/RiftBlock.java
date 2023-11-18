package com.github.mim1q.minecells.block;

import com.github.mim1q.minecells.block.blockentity.RiftBlockEntity;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RiftBlock extends BlockWithEntity {
  public RiftBlock(Settings settings) {
    super(settings);
  }

  @Nullable
  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new RiftBlockEntity(pos, state);
  }

  @Override
  @SuppressWarnings("deprecation")
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return context instanceof EntityShapeContext entityCtx
      && entityCtx.getEntity() instanceof PlayerEntity player
      && player.isCreative() ? VoxelShapes.fullCube() : VoxelShapes.empty();
  }

  @Override
  @SuppressWarnings("deprecation")
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    if (entity instanceof ServerPlayerEntity player && world instanceof ServerWorld serverWorld) {
      var teleportPos = getPlayerTeleportPosition(player, serverWorld);
      serverWorld.getServer().execute(() -> {
        player.teleport(world.getServer().getOverworld(), teleportPos.getX(), teleportPos.getY(), teleportPos.getZ(), 0F, 0F);
      });
    }
  }

  private BlockPos getPlayerTeleportPosition(ServerPlayerEntity player, ServerWorld world) {
    var data = MineCellsData.getPlayerData(player, world, null);

    var currentToOverworld = data.getPortalData(MineCellsDimension.of(world), MineCellsDimension.OVERWORLD);
    if (currentToOverworld.isPresent()) return currentToOverworld.get().toPos();

    var prisonToOverworld = data.getPortalData(MineCellsDimension.PRISONERS_QUARTERS, MineCellsDimension.OVERWORLD);
    if (prisonToOverworld.isPresent()) return prisonToOverworld.get().toPos();

    if (player.getSpawnPointDimension() == MineCellsDimension.OVERWORLD.key) {
      var spawnPoint = player.getSpawnPointPosition();
      if (spawnPoint != null) return spawnPoint;
    }
    return world.getServer().getOverworld().getSpawnPos();
  }

  @Nullable
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
    return checkType(type, MineCellsBlockEntities.RIFT, RiftBlockEntity::tick);
  }
}
