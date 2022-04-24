package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.effect.ElectrifiedStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class StatusEffectRegistry {

    public static final StatusEffect ELECTRIFIED = new ElectrifiedStatusEffect();

    public static void register() {
        Registry.register(Registry.STATUS_EFFECT, new Identifier(MineCells.MOD_ID, "electrified"), ELECTRIFIED);
    }
}
