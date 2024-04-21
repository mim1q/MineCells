package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreen.TexturedButton;
import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.github.mim1q.minecells.screen.cellcrafter.CellCrafterScreen.backgroundTexture;

public class CellCrafterRecipeList {
  public static final Identifier RECIPES_SCREEN_TEXTURE = MineCells.createId("textures/gui/cell_crafter/recipes.png");

  private final Runnable closeAction;

  private final List<DisplayedRecipe> allRecipes = new ArrayList<>();
  private final List<DisplayedRecipe> currentRecipes = new ArrayList<>();
  private final List<DisplayedRecipe> visibleRecipes = new ArrayList<>();
  private DisplayedRecipe selectedRecipe = null;

  private final GridLayout grid;

  public CellCrafterRecipeList(Runnable closeAction) {
    this.closeAction = closeAction;
    grid = Containers.grid(Sizing.content(), Sizing.content(), 5, 6);
    updateRecipes();
  }

  public void build(FlowLayout rootComponent) {
    var container = Containers.verticalFlow(Sizing.fixed(160), Sizing.fixed(176));
    container
      .surface(backgroundTexture(RECIPES_SCREEN_TEXTURE, 256, 256))
      .horizontalAlignment(HorizontalAlignment.LEFT)
      .verticalAlignment(VerticalAlignment.TOP)
      .padding(Insets.of(8))
      .allowOverflow(true);

    var textBox = new NoShadowTextBox(Sizing.fixed(70));
    textBox.setDrawsBackground(false);
    textBox.setRenderTextProvider((string, firstCharacterIndex) ->
      OrderedText.styledForwardsVisitedString(string, Style.EMPTY.withFormatting(Formatting.BLACK))
    );
    textBox.positioning(Positioning.absolute(58, 18));
    container.child(textBox);

    container.child(
      new TexturedButton(
        it -> this.closeAction.run(),
        RECIPES_SCREEN_TEXTURE,
        208, 0
      )
        .sizing(Sizing.fixed(16))
        .positioning(Positioning.absolute(4, 16))
    );

    container.child(
      new TexturedButton(
        it -> System.out.println("applied"),
        RECIPES_SCREEN_TEXTURE,
        224, 0
      )
        .sizing(Sizing.fixed(16))
        .positioning(Positioning.absolute(112, 138))
    );

    grid.positioning(Positioning.absolute(3, 34)).allowOverflow(true);

    container.child(grid);
    rootComponent.child(container);
  }

  public void updateRecipes(List<DisplayedRecipe> recipes) {
    allRecipes.clear();
    allRecipes.addAll(recipes);
    updateRecipes();
  }

  public void updateRecipes() {
    currentRecipes.clear();
    visibleRecipes.clear();

    currentRecipes.addAll(allRecipes);
    visibleRecipes.addAll(currentRecipes);

    for (int r = 0; r < 5; ++r) {
      for (int c = 0; c < 6; ++c) {
        grid.removeChild(r, c);
        var index = r * 6 + c;
        if (index >= visibleRecipes.size()) {
          continue;
        }
        var recipe = visibleRecipes.get(index);
        var component = new RecipeComponent(recipe, MinecraftClient.getInstance().textRenderer, this)
          .margins(Insets.of(1));
        grid.child(component, r, c);
      }
    }
  }

  public record DisplayedRecipe(
    CellForgeRecipe recipe,
    boolean isUnlocked
  ) {
  }

  private static class RecipeComponent extends ItemComponent {
    private final DisplayedRecipe recipe;
    private final TextRenderer textRenderer;
    private final CellCrafterRecipeList parent;

    public RecipeComponent(DisplayedRecipe recipe, TextRenderer textRenderer, CellCrafterRecipeList parent) {
      super(recipe.recipe().output());
      this.recipe = recipe;
      this.textRenderer = textRenderer;
      this.parent = parent;

      if (recipe.isUnlocked) {
        this.mouseDownEvents.source().subscribe((mouseX, mouseY, button) -> {
          MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
          parent.selectedRecipe = this.recipe;
          return true;
        });
        this.cursorStyle(CursorStyle.HAND);
      }

      this.sizing(Sizing.fixed(16));
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      var matrices = context.getMatrices();

      if (recipe.isUnlocked) super.draw(context, mouseX, mouseY, partialTicks, delta);

      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();

      if (recipe.isUnlocked) {
        if (this.hovered || parent.selectedRecipe == this.recipe) {
          matrices.push();
          matrices.translate(0, 0, 200);
          context.drawTexture(RECIPES_SCREEN_TEXTURE, x() - 3, y() - 3, 160, 96, 22, 22);
          matrices.pop();
        }
        if (this.hovered) {
          context.drawItemTooltip(textRenderer, recipe.recipe().output(), mouseX, mouseY);
        }
      } else {
        matrices.push();
        matrices.translate(0, 0, 300);
        context.drawTexture(RECIPES_SCREEN_TEXTURE, x(), y(), 208, 64, 16, 16);
        matrices.pop();
      }
      RenderSystem.disableDepthTest();
      RenderSystem.disableBlend();

    }
  }

  //#region Absolutely disgusting workaround to disable shadows in the textbox
  private static class DisableShadowDrawContext extends DrawContext {
    public DisableShadowDrawContext(DrawContext parent) {
      super(MinecraftClient.getInstance(), parent.getVertexConsumers());
      this.getMatrices().multiplyPositionMatrix(parent.getMatrices().peek().getPositionMatrix());
    }

    @Override
    public int drawTextWithShadow(TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
      return this.drawText(textRenderer, text, x, y, color, false);
    }
  }

  private static class NoShadowTextBox extends TextBoxComponent {
    protected NoShadowTextBox(Sizing horizontalSizing) {
      super(horizontalSizing);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
      super.renderButton(new DisableShadowDrawContext(context), mouseX, mouseY, delta);
    }
  }
  //#endregion
}
