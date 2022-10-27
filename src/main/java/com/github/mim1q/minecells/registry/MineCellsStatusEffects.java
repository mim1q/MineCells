package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.effect.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;


public class MineCellsStatusEffects {

  public static final StatusEffect CURSED = register(
    new MineCellsStatusEffect(StatusEffectCategory.HARMFUL, 0x000000, false, MineCellsEffectFlags.CURSED, false),
    "cursed"
  );
  public static final StatusEffect ELECTRIFIED = register(new ElectrifiedStatusEffect(), "electrified");
  public static final StatusEffect PROTECTED = register(new ProtectedStatusEffect(), "protected");
  public static final StatusEffect BLEEDING = register(new BleedingStatusEffect(), "bleeding");

  public static void init() { }

  public static StatusEffect register(StatusEffect effect, String name) {
    return Registry.register(Registry.STATUS_EFFECT, MineCells.createId(name), effect);
  }
}
