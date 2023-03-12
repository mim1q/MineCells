package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.effect.*;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.registry.Registry;


public class MineCellsStatusEffects {
  private static final String ASSASSINS_STRENGTH_UUID = "3a0efcb6-2dfe-46d8-86ed-7c9246afc29a";

  public static final StatusEffect CURSED = register(
    new MineCellsStatusEffect(StatusEffectCategory.HARMFUL, 0x000000, false, MineCellsEffectFlags.CURSED, false),
    "cursed"
  );
  public static final StatusEffect ELECTRIFIED = register(new ElectrifiedStatusEffect(), "electrified");
  public static final StatusEffect PROTECTED = register(new ProtectedStatusEffect(), "protected");
  public static final StatusEffect BLEEDING = register(new BleedingStatusEffect(), "bleeding");
  public static final StatusEffect DISARMED = register(
    new MineCellsStatusEffect(StatusEffectCategory.HARMFUL, 0x000000, false, MineCellsEffectFlags.DISARMED, false),
    "disarmed"
  );
  public static final StatusEffect FROZEN = register(new FrozenStatusEffect(), "frozen");
  public static final StatusEffect STUNNED = register(new MineCellsStatusEffect(
    StatusEffectCategory.HARMFUL, 0xE8B724, false, MineCellsEffectFlags.STUNNED, false),
    "stunned"
  );
  public static final StatusEffect ASSASSINS_STRENGTH = register(
    new MineCellsStatusEffect(StatusEffectCategory.BENEFICIAL, 0xDA1C1C, false, null, false)
      .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, ASSASSINS_STRENGTH_UUID, 1.25F, Operation.MULTIPLY_TOTAL),
    "assassins_strength"
  );

  public static void init() { }

  public static StatusEffect register(StatusEffect effect, String name) {
    return Registry.register(Registry.STATUS_EFFECT, MineCells.createId(name), effect);
  }
}
