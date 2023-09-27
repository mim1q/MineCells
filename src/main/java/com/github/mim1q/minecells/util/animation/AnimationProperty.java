package com.github.mim1q.minecells.util.animation;

import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.util.math.MathHelper;

public class AnimationProperty {
  private float value;
  private float lastValue;
  private float targetValue;
  private float lastTime = 0.0F;
  private float time = 0.0F;
  private float duration = 10.0F;
  private EasingFunction easingFunction;

  public AnimationProperty(float value, EasingFunction easingFunction) {
    this.value = value;
    this.lastValue = value;
    this.targetValue = value;
    this.easingFunction = easingFunction;
  }

  public AnimationProperty(float value) {
    this(value, MathUtils::easeInOutQuad);
  }

  public void setupTransitionTo(float targetValue, float duration, EasingFunction easingFunction) {
    boolean result = this.setupTransitionTo(targetValue, duration);
    if (result) {
      this.easingFunction = easingFunction;
    }
  }

  public boolean setupTransitionTo(float targetValue, float duration) {
    if (targetValue == this.targetValue) {
      return false;
    }
    this.lastValue = this.value;
    this.lastTime = this.time;
    this.duration = duration;
    this.targetValue = targetValue;
    return true;
  }

  public float update(float time) {
    this.time = time;
    this.value = this.easingFunction.ease(this.lastValue, this.targetValue, this.getProgress());
    return getValue();
  }

  public float getProgress() {
    return MathHelper.clamp((this.time - this.lastTime) / this.duration, 0.0F, 1.0F);
  }

  public float getValue() {
    return this.value;
  }

  @FunctionalInterface
  public interface EasingFunction {
    float ease(float start, float end, float delta);
  }
}
