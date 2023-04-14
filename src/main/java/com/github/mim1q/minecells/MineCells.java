package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.CommonConfig;
import com.github.mim1q.minecells.data.spawner_runes.SpawnerRunesReloadListener;
import com.github.mim1q.minecells.network.ServerPacketHandler;
import com.github.mim1q.minecells.registry.*;
import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.github.mim1q.minecells.world.feature.MineCellsPlacementModifiers;
import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.github.mim1q.minecells.world.state.MineCellsVersionCheckState;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineCells implements ModInitializer {

  public static final CommonConfig COMMON_CONFIG = OmegaConfig.register(CommonConfig.class);
  public static final SpawnerRunesReloadListener SPAWNER_RUNE_DATA = new SpawnerRunesReloadListener();

  public static final String MOD_ID = "minecells";
  public static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void onInitialize() {
    COMMON_CONFIG.save();
    MineCellsEntities.init();
    MineCellsSounds.init();
    MineCellsBlocks.init();
    MineCellsBlockEntities.init();
    MineCellsFluids.init();
    MineCellsItems.init();
    MineCellsItemGroups.init();
    MineCellsStatusEffects.init();
    MineCellsParticles.init();
    MineCellsBiomes.init();
    MineCellsStructures.init();
    MineCellsPlacerTypes.init();
    MineCellsPlacementModifiers.init();
    MineCellsPointOfInterestTypes.init();
    MineCellsCommands.init();
    MineCellsGameRules.init();
    MineCellsRecipeTypes.init();
    MineCellsScreenHandlerTypes.init();
    ServerPacketHandler.init();

    ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SPAWNER_RUNE_DATA);
    ServerPlayConnectionEvents.JOIN.register(MineCellsVersionCheckState::onOpPlayerJoin);
  }

  public static Identifier createId(String path) {
    return new Identifier(MOD_ID, path);
  }
}
