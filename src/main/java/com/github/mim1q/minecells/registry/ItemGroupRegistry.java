package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroupRegistry {

    public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(
            new Identifier(MineCells.MOD_ID, "eggs"))
            .icon(() -> new ItemStack(EntityRegistry.JUMPING_ZOMBIE_SPAWN_EGG))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(EntityRegistry.JUMPING_ZOMBIE_SPAWN_EGG));
                stacks.add(new ItemStack(EntityRegistry.SHOCKER_SPAWN_EGG));
                stacks.add(new ItemStack(EntityRegistry.GRENADIER_SPAWN_EGG));
                stacks.add(new ItemStack(EntityRegistry.DISGUSTING_WORM_SPAWN_EGG));
                stacks.add(new ItemStack(EntityRegistry.INQUISITOR_SPAWN_EGG));
            })
            .build();

    public static final ItemGroup MINECELLS_WEAPONS = FabricItemGroupBuilder.create(
            new Identifier(MineCells.MOD_ID, "weapons"))
            .icon(() -> new ItemStack(ItemRegistry.ASSASSINS_DAGGER_ITEM))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(ItemRegistry.ASSASSINS_DAGGER_ITEM));
            })
            .build();

    //endregion

    public static void register() { }
}
