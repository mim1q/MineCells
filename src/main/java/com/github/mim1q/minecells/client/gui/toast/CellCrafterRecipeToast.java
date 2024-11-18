package com.github.mim1q.minecells.client.gui.toast;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class CellCrafterRecipeToast implements Toast {
  private static final Identifier TEXTURE = MineCells.createId("textures/gui/toast/background.png");
  private final CellForgeRecipe recipe;

  public CellCrafterRecipeToast(CellForgeRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public Visibility draw(DrawContext context, ToastManager manager, long time) {
    var textRenderer = MinecraftClient.getInstance().textRenderer;

    context.drawTexture(
      TEXTURE,
      0, 0,
      0, 0,
      160, 32
    );
    context.drawText(textRenderer, Text.translatable("toast.minecells.recipe_unlocked"), 30, 5, 0xffee4d, true);
    context.drawText(textRenderer, recipe.getOutput(null).getName(), 30, 15, 0xffffff, true);
    context.drawItem(recipe.getOutput(null).copyWithCount(1), 8, 6);

    return time > 5000 ? Visibility.HIDE : Visibility.SHOW;
  }
}
