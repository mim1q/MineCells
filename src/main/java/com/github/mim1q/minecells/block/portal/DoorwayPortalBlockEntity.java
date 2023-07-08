package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;

import static com.github.mim1q.minecells.block.portal.DoorwayPortalBlock.FACING;

public class DoorwayPortalBlockEntity extends BlockEntity {
  private boolean upstream = false;

  public DoorwayPortalBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.DOORWAY, pos, state);
  }

  public Identifier getTexture() {
    return ((DoorwayPortalBlock)getCachedState().getBlock()).type.texture;
  }

  public MutableText getLabel() {
    return Text.translatable(((DoorwayPortalBlock)getCachedState().getBlock()).type.dimension.translationKey);
  }

  public boolean canPlayerEnter(PlayerEntity player) {
    if (isDownstream()) return true;
    if (player == null || world == null) return false;
    return ((PlayerEntityAccessor)player).getMineCellsData().get(this.pos).getPortalData(
      MineCellsDimension.of(world),
      ((DoorwayPortalBlock)getCachedState().getBlock()).type.dimension
    ).isPresent();
  }

  public void teleportPlayer(ServerPlayerEntity player, ServerWorld world, MineCellsDimension targetDimension) {
    if (isDownstream()) {
      if (targetDimension == MineCellsDimension.OVERWORLD) {
        var data = MineCellsData.getPlayerData(player, world).getPortalData(MineCellsDimension.PRISONERS_QUARTERS, MineCellsDimension.OVERWORLD);
        world.getServer().execute(() -> data.ifPresent(portalData -> FabricDimensions.teleport(
          player,
          world.getServer().getOverworld(),
          new TeleportTarget(Vec3d.ofCenter(portalData.toPos()), Vec3d.ZERO, 0F, 0F)
        )));
      } else {
        MineCellsData.getPlayerData(player, world).addPortalData(
          MineCellsDimension.of(world),
          targetDimension,
          pos.add(getCachedState().get(FACING).getVector()),
          new BlockPos(targetDimension.getTeleportPosition(pos, world))
        );
        targetDimension.teleportPlayer(player, world);
      }
    } else {
      var data = MineCellsData.getPlayerData(player, world).getPortalData(MineCellsDimension.of(world), targetDimension);
      world.getServer().execute(() -> data.ifPresent(portalData -> FabricDimensions.teleport(
        player,
        targetDimension.getWorld(world),
        new TeleportTarget(Vec3d.ofCenter(portalData.toPos()), Vec3d.ZERO, 0F, targetDimension.yaw)
      )));
    }
  }

  public float getRotation() {
    return getCachedState().get(FACING).asRotation();
  }

  public boolean isUpstream() {
    return upstream;
  }

  public boolean isDownstream() {
    return !isUpstream();
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

  @Override
  public void readNbt(NbtCompound nbt) {
    upstream = nbt.getBoolean("upstream");
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    nbt.putBoolean("upstream", upstream);
  }
}
