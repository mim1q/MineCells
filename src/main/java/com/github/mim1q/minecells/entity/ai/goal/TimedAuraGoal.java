package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class TimedAuraGoal<E extends LivingEntity> extends TimedActionGoal<E> {

  private final double radius;
  private final float damage;

  public TimedAuraGoal(Builder<E> builder) {
    super(builder);
    this.radius = builder.radius;
    this.damage = builder.damage;
  }

  @Override
  protected void release() {
    Box box = Box.of(this.entity.getPos().add(0.0D, this.entity.getHeight() * 0.5D, 0.0D), this.radius * 2.0F, this.radius * 2.0F, this.radius * 2.0F);
    for (PlayerEntity player : this.entity.world.getPlayers(TargetPredicate.DEFAULT, this.entity, box)) {
      if (player.distanceTo(this.entity) <= this.radius) {
        player.damage(MineCellsDamageSource.aura(this.entity), this.damage);
      }
    }
  }

  public static class Builder<E extends LivingEntity> extends TimedActionGoal.Builder<E, Builder<E>> {

    double radius;
    float damage;

    public Builder(E entity) {
      super(entity);
    }

    public Builder<E> radius(double radius) {
      this.radius = radius;
      return this;
    }

    public Builder<E> damage(float damage) {
      this.damage = damage;
      return this;
    }

    public TimedAuraGoal<E> build() {
      return new TimedAuraGoal<>(this);
    }
  }
}
