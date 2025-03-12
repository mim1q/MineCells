package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.cc.MineCellsLevelCC;
import net.minecraft.server.network.ServerPlayerEntity;

public interface DimensionRequirement {
  boolean isMet(MineCellsDimension dimension, ServerPlayerEntity player, MineCellsLevelCC.PortalData portal);


  record AccessFromDimension(
    MineCellsDimension from
  ) implements DimensionRequirement {

    @Override
    public boolean isMet(MineCellsDimension dimension, ServerPlayerEntity player, MineCellsLevelCC.PortalData portal) {
      return portal.visitedDimensions().contains(dimension);
    }
  }


}
