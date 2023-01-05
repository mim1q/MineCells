package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.entity.nonliving.obelisk.ObeliskEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ObeliskActivationS2CPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("obelisk_activation");

  public ObeliskActivationS2CPacket(int entityId) {
    super(Unpooled.buffer());
    writeInt(entityId);
  }

  public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    int entityId = buf.readInt();
    client.execute(() -> {
      Entity entity = handler.getWorld().getEntityById(entityId);
      if (entity instanceof ObeliskEntity obelisk) {
        obelisk.resetActivatedTicks();
      }
    });
  }
}
