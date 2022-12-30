package com.github.mim1q.minecells.world.state;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;

public class MineCellsVersionCheckState extends PersistentState {
  // Modify these when significant changes are made to respective aspects of the mod
  private String prison = "1.3.2";
  private String insufferableCrypt = "1.3.3";

  private MineCellsVersionCheckState(NbtCompound nbt) {
    this.prison = nbt.getString("prison");
    this.insufferableCrypt = nbt.getString("insufferable_crypt");
  }

  private MineCellsVersionCheckState() {}

  public static MineCellsVersionCheckState getDefault() {
    return new MineCellsVersionCheckState();
  }

  public static MineCellsVersionCheckState getFromNbt(NbtCompound nbt) {
    return new MineCellsVersionCheckState(nbt);
  }

  @Override
  public NbtCompound writeNbt(NbtCompound nbt) {
    nbt.putString("prison", this.prison);
    nbt.putString("insufferable_crypt", this.insufferableCrypt);

    return nbt;
  }

  public List<String> compareDifferences(MineCellsVersionCheckState other) {
    List<String> result = new ArrayList<>();
    if (!this.prison.equals(other.prison) && !this.prison.isEmpty()) {
      result.add("chat.minecells.version_mismatch.prison");
    }
    if (!this.insufferableCrypt.equals(other.insufferableCrypt) && !this.prison.isEmpty()) {
      result.add("chat.minecells.version_mismatch.insufferable_crypt");
    }
    return result;
  }

  public static void onOpPlayerJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
    ServerPlayerEntity player = handler.getPlayer();
    if (!(player.isCreativeLevelTwoOp() || server.isSingleplayer())) {
      return;
    }
    ServerWorld overworld = server.getOverworld();
    MineCellsVersionCheckState state = overworld.getPersistentStateManager().getOrCreate(MineCellsVersionCheckState::getFromNbt, MineCellsVersionCheckState::getDefault, "MineCellsVersionCheck");
    state.markDirty();
    MineCellsVersionCheckState defaultState = MineCellsVersionCheckState.getDefault();
    List<String> diff = state.compareDifferences(defaultState);
    if (diff.isEmpty()) {
      return;
    }
    MutableText text = Text.literal("\n[Mine Cells] ").formatted(Formatting.RED)
      .append(Text.translatable("chat.minecells.version_mismatch").formatted(Formatting.WHITE));
    for (String dimension : diff) {
      text = text.append(Text.literal("\n")).append(Text.translatable(dimension).formatted(Formatting.WHITE));
    }
    final String url = "https://github.com/mim1q/MineCells/wiki/Updating-Mine-Cells";
    text = text.append(Text.translatable("chat.minecells.version_mismatch.link")
      .formatted(Formatting.WHITE, Formatting.UNDERLINE)
      .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url))));

    player.sendMessage(text.append(Text.literal("\n")));
    MineCells.LOGGER.warn("\n" + text.getString() + ": " + url + "\n");
    overworld.getPersistentStateManager().set("MineCellsVersionCheck", defaultState);
    defaultState.markDirty();
  }
}
