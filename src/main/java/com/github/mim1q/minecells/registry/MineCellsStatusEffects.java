package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.effect.*;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;


public class MineCellsStatusEffects {
  private static final Identifier ASSASSINS_STRENGTH_UUID = MineCells.createId("assassins_strength");

  public static final RegistryEntry<StatusEffect> CURSED = register(
    new MineCellsStatusEffect(StatusEffectCategory.HARMFUL, 0x000000, false, MineCellsEffectFlags.CURSED, false),
    "cursed"
  );
  public static final RegistryEntry<StatusEffect> ELECTRIFIED = register(new ElectrifiedStatusEffect(), "electrified");
  public static final RegistryEntry<StatusEffect> PROTECTED = register(new ProtectedStatusEffect(), "protected");
  public static final RegistryEntry<StatusEffect> BLEEDING = register(new BleedingStatusEffect(), "bleeding");
  public static final RegistryEntry<StatusEffect> DISARMED = register(
    new MineCellsStatusEffect(StatusEffectCategory.HARMFUL, 0x000000, false, MineCellsEffectFlags.DISARMED, false),
    "disarmed"
  );
  public static final RegistryEntry<StatusEffect> FROZEN = register(new FrozenStatusEffect(MineCellsEffectFlags.FROZEN, 0x96f0ff, true), "frozen");
  public static final RegistryEntry<StatusEffect> STUNNED = register(new FrozenStatusEffect(MineCellsEffectFlags.STUNNED, 0xf1f8b5, false), "stunned");
  public static final RegistryEntry<StatusEffect> ASSASSINS_STRENGTH = register(
    new MineCellsStatusEffect(StatusEffectCategory.BENEFICIAL, 0xDA1C1C, false, null, false)
      .addAttributeModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, ASSASSINS_STRENGTH_UUID, 1.25F, Operation.ADD_MULTIPLIED_TOTAL),
    "assassins_strength"
  );

  public static void init() {
  }

  public static RegistryEntry<StatusEffect> register(StatusEffect effect, String name) {
    var e = Registry.register(Registries.STATUS_EFFECT, MineCells.createId(name), effect);
    return Registries.STATUS_EFFECT.getEntry(e);

  }
}
