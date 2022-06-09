package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {

    public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(
            new Identifier(MineCells.MOD_ID, "eggs"))
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
            })
            .build();

    public static final ItemGroup MINECELLS_WEAPONS = FabricItemGroupBuilder.create(
            new Identifier(MineCells.MOD_ID, "weapons"))
            .icon(() -> new ItemStack(ItemRegistry.ASSASSINS_DAGGER))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(ItemRegistry.ASSASSINS_DAGGER));
            })
            .build();

    public static final ItemGroup MINECELLS_BLOCKS_AND_ITEMS = FabricItemGroupBuilder.create(
            new Identifier(MineCells.MOD_ID, "blocks_and_items"))
            .icon(() -> new ItemStack(BlockRegistry.ELEVATOR_ASSEMBLER))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(BlockRegistry.ELEVATOR_ASSEMBLER));
                stacks.add(new ItemStack(ItemRegistry.ELEVATOR_MECHANISM));
                stacks.add(new ItemStack(BlockRegistry.BIG_CHAIN));
            })
            .build();

    //endregion

    public static void register() { }
}
