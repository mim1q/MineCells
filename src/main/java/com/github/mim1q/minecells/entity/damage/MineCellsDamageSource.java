package com.github.mim1q.minecells.entity.damage;

import com.github.mim1q.minecells.MineCells;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public class MineCellsDamageSource {
  public final RegistryKey<DamageType> key;

  private MineCellsDamageSource(RegistryKey<DamageType> key) {
    this.key = key;
  }

  public DamageSource get(World world, Entity entity, Entity indirectEntity) {
    return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), entity, indirectEntity);
  }

  public DamageSource get(World world, Entity entity) {
    return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), entity);
  }

  public DamageSource get(World world) {
    return get(world, null);
  }

  public static final MineCellsDamageSource ELEVATOR = create("elevator");
  public static final MineCellsDamageSource CURSED = create("cursed");
  public static final MineCellsDamageSource BLEEDING = create("bleeding");
  public static final MineCellsDamageSource KATANA = create("katana");
  public static final MineCellsDamageSource BACKSTAB = create("backstab");
  public static final MineCellsDamageSource AURA = create("aura");
  public static final MineCellsDamageSource GRENADE = create("grenade");
  public static final MineCellsDamageSource HEAVY_BOLT = create("heavy_bolt");
  public static final MineCellsDamageSource ELECTRICITY = create("electricity");

  private static MineCellsDamageSource create(String name) {
    return new MineCellsDamageSource(RegistryKey.of(RegistryKeys.DAMAGE_TYPE, MineCells.createId(name)));
  }
}
