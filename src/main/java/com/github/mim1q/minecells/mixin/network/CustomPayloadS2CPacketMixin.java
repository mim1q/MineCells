package com.github.mim1q.minecells.mixin.network;

import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = CustomPayloadS2CPacket.class, priority = 2275)
public abstract class CustomPayloadS2CPacketMixin implements Packet<ClientPlayPacketListener> {
  @ModifyConstant(
    method = "<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/network/PacketByteBuf;)V",
    constant = @Constant(intValue = 1048576)
  )
  private int minecells$initModifyMaxSize(int constant) {
    return Math.max(constant, 1073741824);
  }

  @ModifyConstant(
    method = "<init>(Lnet/minecraft/network/PacketByteBuf;)V",
    constant = @Constant(intValue = 1048576)
  )
  private int minecells$init2ModifyMaxSize(int constant) {
    return Math.max(constant, 1073741824);
  }
}
