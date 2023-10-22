package com.github.mim1q.minecells;

import com.github.mim1q.minecells.accessor.PlayerEntityAccessor;
import com.github.mim1q.minecells.config.CommonConfig;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRunesReloadListener;
import com.github.mim1q.minecells.dimension.MineCellsDimensionGraph;
import com.github.mim1q.minecells.network.ServerPacketHandler;
import com.github.mim1q.minecells.registry.*;
import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.github.mim1q.minecells.world.feature.MineCellsFeatures;
import com.github.mim1q.minecells.world.feature.MineCellsPlacementModifiers;
import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.github.mim1q.minecells.world.feature.MineCellsStructurePlacementTypes;
import com.github.mim1q.minecells.world.state.MineCellsData;
import com.github.mim1q.minecells.world.state.PlayerSpecificMineCellsData;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineCells implements ModInitializer {

  public static final CommonConfig COMMON_CONFIG = OmegaConfig.register(CommonConfig.class);
  public static final SpawnerRunesReloadListener SPAWNER_RUNE_DATA = new SpawnerRunesReloadListener();

  public static final String MOD_ID = "minecells";
  public static final Logger LOGGER = LogManager.getLogger();

  public static final MineCellsDimensionGraph DIMENSION_GRAPH = new MineCellsDimensionGraph();

  @Override
  public void onInitialize() {
    COMMON_CONFIG.save();
    MineCellsEntities.init();
    MineCellsSounds.init();
    MineCellsBlocks.init();
    MineCellsBlockEntities.init();
    MineCellsFluids.init();
    MineCellsItems.init();
    MineCellsStatusEffects.init();
    MineCellsParticles.init();
    MineCellsStructures.init();
    MineCellsPlacerTypes.init();
    MineCellsStructureProcessorTypes.init();
    MineCellsFeatures.init();
    MineCellsStructurePlacementTypes.init();
    MineCellsPlacementModifiers.init();
    MineCellsPointOfInterestTypes.init();
    MineCellsCommands.init();
    MineCellsGameRules.init();
    MineCellsRecipeTypes.init();
    MineCellsScreenHandlerTypes.init();
    ServerPacketHandler.init();

    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SPAWNER_RUNE_DATA);
    ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> ((PlayerEntityAccessor)newPlayer).setMineCellsData(
      new PlayerSpecificMineCellsData(MineCellsData.get((ServerWorld) oldPlayer.getWorld()), newPlayer)
    ));
    ServerWorldEvents.LOAD.register(((server, world) -> {
      var data = MineCellsData.get(world);
      data.markDirty();
      data.wipeIfVersionMismatched(world);
    }));
  }

  public static Identifier createId(String path) {
    return new Identifier(MOD_ID, path);
  }
}
