package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemRegistry {

    //region ItemGroups

    public static final ItemGroup MINECELLS_EGGS = FabricItemGroupBuilder.create(
            new Identifier(MineCells.MOD_ID, "eggs"))
            .icon(() -> new ItemStack(EntityRegistry.JUMPING_ZOMBIE_SPAWN_EGG))
            .appendItems(stacks -> {
                stacks.add(new ItemStack(EntityRegistry.JUMPING_ZOMBIE_SPAWN_EGG));
                stacks.add(new ItemStack(EntityRegistry.SHOCKER_SPAWN_EGG));
                stacks.add(new ItemStack(EntityRegistry.GRENADIER_SPAWN_EGG));
            })
            .build();

    //endregion

    public static void register() {

    }
}
