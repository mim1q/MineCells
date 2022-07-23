package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.AssassinsDaggerItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ItemRegistry {

  public static final Item ELEVATOR_MECHANISM = new Item(new FabricItemSettings().group(ItemGroupRegistry.MINECELLS_BLOCKS_AND_ITEMS));

  public static final AssassinsDaggerItem ASSASSINS_DAGGER = new AssassinsDaggerItem(new FabricItemSettings()
    .group(ItemGroupRegistry.MINECELLS_WEAPONS)
    .maxDamage(1200));

  public static void register() {
    Registry.register(Registry.ITEM, MineCells.createId("elevator_mechanism"), ELEVATOR_MECHANISM);

    Registry.register(Registry.ITEM, MineCells.createId("assassins_dagger"), ASSASSINS_DAGGER);
  }
}
