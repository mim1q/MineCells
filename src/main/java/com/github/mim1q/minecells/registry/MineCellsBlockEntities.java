package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.*;
import com.github.mim1q.minecells.block.blockentity.spawnerrune.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockEntities {
  public static final BlockEntityType<KingdomPortalCoreBlockEntity> KINGDOM_PORTAL_CORE_BLOCK_ENTITY = register(
    "kingdom_portal_core", KingdomPortalCoreBlockEntity::new, MineCellsBlocks.KINGDOM_PORTAL_CORE
  );
  public static final BlockEntityType<SetupBlockEntity> SETUP_BLOCK_ENTITY = register(
    "setup_block_entity",
    SetupBlockEntity::new,
    MineCellsBlocks.ELEVATOR_ASSEMBLER, MineCellsBlocks.CONJUNCTIVIUS_BOX, MineCellsBlocks.BEAM_PLACER
  );
  public static final BlockEntityType<BiomeBannerBlockEntity> BIOME_BANNER_BLOCK_ENTITY = register(
    "biome_banner", BiomeBannerBlockEntity::new, MineCellsBlocks.BIOME_BANNER
  );
  public static final BlockEntityType<SpawnerRuneBlockEntity> SPAWNER_RUNE_BLOCK_ENTITY = register(
    "spawner_rune", SpawnerRuneBlockEntity::new, MineCellsBlocks.SPAWNER_RUNE
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

  public static void init() {}

  private static <T extends BlockEntity> BlockEntityType<T> register(
    String id,
    FabricBlockEntityTypeBuilder.Factory<T> factory,
    Block... blocks
  ) {
    return Registry.register(
      Registry.BLOCK_ENTITY_TYPE,
      MineCells.createId(id),
      FabricBlockEntityTypeBuilder.create(factory).addBlocks(blocks).build()
    );
  }
}
