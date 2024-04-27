package com.github.mim1q.minecells.mixin.client;

import com.github.mim1q.minecells.client.gui.toast.CellCrafterRecipeToast;
import com.github.mim1q.minecells.client.render.misc.AdvancementHintRenderer;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {
  @Shadow @Final private MinecraftClient client;

  @Inject(method = "onAdvancements", at = @At("HEAD"))
  private void minecells$onAdvancements(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
    packet.getAdvancementsToEarn().forEach(
      (id, builder) -> AdvancementHintRenderer.setAdvancementRendered(id, false)
    );
    packet.getAdvancementIdsToRemove().forEach(
      id -> AdvancementHintRenderer.setAdvancementRendered(id, true)
    );

    // Display toasts for unlocked Cell Crafter recipes
    var world = client.world;
    if (world == null) return;
    var allRecipes = world.getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE);

    packet.getAdvancementsToEarn().forEach((id, builder) -> {
      var recipes = allRecipes.stream().filter(recipe -> recipe.requiredAdvancement().map(it -> it.equals(id)).orElse(false));
      recipes.forEach(recipe -> client.getToastManager().add(new CellCrafterRecipeToast(recipe)));
    });
  }
}
