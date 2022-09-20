package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.block.blockentity.MonsterBoxBlock;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlock;
import com.github.mim1q.minecells.block.blockentity.KingdomPortalCoreBlockEntity;
import com.github.mim1q.minecells.block.blockentity.SetupBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

public class MineCellsBlockEntities {

  public static final Block KINGDOM_PORTAL_CORE = MineCellsBlocks.registerBlock(
    new KingdomPortalCoreBlock(FabricBlockSettings.copyOf(Blocks.BEDROCK).luminance(
      state -> state.get(KingdomPortalCoreBlock.LIT) ? 8 : 0)
    ),
    "kingdom_portal_core"
  );

  public static final BlockEntityType<KingdomPortalCoreBlockEntity> KINGDOM_PORTAL_CORE_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("kingdom_portal_core"),
    FabricBlockEntityTypeBuilder
      .create(KingdomPortalCoreBlockEntity::new)
      .addBlock(KINGDOM_PORTAL_CORE)
      .build()
  );

  public static final Block KINGDOM_PORTAL_FILLER = MineCellsBlocks.registerBlock(
    new KingdomPortalCoreBlock.Filler(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)),
    "kingdom_portal_filler"
  );

  public static final Block PRISON_BOX = MineCellsBlocks.registerBlock(
    new MonsterBoxBlock(
      new MonsterBoxBlock.Entry(MineCellsEntities.LEAPING_ZOMBIE, 1.0F, 2),
      new MonsterBoxBlock.Entry(MineCellsEntities.LEAPING_ZOMBIE, 0.5F, 1),
      new MonsterBoxBlock.Entry(MineCellsEntities.UNDEAD_ARCHER, 0.75F, 2),
      new MonsterBoxBlock.Entry(MineCellsEntities.GRENADIER, 0.5F, 1),
      new MonsterBoxBlock.Entry(MineCellsEntities.SHIELDBEARER, 0.5F, 1)
    ),
    "prison_box"
  );

  public static final Block CONJUNCTIVIUS_BOX = MineCellsBlocks.registerBlock(
    new MonsterBoxBlock(MineCellsEntities.CONJUNCTIVIUS),
    "conjunctivius_box"
  );

  public static final Block SHOCKER_BOX = MineCellsBlocks.registerBlock(
    new MonsterBoxBlock(MineCellsEntities.SHOCKER),
    "shocker_box"
  );

  public static final BlockEntityType<SetupBlockEntity> SETUP_BLOCK_ENTITY = Registry.register(
    Registry.BLOCK_ENTITY_TYPE,
    MineCells.createId("setup_block_entity"),
    FabricBlockEntityTypeBuilder
      .create(SetupBlockEntity::new)
      .addBlocks(PRISON_BOX, CONJUNCTIVIUS_BOX, SHOCKER_BOX)
      .build()
  );

  public static void init() {}
}
