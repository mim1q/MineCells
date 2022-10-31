package com.github.mim1q.minecells.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;

public class MineCellsDamageSource {
  public static final DamageSource ELEVATOR = new DamageSource("minecells.elevator").setBypassesArmor();
  public static final DamageSource CURSED = new DamageSource("minecells.cursed").setOutOfWorld();
  public static final DamageSource BLEEDING = new DamageSource("minecells.bleeding").setBypassesArmor().setBypassesProtection();

  public static DamageSource katana(Entity attacker) {
    return new EntityDamageSource("minecells.katana", attacker);
  }

  public static DamageSource backstab(Entity attacker) {
    return new EntityDamageSource("minecells.backstab", attacker);
  }

  public static DamageSource aura(Entity attacker) {
    return new EntityDamageSource("minecells.aura", attacker).setUsesMagic();
  }
}
