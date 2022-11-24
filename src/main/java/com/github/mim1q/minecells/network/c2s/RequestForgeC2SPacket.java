package com.github.mim1q.minecells.network.c2s;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class RequestForgeC2SPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("request_forge");

  public RequestForgeC2SPacket(BlockPos pos) {
    super(Unpooled.buffer());
    this.writeBlockPos(pos);
  }

  public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
    BlockPos pos = buf.readBlockPos();
    server.execute(() -> {
      if (player == null || player.world == null) {
        return;
      }
      ScreenHandler screenHandler = player.currentScreenHandler;
      if (screenHandler instanceof CellForgeScreenHandler forgeScreenHandler) {
        if (forgeScreenHandler.canForge()) {
          forgeScreenHandler.forge(pos);
        }
      }
      player.closeHandledScreen();
    });
  }
}

