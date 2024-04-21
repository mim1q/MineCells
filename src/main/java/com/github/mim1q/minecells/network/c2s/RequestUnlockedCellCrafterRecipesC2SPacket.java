package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.MineCells;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class RequestUnlockedCellCrafterRecipesC2SPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("request_unlocked_cell_crafter_recipes");

  public RequestUnlockedCellCrafterRecipesC2SPacket(PlayerEntity player) {
    super(Unpooled.buffer());
    writeInt(player.getId());
  }
}
