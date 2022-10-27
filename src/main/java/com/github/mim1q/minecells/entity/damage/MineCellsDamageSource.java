package com.github.mim1q.minecells.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;

public class MineCellsDamageSource extends DamageSource {
  protected MineCellsDamageSource(String name) {
    super(name);
  }

  public static final DamageSource ELEVATOR = new MineCellsDamageSource("minecells_elevator").setBypassesArmor();
  public static final DamageSource CURSED = new MineCellsDamageSource("minecells_cursed").setOutOfWorld();

  public static final DamageSource BLEEDING = new MineCellsDamageSource("minecells_bleeding").setBypassesArmor().setBypassesProtection();

  public static DamageSource backstab(Entity attacker) {
    return new EntityDamageSource("minecells_backstab", attacker);
  }

  public static DamageSource aura(Entity attacker) {
    return new EntityDamageSource("minecells_aura", attacker).setUsesMagic();
  }
}
