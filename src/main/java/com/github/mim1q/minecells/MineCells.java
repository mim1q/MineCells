package com.github.mim1q.minecells;

import com.github.mim1q.minecells.config.CommonConfig;
import com.github.mim1q.minecells.network.ServerPacketHandler;
import com.github.mim1q.minecells.registry.*;
import com.github.mim1q.minecells.structure.MineCellsStructures;
import com.github.mim1q.minecells.world.feature.MineCellsPlacerTypes;
import com.github.mim1q.minecells.world.state.MineCellsVersionCheckState;
import draylar.omegaconfig.OmegaConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MineCells implements ModInitializer {

  public static final CommonConfig COMMON_CONFIG = OmegaConfig.register(CommonConfig.class);

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
    MineCellsPointOfInterestTypes.init();
    MineCellsCommands.init();
    MineCellsGameRules.init();
    MineCellsRecipeTypes.init();
    MineCellsScreenHandlerTypes.init();
    ServerPacketHandler.init();

    ServerPlayConnectionEvents.JOIN.register(MineCellsVersionCheckState::onOpPlayerJoin);

//    ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
//      if (MineCellsDimensions.isMineCellsDimension(oldPlayer.getWorld())) {
//        ((PlayerEntityAccessor) newPlayer).setKingdomPortalCooldown(20);
//        Vec3d pos = Vec3d.of(MathUtils.getClosestMultiplePosition(oldPlayer.getBlockPos(), 4096));
//        newPlayer.teleport(
//          oldPlayer.getWorld(),
//          pos.x + 8,
//          pos.y + 32,
//          pos.z + 8,
//          oldPlayer.getYaw(),
//          oldPlayer.getPitch()
//        );
//      }
//    });
  }

  public static Identifier createId(String path) {
    return new Identifier(MOD_ID, path);
  }
}
