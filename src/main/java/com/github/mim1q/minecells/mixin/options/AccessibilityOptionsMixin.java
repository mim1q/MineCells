package com.github.mim1q.minecells.mixin.options;

import com.github.mim1q.minecells.accessor.GameOptionsAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsMixin {

  @ModifyReturnValue(
    method = "getOptions",
    at = @At("RETURN")
  )
  private static SimpleOption<?>[] modifyOptions(SimpleOption<?>[] original, GameOptions options) {
    var newArray = new SimpleOption<?>[original.length + 1];
    System.arraycopy(original, 0, newArray, 0, original.length);
    newArray[original.length] = ((GameOptionsAccessor)options).minecells$getMinecellsScreenshake();
    return newArray;
  }
}
