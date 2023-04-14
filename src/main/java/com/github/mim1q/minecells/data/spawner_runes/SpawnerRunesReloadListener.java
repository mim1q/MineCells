package com.github.mim1q.minecells.data.spawner_runes;

import com.github.mim1q.minecells.MineCells;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class SpawnerRunesReloadListener implements SimpleSynchronousResourceReloadListener {
  private final Map<Identifier, SpawnerRuneData> data = new HashMap<>();

  @Override
  public Identifier getFabricId() {
    return MineCells.createId("spawner_runes");
  }

  @Override
  public void reload(ResourceManager manager) {
    data.clear();
    MineCells.LOGGER.info("Reloading Spawner Rune data...");
    var resources = manager.findResources("spawner_runes", id -> id.getPath().endsWith(".json"));
    resources.forEach((id, resource) -> {
      try (var stream = resource.getInputStream()) {
        addResource(
          new Identifier(
            id.getNamespace(),
            id.getPath().replace(".json", "").replace("spawner_runes/", "")),
          stream
        );
      } catch (IOException e) {
        MineCells.LOGGER.error("Failed to read resource: " + id.toString(), e);
      }
    });
  }

  public SpawnerRuneData get(Identifier id) {
    return data.getOrDefault(id, null);
  }

  public SpawnerRuneData get(String id) {
    return get(Identifier.tryParse(id));
  }

  private void addResource(Identifier id, InputStream stream) {
    SpawnerRuneData.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(new InputStreamReader(stream)))
      .resultOrPartial(MineCells.LOGGER::error)
      .ifPresent(result -> data.put(id, result));
  }
}
