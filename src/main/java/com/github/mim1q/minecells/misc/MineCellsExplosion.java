package com.github.mim1q.minecells.misc;

import com.github.mim1q.minecells.entity.MineCellsEntity;
import com.github.mim1q.minecells.network.PacketIdentifiers;
import com.github.mim1q.minecells.registry.SoundRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

import java.util.List;

public class MineCellsExplosion {
  public static void explode(ServerWorld world, LivingEntity causingEntity, Vec3d position, float power) {
    world.playSound(null, new BlockPos(position), SoundRegistry.EXPLOSION, SoundCategory.HOSTILE, 0.5F, 1.0F);
    damageEntities(world, causingEntity, position, power);

    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeDouble(position.x);
    buf.writeDouble(position.y);
    buf.writeDouble(position.z);
    buf.writeDouble(power);
    for (ServerPlayerEntity player : PlayerLookup.tracking(world, new BlockPos(position))) {
      ServerPlayNetworking.send(player, PacketIdentifiers.EXPLOSION, buf);
    }
  }

  protected static void damageEntities(ServerWorld world, LivingEntity causingEntity, Vec3d pos, float power) {
    double q = power * 2.0F;
    double k = MathHelper.floor(pos.x - q - 1.0D);
    double l = MathHelper.floor(pos.x + q + 1.0D);
    int r = MathHelper.floor(pos.y - q - 1.0D);
    int s = MathHelper.floor(pos.y + q + 1.0D);
    int t = MathHelper.floor(pos.z - q - 1.0D);
    int u = MathHelper.floor(pos.z + q + 1.0D);
    List<Entity> list = world.getOtherEntities(causingEntity, new Box(k, r, t, l, s, u));

    for (Entity entity : list) {
      if (!entity.isImmuneToExplosion() && !(entity instanceof MineCellsEntity)) {
        double w = Math.sqrt(entity.squaredDistanceTo(pos)) / q;
        if (w <= 1.0D) {
          double x = entity.getX() - pos.x;
          double y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - pos.y;
          double z = entity.getZ() - pos.z;
          double aa = Math.sqrt(x * x + y * y + z * z);
          if (aa != 0.0D) {
            double ab = Explosion.getExposure(pos, entity);
            double ac = (1.0D - w) * ab;
            entity.damage(DamageSource.explosion(causingEntity), (float) ((int) ((ac * ac + ac) / 2.0D * 7.0D * q + 1.0D)));
          }
        }
      }
    }
  }
}
