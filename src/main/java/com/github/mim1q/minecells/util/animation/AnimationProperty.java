package com.github.mim1q.minecells.util.animation;

import com.github.mim1q.minecells.util.MathUtils;
import net.minecraft.util.math.MathHelper;

public class AnimationProperty {
    private float value;
    private float lastValue;
    private float targetValue;
    private float lastTime;
    private float time;
    private float duration;
    private EasingFunction easingFunction;

    public AnimationProperty(float value, EasingType easingType) {
        this.value = value;
        this.lastValue = value;
        this.targetValue = value;
        this.easingFunction = EasingType.getFunction(easingType);
    }

    public AnimationProperty(float value) {
        this(value, EasingType.IN_OUT_QUAD);
    }

    public void setupTransitionTo(float targetValue, float duration, EasingType easingType) {
        setupTransitionTo(targetValue, duration, EasingType.getFunction(easingType));
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
        this.targetValue = targetValue;
        this.lastTime = this.time;
        this.duration = duration;
        return true;
    }

    public void update(float time) {
        this.time = time;
        this.value = this.easingFunction.ease(this.lastValue, this.targetValue, this.getProgress());
    }

    public float getProgress() {
        return MathHelper.clamp((this.time - this.lastTime) / this.duration, 0.0F, 1.0F);
    }

    public float getValue() {
        return this.value;
    }

    public interface EasingFunction {
        float ease(float start, float end, float delta);
    }

    public enum EasingType {
        LINEAR,
        IN_OUT_QUAD;

        public static EasingFunction getFunction(EasingType type) {
            return switch (type) {
                case LINEAR -> MathUtils::lerp;
                case IN_OUT_QUAD -> MathUtils::easeInOutQuad;
            };
        }
    }
}
