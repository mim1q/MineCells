package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.structure.grid.SpecialPointIds;
import dev.mim1q.gimm1q.interpolation.AnimatedProperty;
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
import java.util.UUID;

import static com.github.mim1q.minecells.block.portal.DoorwayPortalBlock.FACING;

public class DoorwayPortalBlockEntity extends BlockEntity {
  public final AnimatedProperty closedBarsAnimation = new AnimatedProperty(1f);

  private Identifier specialPointTarget = SpecialPointIds.ENTRANCE;
  private @Nullable BlockPos posOverride = null;
  private @Nullable UUID ownerId = null;
  private boolean onlyOwnerCanEnter = false;

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
    return !onlyOwnerCanEnter || (ownerId == null || ownerId.equals(player.getUuid()));
  }

  public void teleportPlayer(ServerPlayerEntity player, ServerWorld world, MineCellsDimension targetDimension) {
    if (onlyOwnerCanEnter && ownerId != null && !player.getUuid().equals(ownerId)) return;

    targetDimension.teleportPlayer(player, world, getPos(), this.specialPointTarget);
  }

  public float getRotation() {
    return getCachedState().get(FACING).asRotation();
  }

  public void update(PlayerEntity owner, boolean onlyOwnerCanEnter) {
    this.ownerId = owner.getUuid();
    this.onlyOwnerCanEnter = true;
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

    if (nbt.contains("special_point_target"))
      specialPointTarget = Identifier.tryParse(nbt.getString("special_point_target"));
    else if (nbt.contains("upstream"))
      specialPointTarget = nbt.getBoolean("upstream") ? SpecialPointIds.EXIT : SpecialPointIds.ENTRANCE;
    if (nbt.contains("pos_override")) posOverride = BlockPos.fromLong(nbt.getLong("pos_override"));
    if (nbt.contains("owner_id")) ownerId = UUID.fromString(nbt.getString("owner_id"));
    if (nbt.contains("only_owner_can_enter")) onlyOwnerCanEnter = nbt.getBoolean("only_owner_can_enter");
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);

    nbt.putString("special_point_target", specialPointTarget.toString());
    if (posOverride != null) nbt.putLong("pos_override", posOverride.asLong());
    if (ownerId != null) nbt.putString("owner_id", ownerId.toString());
    nbt.putBoolean("only_owner_can_enter", onlyOwnerCanEnter);
  }
}
