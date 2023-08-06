package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.damage.MineCellsDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimedAuraGoal<E extends LivingEntity> extends TimedActionGoal<E> {
  private final double radius;
  private final float damage;

  protected TimedAuraGoal(E entity, TimedAuraSettings settings, Predicate<E> predicate) {
    super(entity, settings, predicate);
    this.radius = settings.radius;
    this.damage = settings.damage;
  }

  public TimedAuraGoal(E entity, Consumer<TimedAuraSettings> settingsConsumer, Predicate<E> predicate) {
    this(entity, TimedActionSettings.edit(new TimedAuraSettings(), settingsConsumer), predicate);
  }

  @Override
  protected void release() {
    Box box = Box.of(this.entity.getPos().add(0.0D, this.entity.getHeight() * 0.5D, 0.0D), this.radius * 2.0F, this.radius * 2.0F, this.radius * 2.0F);
    for (PlayerEntity player : this.entity.getWorld().getPlayers(TargetPredicate.DEFAULT, this.entity, box)) {
      if (player.distanceTo(this.entity) <= this.radius) {
        player.damage(MineCellsDamageSource.AURA.get(player.getWorld(), this.entity), this.damage);
      }
    }
  }

  public static class TimedAuraSettings extends TimedActionSettings {
    public double radius = 10.0D;
    public float damage = 10.0F;
  }
}
