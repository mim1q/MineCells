package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.world.poi.PointOfInterestType;

public class MineCellsPointOfInterestTypes {
  public static final PointOfInterestType KINGDOM_PORTAL = PointOfInterestHelper.register(
    MineCells.createId("kingdom_portal"),
    1,
    1,
    MineCellsBlockEntities.KINGDOM_PORTAL_CORE
  );

  public static void init() { }
}
