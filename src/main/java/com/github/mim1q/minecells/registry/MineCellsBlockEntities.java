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
    MineCellsBlocks.ELEVATOR_ASSEMBLER, MineCellsBlocks.CONJUNCTIVIUS_BOX, MineCellsBlocks.BEAM_PLACER,
    MineCellsBlocks.CONCIERGE_BOX
  );
  public static final BlockEntityType<FlagBlockEntity> FLAG_BLOCK_ENTITY = register(
    "biome_banner", FlagBlockEntity::new, MineCellsBlocks.FLAG_BLOCKS.toArray(new Block[0])
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
    MineCellsBlocks.INSUFFERABLE_CRYPT_DOORWAY, MineCellsBlocks.RAMPARTS_DOORWAY, MineCellsBlocks.BLACK_BRIDGE_DOORWAY
  );
  public static final BlockEntityType<BarrierControllerBlockEntity> BARRIER_CONTROLLER = register(
    "barrier_controller", BarrierControllerBlockEntity::new,
    MineCellsBlocks.BOSS_BARRIER_CONTROLLER, MineCellsBlocks.BOSS_ENTRY_BARRIER_CONTROLLER,
    MineCellsBlocks.PLAYER_BARRIER_CONTROLLER
  );
  public static final BlockEntityType<SpawnerRuneBlockEntity> SPAWNER_RUNE = register(
    "spawner_rune", SpawnerRuneBlockEntity::new, MineCellsBlocks.SPAWNER_RUNE
  );
  public static final BlockEntityType<RiftBlockEntity> RIFT = register(
    "rift", RiftBlockEntity::new, MineCellsBlocks.RIFT
  );
  public static final BlockEntityType<ArrowSignBlockEntity> ARROW_SIGN = register(
    "arrow_sign", ArrowSignBlockEntity::new, MineCellsBlocks.ARROW_SIGN
  );
  public static final BlockEntityType<CellCrafterBlockEntity> CELL_CRAFTER = register(
    "cell_crafter", CellCrafterBlockEntity::new, MineCellsBlocks.CELL_CRAFTER
  );

  public static void init() {
  }

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
