package com.github.mim1q.minecells.client.gui.screen;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.inventory.CellForgeBlueprintInventory;
import com.github.mim1q.minecells.block.inventory.CellForgeInventory;
import com.github.mim1q.minecells.client.gui.screen.button.ForgeButtonWidget;
import com.github.mim1q.minecells.client.gui.screen.slot.LockedSlot;
import com.github.mim1q.minecells.mixin.client.SlotAccessor;
import com.github.mim1q.minecells.network.RequestForgeC2SPacket;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import com.github.mim1q.minecells.registry.MineCellsScreenHandlerTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CellForgeScreenHandler extends ScreenHandler {
  private final CellForgeInventory inventory;
  private final CellForgeBlueprintInventory blueprintInventory;
  private final PlayerEntity player;
  private final BlockPos pos;
  public List<Slot> blueprintSlots = new ArrayList<>();
  private int offset = 0;

  public CellForgeScreenHandler(int id, PlayerInventory playerInventory, PlayerEntity player, BlockPos pos) {
    super(MineCellsScreenHandlerTypes.CELL_FORGE, id);
    this.blueprintInventory = new CellForgeBlueprintInventory(player);
    this.inventory = new CellForgeInventory();
    this.player = player;
    this.pos = pos;

    for (int i = 0; i < blueprintInventory.getRows(); i++) {
      for (int j = 0; j < 9; j++) {
        this.addSlot(new LockedSlot(blueprintInventory, j + i * 9, 9 + j * 18, 18 + i * 18));
      }
    }

    for (int i = 0; i < 6; i++) {
      this.addSlot(new Slot(inventory, i, 54 + i * 18, 87));
    }

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 9; j++) {
        this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 18 + j * 18, 119 + i * 18));
      }
    }

    for (int i = 0; i < 9; i++) {
      this.addSlot(new Slot(playerInventory, i, 18 + i * 18, 177));
    }

    updateBlueprintSlots();
  }

  @Override
  protected Slot addSlot(Slot slot) {
    if (slot.inventory == blueprintInventory) {
      blueprintSlots.add(slot);
    }
    return super.addSlot(slot);
  }

  public CellForgeScreenHandler(int i, PlayerInventory playerInventory, BlockPos pos) {
    this(i, playerInventory, playerInventory.player, pos);
  }

  public CellForgeScreenHandler(int i, PlayerInventory playerInventory) {
    this(i, playerInventory, playerInventory.player, BlockPos.ORIGIN);
  }

  @Override
  public ItemStack transferSlot(PlayerEntity player, int index) {
    Slot slot = this.getSlot(index);
    ItemStack slotStack = slot.getStack();
    if (slot.inventory == this.inventory) {
      player.getInventory().offerOrDrop(slotStack);
      return slotStack;
    } else if (slot.inventory == player.getInventory()) {
      var transfer = getTransferableSlotAndCount(slotStack);
      if (transfer == null) {
        return ItemStack.EMPTY;
      }
      Slot transferSlot = transfer.getLeft();
      ItemStack transferredStack = slotStack.split(transfer.getRight());
      transferredStack.setCount(transferredStack.getCount() + transferSlot.getStack().getCount());
      transferSlot.setStack(transferredStack);
    }
    return ItemStack.EMPTY;
  }

  protected Pair<Slot, Integer> getTransferableSlotAndCount(ItemStack stack) {
    CellForgeRecipe recipe = this.getSelectedRecipe();
    if (recipe == null) {
      return null;
    }
    List<ItemStack> input = recipe.getInput();
    for (int i = 0; i < input.size(); i++) {
      ItemStack recipeStack = input.get(i);
      ItemStack inventoryStack = this.inventory.getStack(i);

      if (recipeStack.getItem() == stack.getItem()) {
        if (inventoryStack.isEmpty() || inventoryStack.getCount() < recipeStack.getCount()) {
          int idx = this.getSlotIndex(this.inventory, i).orElse(-1);
          return idx == -1 ? null : new Pair<>(this.getSlot(idx), recipeStack.getCount() - inventoryStack.getCount());
        }
      }
    }
    return null;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return true;
  }

  @Override
  public void updateToClient() {
    super.updateToClient();
  }

  public CellForgeRecipe getSelectedRecipe() {
    return this.blueprintInventory.getSelectedRecipe();
  }

  public int getSelectedRecipeSlotIndex() {
    return this.blueprintInventory.getSelectedRecipeIndex();
  }

  public void setSelectedRecipe(CellForgeRecipe selectedRecipe, int slot) {
    this.blueprintInventory.setSelectedRecipe(selectedRecipe, slot);
  }

  public BlockPos getPos() {
    return pos;
  }

  public boolean canForge() {
    CellForgeRecipe recipe = this.getSelectedRecipe();
    if (recipe == null) return false;
    int cells = recipe.getCells();
    if (((PlayerEntityAccessor) player).getCells() < cells) return false;
    return recipe.matches(inventory, player.world);
  }

  public static void onForgeButtonClicked(ButtonWidget buttonWidget) {
    CellForgeScreenHandler handler = ((ForgeButtonWidget) buttonWidget).handler;
    if (handler.canForge()) {
      ClientPlayNetworking.send(RequestForgeC2SPacket.ID, new RequestForgeC2SPacket(handler.getPos()));
    }
  }

  public void forge(BlockPos pos) {
    CellForgeRecipe recipe = this.getSelectedRecipe();
    if (recipe == null) return;
    int cells = recipe.getCells();
    ((PlayerEntityAccessor) player).setCells(((PlayerEntityAccessor) player).getCells() - cells);
    ItemStack stack = recipe.craft(inventory);
    player.dropItem(stack, false);
  }

  @Override
  public void close(PlayerEntity player) {
    super.close(player);
    this.inventory.onClose(player);
  }

  public boolean canScrollUp() {
    return offset > 0;
  }

  public boolean canScrollDown() {
    return offset < blueprintInventory.getRows() - 3;
  }

  public void scrollUp() {
    if (canScrollUp()) {
      offset--;
      updateBlueprintSlots();
    }
  }

  public void scrollDown() {
    if (canScrollDown()) {
      offset++;
      updateBlueprintSlots();
    }
  }

  public void updateBlueprintSlots() {
    for (int i = 0; i < blueprintSlots.size(); i++) {
      Slot slot = blueprintSlots.get(i);
      int y = i / 9;
      ((SlotAccessor) slot).setY(y * 18 + 18 - offset * 18);
      ((LockedSlot) slot).enabled = y >= offset && y < offset + 3;
    }
  }
}
