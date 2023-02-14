package com.github.mim1q.minecells.mixin.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CustomPayloadS2CPacket.class, priority = 2275)
public abstract class CustomPayloadS2CPacketMixin implements Packet<ClientPlayPacketListener> {
  @Redirect(
    method = "<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writerIndex()I", ordinal = 0)
  )
  private int minecells$initRedirectWriterIndex(PacketByteBuf instance) {
    return 0;
  }

  @Redirect(
    method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readableBytes()I", ordinal = 0)
  )
  private int minecells$init2RedirectReadableBytes(PacketByteBuf instance) {
    return 0;
  }

  @Redirect(
    method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;readBytes(I)Lio/netty/buffer/ByteBuf;")
  )
  private ByteBuf minecells$init2RedirectReadBytes(PacketByteBuf instance, int length) {
    return instance.readBytes(instance.readableBytes());
  }
}
