package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.*;
import com.github.mim1q.minecells.block.portal.DoorwayPortalBlockEntity;
import com.github.mim1q.minecells.block.portal.TeleporterBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class MineCellsBlockEntities {
  public static final BlockEntityType<SetupBlockEntity> SETUP_BLOCK_ENTITY = register(
    "setup_block_entity",
    SetupBlockEntity::new,
    MineCellsBlocks.ELEVATOR_ASSEMBLER, MineCellsBlocks.CONJUNCTIVIUS_BOX, MineCellsBlocks.BEAM_PLACER
  );
  public static final BlockEntityType<BiomeBannerBlockEntity> BIOME_BANNER_BLOCK_ENTITY = register(
    "biome_banner", BiomeBannerBlockEntity::new, MineCellsBlocks.BIOME_BANNER
  );
  public static final BlockEntityType<DecorativeStatueBlockEntity> DECORATIVE_STATUE_BLOCK_ENTITY = register(
    "decorative_statue", DecorativeStatueBlockEntity::new, MineCellsBlocks.KING_STATUE
  );
  public static final BlockEntityType<ReturnStoneBlockEntity> RETURN_STONE = register(
    "return_stone", ReturnStoneBlockEntity::new, MineCellsBlocks.RETURN_STONE
  );
  public static final BlockEntityType<RunicVinePlantBlockEntity> RUNIC_VINE_PLANT = register(
    "runic_vine_plant", RunicVinePlantBlockEntity::new, MineCellsBlocks.RUNIC_VINE_PLANT
  );
  public static final BlockEntityType<TeleporterBlockEntity> TELEPORTER = register(
    "teleporter", TeleporterBlockEntity::new, MineCellsBlocks.TELEPORTER_CORE
  );
  public static final BlockEntityType<DoorwayPortalBlockEntity> DOORWAY = register(
    "doorway", DoorwayPortalBlockEntity::new,
    MineCellsBlocks.OVERWORLD_DOORWAY, MineCellsBlocks.PRISON_DOORWAY, MineCellsBlocks.PROMENADE_DOORWAY,
    MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY,  MineCellsBlocks.RAMPARTS_DOORWAY
  );
  public static final BlockEntityType<BarrierControllerBlockEntity> BARRIER_CONTROLLER = register(
    "barrier_controller", BarrierControllerBlockEntity::new,
    MineCellsBlocks.BOSS_BARRIER_CONTROLLER, MineCellsBlocks.BOSS_ENTRY_BARRIER_CONTROLLER,
    MineCellsBlocks.PLAYER_BARRIER_CONTROLLER
  );

  public static void init() {}

  private static <T extends BlockEntity> BlockEntityType<T> register(
    String id,
    FabricBlockEntityTypeBuilder.Factory<T> factory,
    Block... blocks
  ) {
    return Registry.register(
      Registries.BLOCK_ENTITY_TYPE,
      MineCells.createId(id),
      FabricBlockEntityTypeBuilder.create(factory).addBlocks(blocks).build()
    );
  }
}
