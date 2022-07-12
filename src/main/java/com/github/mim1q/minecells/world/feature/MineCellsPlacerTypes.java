package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.mixin.TrunkPlacerTypeInvoker;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class MineCellsPlacerTypes {
  public static final TrunkPlacerType<PromenadeTreeTrunkPlacer> PROMENADE_TRUNK_PLACER = TrunkPlacerTypeInvoker.register(
    MineCells.createId("promenade_trunk_plancer").toString(),
    PromenadeTreeTrunkPlacer.CODEC
  );

  public static void register() {
  }
}
