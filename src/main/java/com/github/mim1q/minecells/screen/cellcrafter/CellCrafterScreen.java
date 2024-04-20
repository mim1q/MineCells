package com.github.mim1q.minecells.screen.cellcrafter;

import com.github.mim1q.minecells.MineCells;
import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CellCrafterScreen extends BaseOwoHandledScreen<FlowLayout, CellCrafterScreenHandler> {
  public static final Identifier SCREEN_TEXTURE = MineCells.createId("textures/gui/cell_crafter/container.png");
  public static final Identifier RECIPES_SCREEN_TEXTURE = MineCells.createId("textures/gui/cell_crafter/recipes.png");

  public CellCrafterScreen(CellCrafterScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
  }

  @Override
  @NotNull
  protected OwoUIAdapter<FlowLayout> createAdapter() {
    return OwoUIAdapter.create(this, Containers::verticalFlow);
  }

  @Override protected void build(FlowLayout rootComponent) {
    rootComponent
      .surface(Surface.VANILLA_TRANSLUCENT)
      .horizontalAlignment(HorizontalAlignment.CENTER)
      .verticalAlignment(VerticalAlignment.CENTER);

    rootComponent.child(
      Containers.verticalFlow(Sizing.fixed(178), Sizing.fixed(160))
        .child(new RecipeButton(it -> System.out.println("test button clicked"))
          .sizing(Sizing.fixed(16))
        )
        .surface(backgroundTexture(SCREEN_TEXTURE, 256, 256))
        .horizontalAlignment(HorizontalAlignment.LEFT)
        .verticalAlignment(VerticalAlignment.TOP)
        .padding(Insets.of(8))
    );
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

  private static class RecipeButton extends ButtonComponent {
    protected RecipeButton(Consumer<ButtonComponent> onPress) {
      super(Text.empty(), onPress);
      this.renderer = Renderer.texture(SCREEN_TEXTURE, 192, 0, 256, 256);
    }
  }
}
