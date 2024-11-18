package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.MineCells;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class CellCrafterCraftRequestC2SPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("cell_crafter_craft_request");

  public CellCrafterCraftRequestC2SPacket(Identifier recipeId, BlockPos pos) {
    super(Unpooled.buffer());
    this.writeBlockPos(pos);
    this.writeIdentifier(recipeId);
  }

  public void send() {
    ClientPlayNetworking.send(CellCrafterCraftRequestC2SPacket.ID, this);
  }
}
