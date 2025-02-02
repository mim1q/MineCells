package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.mim1q.minecells.block.portal.DoorwayPortalBlock.FACING;

public class DoorwayPortalBlockEntity extends BlockEntity {
  private Identifier specialPointTarget = SpecialPointIds.ENTRANCE;

  public DoorwayPortalBlockEntity(BlockPos pos, BlockState state) {
    this(MineCellsBlockEntities.DOORWAY, pos, state);
  }

  protected DoorwayPortalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public Identifier getTexture() {
    return ((DoorwayPortalBlock) getCachedState().getBlock()).type.texture;
  }

  public Identifier getBackgroundTexture() {
    return ((DoorwayPortalBlock) getCachedState().getBlock()).type.backgroundTexture;
  }

  public List<MutableText> getLabel() {
    return List.of(Text.of("label").copy());
  }

  @Override
  public void setStackNbt(ItemStack stack) {
    super.setStackNbt(stack);
  }

  public boolean canPlayerEnter(PlayerEntity player) {
    return true;
  }

  public void teleportPlayer(ServerPlayerEntity player, ServerWorld world, MineCellsDimension targetDimension) {
    targetDimension.teleportPlayer(
      player,
      world,
      null,
      this.specialPointTarget
    );
  }

  public float getRotation() {
    return getCachedState().get(FACING).asRotation();
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
    super.readNbt(nbt);

    if (nbt.contains("special_point_target")) {
      specialPointTarget = Identifier.tryParse(nbt.getString("special_point_target"));
    } else if (nbt.contains("upstream")) {
      specialPointTarget = nbt.getBoolean("upstream") ? SpecialPointIds.EXIT : SpecialPointIds.ENTRANCE;
    }
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);

    nbt.putString("special_point_target", specialPointTarget.toString());
  }
}
