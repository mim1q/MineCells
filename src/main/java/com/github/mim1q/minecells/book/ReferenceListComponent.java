package com.github.mim1q.minecells.book;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ReferenceListComponent implements ICustomComponent {
  private IVariable content;

  private transient GuiBook bookGui = null;
  private final transient Map<Integer, String> titles = new HashMap<>();
  private final transient List<Identifier> referencesList = new ArrayList<>();
  private static List<BookEntry> entries = new ArrayList<>();

  private transient int y = 0;
  private transient int x = 0;
  private transient int pageNum = 0;
  private static final ArrayList<ButtonWidget> buttons = new ArrayList<>();

  @Override
  public void build(int componentX, int componentY, int pageNum) {
    entries.clear();
    y = componentY;
    x = componentX;
    this.pageNum = pageNum;
  }

  @Override
  public void onDisplayed(IComponentRenderContext context) {
    if (context.getGui() instanceof GuiBook) {
      bookGui = (GuiBook) context.getGui();
    }

    entries = referencesList.stream()
      .map(it -> bookGui.book.getContents().entries.get(it))
      .filter(Objects::nonNull)
      .collect(Collectors.toCollection(ArrayList::new));

    int buttonY = y + 5;
    for (var i = 0; i < entries.size(); i++) {
      if (titles.get(i) != null) {
        buttonY += 20;
      }
      var entry = entries.get(i);
      var guiButton = new GuiButtonEntry(bookGui, x, buttonY, entry, (button) -> {
        context.navigateToEntry(entry.getId(), 0, false);
      });
      context.addWidget(guiButton, pageNum);
      buttons.add(guiButton);
      buttonY += 10;
    }
  }

  @Override
  public void render(DrawContext graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
    var spaceAtomic = new AtomicInteger(-3);
    var textRenderer = MinecraftClient.getInstance().textRenderer;
    titles.forEach((key, value) -> {
      var space = spaceAtomic.getAndAdd(20);
      graphics.drawText(textRenderer, Text.literal(value), x, y + space + key * 10, 0x404040, false);
    });
  }


  @Override
  public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
    var tempList = lookup.apply(content)
      .asList()
      .stream()
      .map(IVariable::asString)
      .toList();

    var i = 0;
    for (var entry : tempList) {
      if (entry.startsWith("# ")) {
        titles.put(i, entry.substring(2));
      } else {
        referencesList.add(new Identifier(entry));
        i++;
      }
    }
  }

  public static void clearButtons() {
    var screen = MinecraftClient.getInstance().currentScreen;
    if (screen instanceof GuiBook book) {
      book.removeDrawablesIn(buttons);
      buttons.clear();
    }
  }
}
