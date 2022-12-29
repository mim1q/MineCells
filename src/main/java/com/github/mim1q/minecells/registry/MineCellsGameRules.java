package com.github.mim1q.minecells.registry;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class MineCellsGameRules {
  public static final GameRules.Key<GameRules.BooleanRule> MOBS_DROP_CELLS = GameRuleRegistry.register(
    "minecells.mobsDropCells",
    GameRules.Category.MOBS,
    GameRuleFactory.createBooleanRule(false)
  );

  public static final GameRules.Key<GameRules.BooleanRule> SUFFOCATION_FIX = GameRuleRegistry.register(
    "minecells.suffocationFix",
    GameRules.Category.MISC,
    GameRuleFactory.createBooleanRule(true)
  );

  public static void init() { }
}
