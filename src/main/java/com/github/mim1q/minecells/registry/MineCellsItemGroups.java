package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MineCellsItemGroups {

  public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(MineCells.createId("eggs"))
    .icon(() -> new ItemStack(MineCellsEntities.LEAPING_ZOMBIE_SPAWN_EGG))
    .appendItems(stacks -> {
      stacks.add(new ItemStack(MineCellsEntities.LEAPING_ZOMBIE_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.SHOCKER_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.GRENADIER_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.DISGUSTING_WORM_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.INQUISITOR_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.KAMIKAZE_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.PROTECTOR_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.UNDEAD_ARCHER_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.SHIELDBEARER_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.MUTATED_BAT_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.SEWERS_TENTACLE_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.RANCID_RAT_SPAWN_EGG));
      stacks.add(new ItemStack(MineCellsEntities.RUNNER_SPAWN_EGG));
      stacks.add(MineCellsEntities.SCORPION_SPAWN_EGG.getDefaultStack());
    })
    .build();

  public static final ItemGroup MINECELLS_WEAPONS = FabricItemGroupBuilder.create(MineCells.createId("weapons"))
    .icon(() -> new ItemStack(MineCellsItems.ASSASSINS_DAGGER))
    .appendItems(stacks -> {
      stacks.add(new ItemStack(MineCellsItems.ASSASSINS_DAGGER));
    })
    .build();

  public static final ItemGroup MINECELLS_BLOCKS_AND_ITEMS = FabricItemGroupBuilder.create(MineCells.createId("blocks_and_items"))
    .icon(() -> new ItemStack(MineCellsBlocks.ELEVATOR_ASSEMBLER))
    .appendItems(stacks -> {
      stacks.add(MineCellsBlocks.HARDSTONE_ITEM.getDefaultStack());
      stacks.add(MineCellsBlocks.ELEVATOR_ASSEMBLER.asItem().getDefaultStack());
      stacks.add(MineCellsItems.ELEVATOR_MECHANISM.getDefaultStack());
      stacks.add(MineCellsBlocks.BIG_CHAIN.asItem().getDefaultStack());
      stacks.add(MineCellsItems.SEWAGE_BUCKET.getDefaultStack());
      stacks.add(MineCellsItems.ANCIENT_SEWAGE_BUCKET.getDefaultStack());
    })
    .build();

  //endregion

  public static void init() {
  }
}
