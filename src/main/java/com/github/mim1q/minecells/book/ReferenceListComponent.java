package com.github.mim1q.minecells.book;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class ReferenceListComponent implements ICustomComponent {
  private IVariable references;

  private transient GuiBook bookGui = null;
  private transient List<Identifier> referencesList = new ArrayList<>();
  private transient List<BookEntry> entries = new ArrayList<>();

  private transient int y;
  private transient int x;

  @Override
  public void build(int componentX, int componentY, int pageNum) {
    entries.clear();
    bookGui = null;
    y = componentY;
    x = componentX;
  }

  @Override public void onDisplayed(IComponentRenderContext context) {
    if (entries.isEmpty() && context.getGui() instanceof GuiBook) {
      bookGui = (GuiBook) context.getGui();
      entries = bookGui.book.getContents().entries.values()
        .stream()
        .filter(entry -> referencesList.contains(entry.getId()))
        .toList();
    }

    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      var guiButton = new GuiButtonEntry(bookGui, x, y + 10 + i * 10, entry, (button) -> {
        context.navigateToEntry(entry.getId(), 0, true);
      });
      context.addWidget(guiButton, 1);
    }
  }

  @Override
  public void render(DrawContext graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {

  }

  @Override
  public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
    referencesList = lookup.apply(references)
      .asList()
      .stream()
      .map(it -> new Identifier(it.asString()))
      .toList();
  }
}
