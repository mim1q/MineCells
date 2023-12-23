package com.github.mim1q.minecells.world.feature;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.mixin.world.FoliagePlacerTypeInvoker;
import com.github.mim1q.minecells.mixin.world.TrunkPlacerTypeInvoker;
import com.github.mim1q.minecells.world.feature.tree.PromenadeFoliagePlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeShrubTrunkPlacer;
import com.github.mim1q.minecells.world.feature.tree.PromenadeTreeTrunkPlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class MineCellsPlacerTypes {
  public static final TrunkPlacerType<PromenadeTreeTrunkPlacer> PROMENADE_TRUNK = TrunkPlacerTypeInvoker.register(
    MineCells.createId("promenade_trunk").toString(),
    PromenadeTreeTrunkPlacer.CODEC
  );

  public static final TrunkPlacerType<PromenadeShrubTrunkPlacer> PROMENADE_SHRUB_TRUNK = TrunkPlacerTypeInvoker.register(
    MineCells.createId("promenade_shrub").toString(),
    PromenadeShrubTrunkPlacer.CODEC
  );

  public static final FoliagePlacerType<PromenadeFoliagePlacer> PROMENADE_FOLIAGE_PLACER = FoliagePlacerTypeInvoker.register(
    MineCells.createId("promenade_foliage_placer").toString(),
    PromenadeFoliagePlacer.CODEC
  );

  public static void init() {
  }
}
