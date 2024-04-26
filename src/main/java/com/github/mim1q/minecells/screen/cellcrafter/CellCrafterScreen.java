package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.network.c2s.CellCrafterCraftRequestC2SPacket;
import com.github.mim1q.minecells.network.c2s.RequestUnlockedCellCrafterRecipesC2SPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.screen.cellcrafter.CellCrafterRecipeList.DisplayedRecipe;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CellCrafterScreen extends BaseOwoHandledScreen<FlowLayout, CellCrafterScreenHandler> {
  public static final Identifier SCREEN_TEXTURE = MineCells.createId("textures/gui/cell_crafter/container.png");
  private final CellCrafterRecipeList recipeList;
  private final TexturedButton forgeButton;
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

    forgeButton = new TexturedButton(it -> {
      if (selectedRecipe != null && blockPos != null) {
        new CellCrafterCraftRequestC2SPacket(selectedRecipe.getId(), blockPos).send();
      }
    }, SCREEN_TEXTURE, 208, 0);
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
        .surface(backgroundTexture(SCREEN_TEXTURE, 178, 160))
        .horizontalAlignment(HorizontalAlignment.LEFT)
        .verticalAlignment(VerticalAlignment.TOP)
        .padding(Insets.of(8));

      container.child(
        new TexturedButton(it -> toggleRecipeList(), SCREEN_TEXTURE, 192, 48)
          .sizing(Sizing.fixed(64))
          .tooltip(Text.translatable("block.minecells.cell_crafter.view_recipes"))
      );

      if (selectedRecipe != null) {
        container.child(
          Components.texture(SCREEN_TEXTURE, 224, 0, 16, 16)
            .sizing(Sizing.fixed(16))
            .positioning(Positioning.absolute(100, 26))
        );

        container.child(
          forgeButton
            .sizing(Sizing.fixed(16))
            .positioning(Positioning.absolute(118, 26))
        );

        container.child(
          Components.item(selectedRecipe.output())
            .sizing(Sizing.fixed(16))
            .positioning(Positioning.absolute(100, 44))
            .tooltip(selectedRecipe.output().getTooltip(this.handler.player(), TooltipContext.BASIC))
        );

        container.child(
          new IngredientDisplay(handler.player().getInventory(), selectedRecipe, Sizing.fixed(96), Sizing.fixed(16))
            .positioning(Positioning.absolute(60, 6))
        );
      }

      rootComponent.child(container);
    }
  }

  @Override
  protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    // Update the forge button state, skip the super call
    forgeButton.active(
      selectedRecipe != null
        && selectedRecipe.matches(this.handler.player().getInventory(), this.handler.player().getWorld())
    );

    forgeButton.tooltip(
      forgeButton.active
        ? Text.translatable("block.minecells.cell_crafter.craft")
        : Text.translatable("block.minecells.cell_crafter.not_enough_ingredients")
    );
  }

  public void rebuild() {
    if (this.uiAdapter == null) return;

    this.uiAdapter.rootComponent.clearChildren();
    this.build(this.uiAdapter.rootComponent);
  }

  public void toggleRecipeList() {
    isRecipeListVisible = !isRecipeListVisible;

    for (int i = 0; i < 36; ++i) {
      if (isRecipeListVisible) disableSlot(i);
      else enableSlot(i);
    }

    rebuild();
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
        component.x() - textureWidth + component.width(), component.y(),
        0, 0,
        textureWidth, textureHeight,
        256, 256
      );
    };
  }

  public static class TexturedButton extends ButtonComponent {
    protected TexturedButton(Consumer<ButtonComponent> onPress, Identifier texture, int u, int v) {
      super(Text.empty(), onPress);
      this.renderer = Renderer.texture(texture, u, v, 256, 256);
    }
  }

  public static class IngredientDisplay extends FlowLayout {
    private final CellForgeRecipe recipe;
    private final PlayerInventory inventory;
    private final Map<ItemStack, Pair<ItemComponent, LabelComponent>> itemLabels = new java.util.HashMap<>();

    public IngredientDisplay(PlayerInventory inventory, CellForgeRecipe recipe, Sizing horizontalSizing, Sizing verticalSizing) {
      super(horizontalSizing, verticalSizing, Algorithm.HORIZONTAL);
      this.inventory = inventory;
      this.recipe = recipe;

      horizontalAlignment(HorizontalAlignment.CENTER);
      allowOverflow(true);

      setup();
    }

    public void setup() {
      clearChildren();

      for (var ingredient : recipe.ingredients().entrySet()) {
        var itemStack = ingredient.getKey().getDefaultStack();
        var box = Containers.horizontalFlow(Sizing.fixed(16), Sizing.fixed(16));
        box.allowOverflow(true);

        var itemComponent = Components.item(itemStack);
        itemComponent
          .sizing(Sizing.fixed(16))
          .tooltip(tooltip);
        box.child(itemComponent);

        var label = Components.label(Text.literal("" + ingredient.getValue()));
        label
          .shadow(true)
          .horizontalTextAlignment(HorizontalAlignment.CENTER)
          .positioning(Positioning.absolute(6, 10))
          .horizontalSizing(Sizing.fixed(16))
          .zIndex(300);
        box.child(label);

        itemLabels.put(itemStack, new Pair<>(itemComponent, label));
        child(box);
      }

      updateLabels();
    }

    private void updateLabels() {
      for (var entry : itemLabels.entrySet()) {
        var ingredient = entry.getKey();

        var tooltip = ingredient.getTooltip(null, TooltipContext.BASIC);
        var itemComponent = entry.getValue().getLeft();
        var label = entry.getValue().getRight();

        var firstLine = tooltip.get(0);
        if (firstLine != null) {
          var sibling = firstLine.getSiblings().get(0);
          if (sibling != null) {
            MutableText text = MutableText.of(sibling.getContent());
            text.append(" x" + ingredient.getCount());
            tooltip.clear();
            tooltip.add(firstLine);
            firstLine.getSiblings().set(0, text);
          }
        }

        var hasEnough = inventory == null || inventory.count(ingredient.getItem()) >= ingredient.getCount();
        if (!hasEnough) {
          tooltip.addAll(Text.translatable("block.minecells.cell_crafter.not_enough_of_this_item").getWithStyle(Style.EMPTY.withColor(0xFF0000)));
        }

        var color = hasEnough ? 0x99ffa9 : 0xff7b7d;
        if (inventory == null) color = 0xFFFFFF;

        itemComponent.tooltip(tooltip);
        label.color(Color.ofArgb(color));
      }
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
      updateLabels();
      super.draw(context, mouseX, mouseY, partialTicks, delta);
    }
  }
}
