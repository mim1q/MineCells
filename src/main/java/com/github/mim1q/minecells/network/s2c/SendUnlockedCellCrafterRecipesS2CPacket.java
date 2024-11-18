package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsRecipeTypes;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterRecipeList.DisplayedRecipe;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreen;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SendUnlockedCellCrafterRecipesS2CPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("send_unlocked_cell_crafter_recipes");

  public SendUnlockedCellCrafterRecipesS2CPacket(ServerPlayerEntity player) {
    super(Unpooled.buffer());

    var recipes = player.server.getRecipeManager().listAllOfType(MineCellsRecipeTypes.CELL_FORGE_RECIPE_TYPE);
    writeInt(recipes.size());

    for (var recipe : recipes) {
      writeIdentifier(recipe.getId());
      writeBoolean(recipe.requiredAdvancement().map(it -> {
        var advancement = player.server.getAdvancementLoader().get(it);
        return advancement == null || player.getAdvancementTracker().getProgress(advancement).isDone();
      }).orElse(true));
    }
  }

  public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    var size = buf.readInt();

    List<DisplayedRecipe> recipes = new ArrayList<>();

    var recipeManager = handler.getRecipeManager();

    for (int i = 0; i < size; i++) {
      var recipeId = buf.readIdentifier();
      var recipe = (CellForgeRecipe) recipeManager.get(recipeId).orElseThrow();
      var isUnlocked = buf.readBoolean();
      recipes.add(new DisplayedRecipe(recipe, isUnlocked));
    }

    client.execute(() -> {
      var screen = client.currentScreen;
      if (screen instanceof CellCrafterScreen cellCrafterScreen) {
        cellCrafterScreen.updateRecipes(recipes);
      }
    });
  }
}
