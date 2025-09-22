package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SendUnlockedCellCrafterRecipesS2CPacket(
  Map<Identifier, Boolean> requiredAdvancements
) {
  public SendUnlockedCellCrafterRecipesS2CPacket(ServerPlayerEntity player) {
    this(
      getRequiredAdvancements(
        player,
        player.server.getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE)
      )
    );
  }

  private static Map<Identifier, Boolean> getRequiredAdvancements(ServerPlayerEntity player, List<RecipeEntry<CellForgeRecipe>> recipes) {
    var requiredAdvancements = new HashMap<Identifier, Boolean>();
    for (var r : recipes) {
      var recipe = r.value();
      var entry = (recipe.requiredAdvancement().map(it -> {
        var advancement = player.server.getAdvancementLoader().get(it);
        return new Pair<>(r.id(), advancement == null || player.getAdvancementTracker().getProgress(advancement).isDone());
      }));
      entry.ifPresent(it -> requiredAdvancements.put(it.getLeft(), it.getRight()));
    }
    return requiredAdvancements;
  }
}
