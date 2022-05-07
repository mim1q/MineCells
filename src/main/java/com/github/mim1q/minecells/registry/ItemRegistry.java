package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.AssassinsDaggerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {

    public static final AssassinsDaggerItem ASSASSINS_DAGGER_ITEM = new AssassinsDaggerItem(new FabricItemSettings()
        .group(ItemGroupRegistry.MINECELLS_WEAPONS)
        .maxDamage(1200));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(MineCells.MOD_ID, "assassins_dagger"), ASSASSINS_DAGGER_ITEM);
    }
}
