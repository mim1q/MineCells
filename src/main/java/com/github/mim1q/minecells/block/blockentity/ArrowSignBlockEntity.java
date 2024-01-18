package com.github.mim1q.minecells.block.blockentity;

import com.github.mim1q.minecells.registry.MineCellsBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class ArrowSignBlockEntity extends MineCellsBlockEntity {
  private ItemStack itemStack = ItemStack.EMPTY;
  private int verticalRotation = 0;
  private BlockState chainState = Blocks.AIR.getDefaultState();

  public ArrowSignBlockEntity(BlockPos pos, BlockState state) {
    super(MineCellsBlockEntities.ARROW_SIGN, pos, state);
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public void setItemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
    markDirty();
    sync();
  }

  public int getVerticalRotation() {
    return verticalRotation;
  }

  public void cycleVerticalRotation(int amount) {
    this.verticalRotation += amount;
    if (this.verticalRotation > 15) {
      this.verticalRotation = 0;
    }
    if (this.verticalRotation < 0) {
      this.verticalRotation = 15;
    }
    markDirty();
    sync();
  }

  public BlockState getChainState() {
    return chainState;
  }

  public void setChainState(BlockState chainState) {
    this.chainState = chainState;
    markDirty();
    sync();
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    itemStack = ItemStack.fromNbt(nbt.getCompound("itemStack"));
    verticalRotation = nbt.getInt("verticalRotation");
    chainState = BlockState.CODEC.parse(NbtOps.INSTANCE, nbt.get("chainState")).result().orElse(Blocks.AIR.getDefaultState());
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    nbt.put("itemStack", itemStack.writeNbt(new NbtCompound()));
    nbt.putInt("verticalRotation", verticalRotation);
    nbt.put("chainState", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, chainState).getOrThrow(false, System.err::println));
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
}
