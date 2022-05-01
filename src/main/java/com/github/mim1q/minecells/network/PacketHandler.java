package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class PacketHandler {

    public static final Identifier CRIT = new Identifier(MineCells.MOD_ID, "crit_packet");

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(CRIT, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                System.out.println("aaaaaa");
                if (client.player != null) {
                    client.player.playSound(SoundRegistry.CRIT, SoundCategory.PLAYERS, 0.5F, 1.0F);
                }
            });
        });
    }
}
