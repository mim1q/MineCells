package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.effect.ElectrifiedStatusEffect;
import com.github.mim1q.minecells.effect.ProtectedStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.registry.Registry;


public class StatusEffectRegistry {

  public static final StatusEffect ELECTRIFIED = new ElectrifiedStatusEffect();
  public static final StatusEffect PROTECTED = new ProtectedStatusEffect();

  public static void register() {
    Registry.register(Registry.STATUS_EFFECT, MineCells.createId("electrified"), ELECTRIFIED);
    Registry.register(Registry.STATUS_EFFECT, MineCells.createId("protected"), PROTECTED);
  }
}
