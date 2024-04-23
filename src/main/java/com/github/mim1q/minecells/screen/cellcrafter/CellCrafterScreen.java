package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterRecipeList.DisplayedRecipe;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class CellCrafterScreen extends BaseOwoHandledScreen<FlowLayout, CellCrafterScreenHandler> {
  public static final Identifier SCREEN_TEXTURE = MineCells.createId("textures/gui/cell_crafter/container.png");
  private final CellCrafterRecipeList recipeList;
  private boolean isRecipeListVisible = false;
  private CellForgeRecipe selectedRecipe = null;
  private BlockPos blockPos = null;

  public CellCrafterScreen(CellCrafterScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
    recipeList = new CellCrafterRecipeList(this);

    ClientPlayNetworking.send(
      RequestUnlockedCellCrafterRecipesC2SPacket.ID,
      new RequestUnlockedCellCrafterRecipesC2SPacket(inventory.player)
    );

    handler.blockPos.observe(it -> blockPos = it);
  }

  public void updateRecipes(List<DisplayedRecipe> recipes) {
    recipeList.updateRecipes(recipes);
  }

  @Override
  @NotNull
  protected OwoUIAdapter<FlowLayout> createAdapter() {
    return OwoUIAdapter.create(this, Containers::verticalFlow);
  }

  @Override
  protected void build(FlowLayout rootComponent) {
    rootComponent
      .surface(Surface.VANILLA_TRANSLUCENT)
      .horizontalAlignment(HorizontalAlignment.CENTER)
      .verticalAlignment(VerticalAlignment.CENTER);

    if (isRecipeListVisible) {
      recipeList.build(rootComponent);
    } else {
      var container = Containers.verticalFlow(Sizing.fixed(178), Sizing.fixed(160));
      container
        .surface(backgroundTexture(SCREEN_TEXTURE, 256, 256))
        .horizontalAlignment(HorizontalAlignment.LEFT)
        .verticalAlignment(VerticalAlignment.TOP)
        .padding(Insets.of(8));

      container.child(
        new TexturedButton(it -> toggleRecipeList(), SCREEN_TEXTURE, 192, 48)
          .sizing(Sizing.fixed(64))
      );

      container.child(
        Components.texture(SCREEN_TEXTURE, 224, 0, 16, 16)
          .sizing(Sizing.fixed(16))
          .positioning(Positioning.absolute(100, 26))
      );

      container.child(
        new TexturedButton(it -> {
          if (selectedRecipe != null && blockPos != null) {
            new CellCrafterCraftRequestC2SPacket(selectedRecipe.getId(), blockPos).send();
          }
        }, SCREEN_TEXTURE, 208, 0)
          .sizing(Sizing.fixed(16))
          .positioning(Positioning.absolute(118, 26))
      );

      if (selectedRecipe != null) {
        container.child(
          Components.item(selectedRecipe.output())
            .sizing(Sizing.fixed(16))
            .positioning(Positioning.absolute(100, 44))
        );
      }

      rootComponent.child(container);
    }
  }

  @Override
  protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    // No-op
  }

  public void toggleRecipeList() {
    isRecipeListVisible = !isRecipeListVisible;

    for (int i = 0; i < 36; ++i) {
      if (isRecipeListVisible) disableSlot(i);
      else enableSlot(i);
    }

    this.uiAdapter.rootComponent.clearChildren();
    this.build(this.uiAdapter.rootComponent);
    this.recipeList.clearSearch();
  }

  public void setSelectedRecipe(CellForgeRecipe recipe) {
    this.selectedRecipe = recipe;
  }

  @Override
  public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
    if (amount > 0) {
      recipeList.scrollUp();
    } else if (amount < 0) {
      recipeList.scrollDown();
    }

    return true;
  }

  @Override
  public void close() {
    if (isRecipeListVisible) toggleRecipeList();
    else super.close();
  }

  public static Surface backgroundTexture(Identifier texture, int textureWidth, int textureHeight) {
    return (context, component) -> {
      context.drawTexture(
        texture,
        component.x(), component.y(),
        0, 0,
        component.width(), component.height(),
        textureWidth, textureHeight
      );
    };
  }

  public static class TexturedButton extends ButtonComponent {
    protected TexturedButton(Consumer<ButtonComponent> onPress, Identifier texture, int u, int v) {
      super(Text.empty(), onPress);
      this.renderer = Renderer.texture(texture, u, v, 256, 256);
    }
  }
}
