package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.registry.MineCellsSounds;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.github.mim1q.minecells.util.MathUtils.vectorRotateY;

public class JumpBackGoal<E extends MobEntity> extends TimedActionGoal<E> {
  private final JumpBackSettings settings;

  public JumpBackGoal(E entity, JumpBackSettings settings, Predicate<E> predicate) {
    super(entity, settings, predicate);
    this.settings = settings;
    setControls(EnumSet.of(Control.MOVE, Control.LOOK));
  }

  public JumpBackGoal(E entity, Consumer<JumpBackSettings> settings, Predicate<E> predicate) {
    this(entity, TimedActionSettings.edit(new JumpBackSettings(), settings), predicate);
  }

  @Override public boolean canStart() {
    if (!super.canStart()) return false;
    if (entity.getTarget() == null) return false;
    if (entity.getTarget().squaredDistanceTo(entity) > settings.minDistance * settings.minDistance) return false;

    var posBehind = entity.getPos().add(entity.getRotationVec(1F).multiply(-1, 0, -1).normalize());
    return !entity.getWorld().getBlockState(BlockPos.ofFloored(posBehind)).isOpaque();
  }

  @Override protected void runAction() {
    if (this.entity.getTarget() == null) return;

    playSound(MineCellsSounds.LEAPING_ZOMBIE_RELEASE);
    entity.lookAtEntity(this.entity.getTarget(), 360.0F, 360.0F);

    var targetDirection = entity.getTarget().getPos().subtract(entity.getPos()).multiply(1, 0, 1).normalize();
    var sideDirection = vectorRotateY(targetDirection, MathUtils.radians(90.0F));
    var sideStrength = settings.sideStrength * 0.5 + entity.getRandom().nextFloat() * settings.sideStrength * 0.5;

    if (entity.getRandom().nextBoolean()) sideStrength *= -1;

    entity.setVelocity(
      targetDirection
        .multiply(-settings.backStrength)
        .add(sideDirection.multiply(sideStrength))
        .add(0.0, settings.upStrength, 0.0)
    );
  }

  public static class JumpBackSettings extends TimedActionSettings {
    public double minDistance = 5.0D;
    public double backStrength = 0.75D;
    public double upStrength = 0.33D;
    public double sideStrength = 0.0D;
  }
}
