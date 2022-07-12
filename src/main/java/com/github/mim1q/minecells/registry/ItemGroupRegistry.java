package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupRegistry {

  public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(MineCells.createId("eggs"))
    .icon(() -> new ItemStack(EntityRegistry.LEAPING_ZOMBIE_SPAWN_EGG))
    .appendItems(stacks -> {
      stacks.add(new ItemStack(EntityRegistry.LEAPING_ZOMBIE_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.SHOCKER_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.GRENADIER_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.DISGUSTING_WORM_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.INQUISITOR_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.KAMIKAZE_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.PROTECTOR_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.UNDEAD_ARCHER_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.SHIELDBEARER_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.MUTATED_BAT_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.SEWERS_TENTACLE_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.RANCID_RAT_SPAWN_EGG));
      stacks.add(new ItemStack(EntityRegistry.RUNNER_SPAWN_EGG));
      stacks.add(EntityRegistry.SCORPION_SPAWN_EGG.getDefaultStack());
    })
    .build();

  public static final ItemGroup MINECELLS_WEAPONS = FabricItemGroupBuilder.create(MineCells.createId("weapons"))
    .icon(() -> new ItemStack(ItemRegistry.ASSASSINS_DAGGER))
    .appendItems(stacks -> {
      stacks.add(new ItemStack(ItemRegistry.ASSASSINS_DAGGER));
    })
    .build();

  public static final ItemGroup MINECELLS_BLOCKS_AND_ITEMS = FabricItemGroupBuilder.create(MineCells.createId("blocks_and_items"))
    .icon(() -> new ItemStack(BlockRegistry.ELEVATOR_ASSEMBLER))
    .appendItems(stacks -> {
      stacks.add(BlockRegistry.HARDSTONE_ITEM.getDefaultStack());
      stacks.add(new ItemStack(BlockRegistry.ELEVATOR_ASSEMBLER));
      stacks.add(new ItemStack(ItemRegistry.ELEVATOR_MECHANISM));
      stacks.add(new ItemStack(BlockRegistry.BIG_CHAIN));
    })
    .build();

  //endregion

  public static void register() {
  }
}
