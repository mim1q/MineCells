package com.github.mim1q.minecells.entity.ai.goal;

import com.github.mim1q.minecells.entity.nonliving.ShockwavePlacer;
import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.github.mim1q.minecells.util.MathUtils.radians;

public class ShockwaveGoal<E extends HostileEntity> extends TimedActionGoal<E> {
  private final BlockState shockwaveBlock;
  private final float shockwaveDamage;
  private final int shockwaveRadius;
  private final ShockwaveType shockwaveType;
  private final float shockwaveInterval;

  private ShockwaveGoal(E entity, ShockwaveGoalSettings settings, Predicate<E> predicate) {
    super(entity, settings, predicate);
    this.shockwaveBlock = settings.shockwaveBlock.getDefaultState();
    this.shockwaveDamage = settings.shockwaveDamage;
    this.shockwaveRadius = settings.shockwaveRadius;
    this.shockwaveType = settings.shockwaveType;
    this.shockwaveInterval = settings.shockwaveInterval;
  }

  public ShockwaveGoal(E entity, Consumer<ShockwaveGoalSettings> settingsConsumer, Predicate<E> predicate) {
    this(entity, ShockwaveGoalSettings.edit(new ShockwaveGoalSettings(), settingsConsumer), predicate);
    setControls(EnumSet.of(Control.LOOK, Control.MOVE));
  }

  @Override
  protected void runAction() {
    if (entity.getTarget() == null) return;
    var offset = MathUtils.vectorRotateY(new Vec3d(-0.75D, 0.2D, -0.9D), radians(entity.bodyYaw));
    ShockwavePlacer placer;
    if (shockwaveType == ShockwaveType.LINE) {
      placer = ShockwavePlacer.createLine(
        entity.getWorld(),
        entity.getPos().add(offset),
        entity.getTarget().getPos().withAxis(Direction.Axis.Y, entity.getPos().y),
        shockwaveInterval,
        shockwaveBlock,
        entity.getUuid(),
        shockwaveDamage
      );
    } else {
      placer = ShockwavePlacer.createCircle(
        entity.getWorld(),
        entity.getPos().add(offset),
        shockwaveRadius,
        shockwaveInterval,
        shockwaveBlock,
        entity.getUuid(),
        shockwaveDamage
      );
    }
    entity.getWorld().spawnEntity(placer);
  }

  @Override
  public boolean canStart() {
    var target = entity.getTarget();
    return super.canStart() && target != null;// && target.squaredDistanceTo(entity) < 4.0D;
  }

  @Override
  public void start() {
    super.start();
    var target = this.entity.getTarget();
    if (target == null) return;
    this.entity.lookAtEntity(target, 180.0F, 180.0F);
    this.entity.getNavigation().stop();
    this.entity.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1.0D);
  }

  public static class ShockwaveGoalSettings extends TimedActionGoal.TimedActionSettings {
    public Block shockwaveBlock = MineCellsBlocks.SHOCKWAVE_FLAME;
    public int shockwaveRadius = 12;
    public ShockwaveType shockwaveType = ShockwaveType.LINE;
    public float shockwaveDamage = 6F;
    public float shockwaveInterval = 1F;
  }

  public enum ShockwaveType {
    LINE,
    CIRCLE
  }
}
