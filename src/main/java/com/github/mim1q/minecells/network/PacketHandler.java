package com.github.mim1q.minecells.network;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.util.ParticleHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PacketHandler {

    public static final Identifier CRIT = new Identifier(MineCells.MOD_ID, "crit_packet");

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(CRIT, (client, handler, buf, responseSender) -> {
            Vec3d pos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            client.execute(() -> {
                if (client.player != null) {
                    ParticleHelper.addAura(client.world, pos, ParticleTypes.CRIT, 8, 0.0D, 1.0D);
                }
            });
        });
    }
}
