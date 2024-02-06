package com.github.mim1q.minecells.block.portal;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import com.github.mim1q.minecells.util.MathUtils;
import com.github.mim1q.minecells.world.state.MineCellsData;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
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

import java.util.ArrayList;
import java.util.List;

import static com.github.mim1q.minecells.block.portal.DoorwayPortalBlock.FACING;

public class DoorwayPortalBlockEntity extends BlockEntity {
  private boolean upstream = false;
  private boolean clientVisited = false;
  private BlockPos posOverride = null;
  private List<MutableText> label = null;

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

  public boolean hasClientVisited() {
    return clientVisited;
  }

  public void updateClientVisited() {
    if (world != null && world.isClient) {
      var player = MinecraftClient.getInstance().player;
      if (player == null) return;
      var lastClientVisited = clientVisited;
      clientVisited = ((PlayerEntityAccessor) player).getMineCellsData()
        .get(posOverride == null ? player.getBlockPos() : posOverride)
        .hasVisitedDimension(((DoorwayPortalBlock) getCachedState().getBlock()).type.dimension);
      if (lastClientVisited != clientVisited) {
        label = null;
      }
    }
  }

  public List<MutableText> getLabel() {
    if (label != null) {
      return label;
    }
    var result = new ArrayList<MutableText>();
    var text = Text.translatable(((DoorwayPortalBlock) getCachedState().getBlock()).type.dimension.translationKey);
    if (!hasClientVisited()) {
      text.append(Text.literal("*"));
    }
    var parts = text.getString().split(" ");
    if (parts.length >= 4) {
      result.add(Text.literal(parts[0] + " " + parts[1]));
      result.add(Text.literal(parts[2] + " " + parts[3]));
    } else {
      result.add(text);
    }
    var portalPos = posOverride == null ? MathUtils.getClosestMultiplePosition(this.getPos(), 1024) : posOverride;
    result.add(Text.literal("[x: " + portalPos.getX() + ", z: " + portalPos.getZ() + "]"));
    label = result;
    return result;
  }

  @Override
  public void setStackNbt(ItemStack stack) {
    super.setStackNbt(stack);
    stack.getOrCreateSubNbt("BlockEntityTag").putLong(
      "posOverride",
      posOverride == null ? new BlockPos(MathUtils.getClosestMultiplePosition(pos, 1024)).asLong() : posOverride.asLong()
    );
  }

  public boolean canPlayerEnter(PlayerEntity player) {
    if (player == null || world == null) return false;
    var pos = posOverride == null ? player.getBlockPos() : posOverride;
    var targetDimension = ((DoorwayPortalBlock) getCachedState().getBlock()).type.dimension;
    var mineCellsData = ((PlayerEntityAccessor) player).getMineCellsData().get(pos);
    if (isDownstream()) {
      if (MineCellsDimension.of(world) == MineCellsDimension.OVERWORLD) {
        if (targetDimension == MineCellsDimension.PRISONERS_QUARTERS) return true;
        return mineCellsData.hasVisitedDimension(targetDimension);
      }
      return true;
    }
    return mineCellsData.getPortalData(
      MineCellsDimension.of(world),
      targetDimension
    ).isPresent();
  }

  public void teleportPlayer(ServerPlayerEntity player, ServerWorld world, MineCellsDimension targetDimension) {
    if (isDownstream()) {
      if (targetDimension == MineCellsDimension.OVERWORLD) {
        var data = MineCellsData.getPlayerData(player, world, posOverride).getPortalData(MineCellsDimension.PRISONERS_QUARTERS, MineCellsDimension.OVERWORLD);
        world.getServer().execute(() -> data.ifPresent(portalData -> {
          var pos = portalData.toPos();
          player.teleport(world.getServer().getOverworld(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYaw(), player.getPitch());
        }));
      } else {
        MineCellsData.getPlayerData(player, world, posOverride).addPortalData(
          MineCellsDimension.of(world),
          targetDimension,
          pos.add(getCachedState().get(FACING).getVector()),
          BlockPos.ofFloored(targetDimension.getTeleportPosition(pos, world))
        );
        targetDimension.teleportPlayer(player, world, posOverride);
      }
    } else {
      var data = MineCellsData.getPlayerData(player, world, posOverride).getPortalData(MineCellsDimension.of(world), targetDimension);
      world.getServer().execute(() -> data.ifPresent(portalData -> {
        var pos = portalData.toPos();
        player.teleport(targetDimension.getWorld(world), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYaw(), player.getPitch());
      }));
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

  public BlockPos getPosOverride() {
    return posOverride;
  }

  public void setPosOverride(BlockPos posOverride) {
    this.posOverride = posOverride;
    markDirty();
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    upstream = nbt.getBoolean("upstream");
    updateClientVisited();
    if (nbt.contains("posOverride")) {
      posOverride = BlockPos.fromLong(nbt.getLong("posOverride"));
    }
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    nbt.putBoolean("upstream", upstream);
    if (posOverride != null) {
      nbt.putLong("posOverride", posOverride.asLong());
    }
  }
}
