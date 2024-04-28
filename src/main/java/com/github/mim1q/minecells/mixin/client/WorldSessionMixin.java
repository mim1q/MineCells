package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.client.gui.toast.CellCrafterRecipeToast;
import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSession.class)
public class WorldSessionMixin {
  @Inject(method = "onAdvancementMade", at = @At("HEAD"))
  void minecells$onAdvancementMade(World world, Advancement advancement, CallbackInfo ci) {
    AdvancementHintRenderer.setAdvancementRendered(advancement.getId(), false);

    // Display toasts for unlocked Cell Crafter recipes
    var allRecipes = world.getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE);
    var recipes = allRecipes.stream()
      .filter(
        recipe -> recipe.requiredAdvancement()
          .map(it -> advancement.getId().equals(it))
          .orElse(false)
      );
    recipes.forEach(recipe -> MinecraftClient.getInstance().getToastManager().add(new CellCrafterRecipeToast(recipe)));
  }

  @Inject(method = "onLoad", at = @At("HEAD"))
  void minecells$onLoad(CallbackInfo ci) {
    AdvancementHintRenderer.resetAdvancements();
  }
}
