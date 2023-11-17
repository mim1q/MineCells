package com.github.mim1q.minecells.misc;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import com.github.mim1q.minecells.network.PacketIdentifiers;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class MineCellsExplosion {

  public static void explode(ServerWorld world, LivingEntity causingEntity, Vec3d position, float power, float radius) {
    world.playSound(null, BlockPos.ofFloored(position), MineCellsSounds.EXPLOSION, SoundCategory.HOSTILE, 1.0f, 1.0f);
    damageEntities(world, causingEntity, position, power, radius);

    PacketByteBuf buf = PacketByteBufs.create();
    buf.writeDouble(position.x);
    buf.writeDouble(position.y);
    buf.writeDouble(position.z);
    buf.writeDouble(radius);
    for (ServerPlayerEntity player : PlayerLookup.tracking(world, BlockPos.ofFloored(position))) {
      ServerPlayNetworking.send(player, PacketIdentifiers.EXPLOSION, buf);
    }
  }

  protected static void damageEntities(ServerWorld world, LivingEntity causingEntity, Vec3d pos, float power, float radius) {
    Box box = new Box(
      pos.x - radius,
      pos.y - radius,
      pos.z - radius,
      pos.x + radius,
      pos.y + radius,
      pos.z + radius
    );
    List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, entity -> entity instanceof PlayerEntity || entity instanceof TameableEntity);
    for (LivingEntity entity : entities) {
      float distance = MathHelper.sqrt((float) entity.squaredDistanceTo(pos));
      if (distance <= radius) {
        float damage = power * getDamagePercentage(distance, radius);
        entity.damage(MineCellsDamageSource.GRENADE.get(world, causingEntity), damage);
      }
    }
  }

  // Damage is constant for the first half of the radius, then linearly decreases to 0
  protected static float getDamagePercentage(float distance, float radius) {
    float minRadius = radius * 0.5f;
    if (distance <= minRadius) {
      return 1.0f;
    } else {
      return 1.0f - (distance - minRadius) / (radius - minRadius);
    }
  }
}
