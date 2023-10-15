package com.github.mim1q.minecells.entity.ai.goal.concierge;

import com.github.mim1q.minecells.entity.ai.goal.TimedActionGoal;
import com.github.mim1q.minecells.entity.boss.ConciergeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import java.util.EnumSet;
import java.util.function.Consumer;

public class ConciergePunchGoal extends TimedActionGoal<ConciergeEntity> {
  private final float damage;
  private final double knockback;

  protected ConciergePunchGoal(ConciergeEntity entity, ConciergePunchSettings settings) {
    super(entity, settings, null);
    this.damage = settings.damage;
    this.knockback = settings.knockback;
    this.setControls(EnumSet.of(Control.MOVE));
  }

  public ConciergePunchGoal(ConciergeEntity entity, Consumer<ConciergePunchSettings> settingsConsumer) {
    this(entity, TimedActionSettings.edit(new ConciergePunchSettings(), settingsConsumer));
  }

  @Override
  public boolean canStart() {
    return super.canStart()
        && entity.getTarget() != null
        && entity.getTarget().distanceTo(entity) <= 3.0
        && entity.canAttack();
  }

  @Override
  protected void runAction() {
    var punchPos = entity.getPos().add(entity.getRotationVector().multiply(1, 0, 1).normalize());
    var punchBox = Box.of(punchPos, 5.0, 3.0, 5.0);
    for (var target : entity.getWorld().getOtherEntities(entity, punchBox)) {
      if (target instanceof LivingEntity livingTarget) {
        target.damage(entity.getDamageSources().mobAttack(entity), damage);
        livingTarget.takeKnockback(knockback, entity.getX() - target.getX(), entity.getZ() - target.getZ());
      }
    }
  }

  public static class ConciergePunchSettings extends TimedActionSettings {
    public float damage = 10.0F;
    public double knockback = 1.0;
  }
}
