package com.github.mim1q.minecells.mixin.client.texture;

import com.github.mim1q.minecells.accessor.LivingEntityAccessor;
import com.github.mim1q.minecells.effect.MineCellsEffectFlags;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.SpriteContents;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(SpriteContents.AnimatorImpl.class)
public class SpriteContentsAnimatorImplMixin {
  @Shadow int frame;
  @Shadow @Final SpriteContents.Animation animation;
  @Shadow @Final @Nullable private SpriteContents.Interpolation interpolation;
  @Unique private Supplier<Boolean> minecells$condition = null;

  @Inject(
    method = "<init>",
    at = @At("RETURN")
  )
  private void minecells$injectInit(
    SpriteContents spriteContents,
    SpriteContents.Animation animation,
    SpriteContents.Interpolation interpolation,
    CallbackInfo ci
  ) {
    if (spriteContents.getId().toString().startsWith("minecells:block/awakenable/")) {
      minecells$condition = () -> {
        var player = MinecraftClient.getInstance().player;
        return player != null
          && ((LivingEntityAccessor) player).getMineCellsFlag(MineCellsEffectFlags.AWAKENED);
      };
    }
  }

  @WrapOperation(
    method = "tick",
    at = @At(
      value = "FIELD",
      target = "Lnet/minecraft/client/texture/SpriteContents$AnimatorImpl;frame:I",
      opcode = Opcodes.PUTFIELD
    )
  )
  private void minecells$wrapTick(SpriteContents.AnimatorImpl instance, int value, Operation<Void> original) {
    if (minecells$condition == null) {
      original.call(instance, value);
      return;
    }

    var halfSize = this.animation.frames.size() / 2;
    var modSize = this.interpolation == null ? halfSize : halfSize - 1;
    if (halfSize == 0) halfSize = 1;
    if (modSize <= 0) modSize = 1;

    if (minecells$condition.get()) {
      frame = (this.frame + 1) % modSize + halfSize;
    } else {
      frame = (this.frame + 1) % modSize;
    }
  }
}
