package com.github.mim1q.minecells.client.gui.screen;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.block.inventory.CellForgeBlueprintInventory;
import com.github.mim1q.minecells.block.inventory.CellForgeInventory;
import com.github.mim1q.minecells.client.gui.screen.button.ForgeButtonWidget;
import com.github.mim1q.minecells.client.gui.screen.slot.LockedSlot;
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
import net.minecraft.util.math.BlockPos;

public class CellForgeScreenHandler extends ScreenHandler {
  private final CellForgeInventory inventory;
  private final CellForgeBlueprintInventory blueprintInventory;
  private final PlayerEntity player;
  private final BlockPos pos;
  private CellForgeRecipe selectedRecipe = null;

  public CellForgeScreenHandler(int id, PlayerInventory playerInventory, PlayerEntity player, BlockPos pos) {
    super(MineCellsScreenHandlerTypes.CELL_FORGE, id);
    this.blueprintInventory = new CellForgeBlueprintInventory(player);
    this.inventory = new CellForgeInventory();
    this.player = player;
    this.pos = pos;

    for (int i = 0; i < 3; i++) {
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
  }

  public CellForgeScreenHandler(int i, PlayerInventory playerInventory, BlockPos pos) {
    this(i, playerInventory, playerInventory.player, pos);
  }

  public CellForgeScreenHandler(int i, PlayerInventory playerInventory) {
    this(i, playerInventory, playerInventory.player, BlockPos.ORIGIN);
  }

  @Override
  public ItemStack transferSlot(PlayerEntity player, int index) {
    return ItemStack.EMPTY;
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
    CellForgeRecipe recipe = this.blueprintInventory.getSelectedRecipe();
    return recipe == null ? this.selectedRecipe : recipe;
  }

  public void setSelectedRecipe(CellForgeRecipe selectedRecipe) {
    this.selectedRecipe = selectedRecipe;
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
}
