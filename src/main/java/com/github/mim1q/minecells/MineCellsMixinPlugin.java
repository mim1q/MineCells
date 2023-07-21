package com.github.mim1q.minecells;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class MineCellsMixinPlugin implements IMixinConfigPlugin {
  private static Supplier<Boolean> disableWhenModLoaded(String id) {
    return () -> !FabricLoader.getInstance().isModLoaded(id);
  }

  private static Map.Entry<String, Supplier<Boolean>> mixin(String name, Supplier<Boolean> condition) {
    return Map.entry("com.github.mim1q.minecells.mixin." + name, condition);
  }

  private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.ofEntries(
    mixin("entity.BorderEntityCollisionMixin", disableWhenModLoaded("lithium"))
  );

  @Override
  public void onLoad(String mixinPackage) {

  }

  @Override
  public String getRefMapperConfig() {
    return null;
  }

  @Override
  public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
    return CONDITIONS.getOrDefault(mixinClassName, () -> true).get();
  }

  @Override
  public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

  }

  @Override
  public List<String> getMixins() {
    return null;
  }

  @Override
  public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

  }

  @Override
  public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

  }
}
