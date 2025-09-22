package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record SendUnlockedCellCrafterRecipesS2CPacket(
  Map<Identifier, Boolean> requiredAdvancements
) {
  public SendUnlockedCellCrafterRecipesS2CPacket(ServerPlayerEntity player) {
    this(
      getRecipesWithUnlockStatus(
        player,
        player.server.getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE)
      )
    );
  }

  private static Map<Identifier, Boolean> getRecipesWithUnlockStatus(ServerPlayerEntity player, List<RecipeEntry<CellForgeRecipe>> recipes) {
    var recipesMap = new HashMap<Identifier, Boolean>();
    for (var recipeEntry : recipes) {
      var recipe = recipeEntry.value();
      var isUnlocked = (recipe.requiredAdvancement().map(it -> {
        var advancementEntry = player.server.getAdvancementLoader().get(it);
        return advancementEntry == null || player.getAdvancementTracker().getProgress(advancementEntry).isDone();
      }));
      recipesMap.put(recipeEntry.id(), isUnlocked.orElse(true));
    }
    return recipesMap;
  }
}
