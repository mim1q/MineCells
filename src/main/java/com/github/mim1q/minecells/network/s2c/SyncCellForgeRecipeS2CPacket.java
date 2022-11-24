package com.github.mim1q.minecells.network.s2c;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.client.gui.screen.CellForgeScreenHandler;
import com.github.mim1q.minecells.recipe.CellForgeRecipe;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class SyncCellForgeRecipeS2CPacket extends PacketByteBuf {
  public static final Identifier ID = MineCells.createId("sync_cell_forge");

  public SyncCellForgeRecipeS2CPacket(CellForgeRecipe recipe, int slot) {
    super(Unpooled.buffer());
    writeIdentifier(recipe.getId());
    writeInt(slot);
  }

  public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    Identifier recipeId = buf.readIdentifier();
    int slot = buf.readInt();
    client.execute(() -> {
      Optional<? extends Recipe<?>> recipe = handler.getRecipeManager().get(recipeId);
      ClientPlayerEntity player = client.player;
      if (recipe.isEmpty() || player == null) {
        return;
      }
      ScreenHandler screenHandler = player.currentScreenHandler;
      if (screenHandler instanceof CellForgeScreenHandler forgeScreenHandler) {
        forgeScreenHandler.setSelectedRecipe((CellForgeRecipe) recipe.get(), slot);
      }
    });
  }
}
