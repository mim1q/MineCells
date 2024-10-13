package com.github.mim1q.minecells.mixin.client.music;

import com.github.mim1q.minecells.MineCellsClient;
import com.github.mim1q.minecells.dimension.MineCellsDimension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin {
  @Shadow @Final private MinecraftClient client;

  @Shadow public abstract void stop();

  @Shadow private @Nullable SoundInstance current;

  @Unique
  private MineCellsDimension lastDimension = null;

  @Inject(
    method = "tick",
    at = @At("HEAD"),
    cancellable = true
  )
  private void minecells$injectOnTick(CallbackInfo ci) {
    var world = this.client.world;
    if (world == null || !MineCellsClient.CLIENT_CONFIG.experimentalMusicLooping()) {
      this.lastDimension = null;
      return;
    }

    var dimension = MineCellsDimension.of(world);
    var dimensionChanged = lastDimension != dimension;
    var playing = current != null && this.client.getSoundManager().isPlaying(current);
    if (dimension != null && !dimension.canMusicStart(this.client.player)) return;
    lastDimension = dimension;
    if (dimension == null) return;

    var loopMusic = dimension.getMusic();
    if (loopMusic == null) return;

    if (dimensionChanged || !playing) {
      this.stop();
      this.minecells$playMusic(loopMusic);
    }

    ci.cancel();
  }

  @Unique
  private void minecells$playMusic(MusicSound loop) {
    this.current = new PositionedSoundInstance(
      loop.getSound().value().getId(),
      SoundCategory.MUSIC, 1.0F, 1.0F,
      SoundInstance.createRandom(),
      true, 0,
      SoundInstance.AttenuationType.LINEAR,
      0.0, 0.0, 0.0, false
    );
    if (current.getSound() != SoundManager.MISSING_SOUND) {
      this.client.getSoundManager().play(current);
    }
  }
}
