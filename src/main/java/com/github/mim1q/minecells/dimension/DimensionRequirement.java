package com.github.mim1q.minecells.dimension;

import com.github.mim1q.minecells.cc.MineCellsLevelCC;
import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface DimensionRequirement {
  boolean isMet(MineCellsDimension dimension, PlayerEntity player, @Nullable MineCellsLevelCC.PortalData portal);

  Text getTooltip();

  record AccessFromDimension(
    MineCellsDimension from
  ) implements DimensionRequirement {

    @Override
    public boolean isMet(MineCellsDimension dimension, PlayerEntity player, MineCellsLevelCC.PortalData portal) {
      if (portal == null) return false;
      return portal.visitedDimensions().contains(dimension);
    }

    @Override
    public Text getTooltip() {
      return Text.translatable("gui.minecells.doorway_selection.requirement.visit", Text.translatable(from.translationKey));
    }
  }

  record GetAdvancement(
    Identifier advancementId
  ) implements DimensionRequirement {
    @Override
    public boolean isMet(MineCellsDimension dimension, PlayerEntity player, MineCellsLevelCC.PortalData portal) {
      if (player instanceof ServerPlayerEntity serverPlayer) {
        var advancement = serverPlayer.server.getAdvancementLoader().get(advancementId);
        return serverPlayer.getAdvancementTracker().getProgress(advancement).isDone();
      } else if (player.getWorld().isClient()) {
        // Workaround
        return !AdvancementHintRenderer.isAdvancementRendered(advancementId);
      }
      return false;
    }

    @Override
    public Text getTooltip() {
      return Text.translatable("gui.minecells.doorway_selection.requirement.advancement." + advancementId.getPath());
    }
  }
}
