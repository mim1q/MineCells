package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.*;
import com.github.mim1q.minecells.block.blockentity.spawnerrune.SpawnerRuneBlockEntity;
import com.github.mim1q.minecells.block.blockentity.DecorativeStatueBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockEntities {
  public static final BlockEntityType<KingdomPortalCoreBlockEntity> KINGDOM_PORTAL_CORE_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("kingdom_portal_core"),
    FabricBlockEntityTypeBuilder
      .create(KingdomPortalCoreBlockEntity::new)
      .addBlock(MineCellsBlocks.KINGDOM_PORTAL_CORE)
      .build()
  );

  public static final BlockEntityType<SetupBlockEntity> SETUP_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("setup_block_entity"),
    FabricBlockEntityTypeBuilder
      .create(SetupBlockEntity::new)
      .addBlocks(
        MineCellsBlocks.ELEVATOR_ASSEMBLER,
        MineCellsBlocks.CONJUNCTIVIUS_BOX,
        MineCellsBlocks.BEAM_PLACER
      ).build()
  );

  public static final BlockEntityType<BiomeBannerBlockEntity> BIOME_BANNER_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("biome_banner"),
    FabricBlockEntityTypeBuilder
      .create(BiomeBannerBlockEntity::new)
      .addBlock(MineCellsBlocks.BIOME_BANNER)
      .build()
  );

  public static final BlockEntityType<SpawnerRuneBlockEntity> SPAWNER_RUNE_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("spawner_rune"),
    FabricBlockEntityTypeBuilder
      .create(SpawnerRuneBlockEntity::new)
      .addBlock(MineCellsBlocks.SPAWNER_RUNE)
      .build()
  );

  public static final BlockEntityType<ColoredTorchBlockEntity> COLORED_TORCH_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("colored_torch"),
    FabricBlockEntityTypeBuilder
      .create(ColoredTorchBlockEntity::new)
      .addBlocks(MineCellsBlocks.PRISON_TORCH, MineCellsBlocks.PROMENADE_TORCH)
      .build()
  );

  public static final BlockEntityType<DecorativeStatueBlockEntity> DECORATIVE_STATUE_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("decorative_statue"),
    FabricBlockEntityTypeBuilder.create(DecorativeStatueBlockEntity::new).addBlock(MineCellsBlocks.KING_STATUE).build()
  );

  public static final BlockEntityType<ReturnStoneBlockEntity> RETURN_STONE = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("return_stone"),
    FabricBlockEntityTypeBuilder.create(ReturnStoneBlockEntity::new).addBlock(MineCellsBlocks.RETURN_STONE).build()
  );

  public static void init() {}
}
