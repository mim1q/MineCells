package com.github.mim1q.minecells.entity.ai.goal.conjunctivius;

import com.github.mim1q.minecells.entity.ai.goal.TargetRandomPlayerGoal;
import com.github.mim1q.minecells.entity.boss.ConjunctiviusEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.List;

public class ConjunctiviusTargetGoal extends TargetRandomPlayerGoal<ConjunctiviusEntity> {

  public ConjunctiviusTargetGoal(ConjunctiviusEntity entity) {
    super(entity);
  }

  @Override
  protected List<PlayerEntity> getTargetablePlayers() {
    Box box = Box.from(this.entity.getRoomBox());
    return this.entity.world.getPlayers(TargetPredicate.DEFAULT, this.entity, box);
  }
}
