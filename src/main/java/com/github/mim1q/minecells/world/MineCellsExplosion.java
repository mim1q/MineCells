package com.github.mim1q.minecells.world;

import com.github.mim1q.minecells.network.ServerPacketHandler;
import com.github.mim1q.minecells.network.s2c.ExplosionS2CPacket;
import com.github.mim1q.minecells.registry.MineCellsSounds;
import dev.mim1q.gimm1q.screenshake.ScreenShakeUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.function.Predicate;

public class MineCellsExplosion {
  public static void explode(
    ServerWorld world,
    Entity grenade,
    LivingEntity causingEntity,
    Vec3d position,
    float power,
    float radius
  ) {
    explode(
      world, grenade, causingEntity, position, power, radius,
      entity -> entity instanceof PlayerEntity || entity instanceof TameableEntity
    );
  }

  public static void explode(
    ServerWorld world,
    Entity grenade,
    LivingEntity causingEntity,
    Vec3d position,
    float power,
    float radius,
    Predicate<Entity> damagePredicate
  ) {
    world.playSound(null, BlockPos.ofFloored(position), MineCellsSounds.EXPLOSION, SoundCategory.HOSTILE, 1.0f, 1.0f);
    damageEntities(world, grenade, causingEntity, position, power, radius, damagePredicate);

    ScreenShakeUtils.shakeAround(
      world,
      position,
      1.0f,
      20,
      radius,
      radius * 3f,
      "minecells:explosion"
    );

    ServerPacketHandler.CHANNEL.serverHandle(world, BlockPos.ofFloored(position)).send(new ExplosionS2CPacket(position, radius));
  }

  protected static void damageEntities(
    ServerWorld world,
    Entity grenade,
    LivingEntity attacker,
    Vec3d pos,
    float power,
    float radius,
    Predicate<Entity> damagePredicate
  ) {
    Box box = new Box(
      pos.x - radius,
      pos.y - radius,
      pos.z - radius,
      pos.x + radius,
      pos.y + radius,
      pos.z + radius
    );
    List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box, damagePredicate);
    for (LivingEntity entity : entities) {
      float distance = MathHelper.sqrt((float) entity.squaredDistanceTo(pos));
      if (distance <= radius) {
        float damage = power * getDamagePercentage(distance, radius);
        entity.damage(world.getDamageSources().explosion(grenade, attacker), damage);
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
