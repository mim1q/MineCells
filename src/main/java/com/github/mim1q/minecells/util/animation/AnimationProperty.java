package com.github.mim1q.minecells.util.animation;

import com.github.mim1q.minecells.util.MineCellsMathHelper;
import net.minecraft.util.math.MathHelper;

public class AnimationProperty {
    private float value;
    private float lastValue;
    private float targetValue;

    private float lastTime;
    private float time;

    private float duration;

    public AnimationProperty(float value) {
        this.value = value;
    }

    public void setupTransitionTo(float targetValue, float duration) {
        if (targetValue == this.targetValue) {
            return;
        }
        this.lastValue = this.value;
        this.targetValue = targetValue;
        this.lastTime = this.time;
        this.duration = duration;
    }

    public void update(float time) {
        this.time = time;
        this.value = MineCellsMathHelper.easeInOutQuad(this.lastValue, this.targetValue, this.getProgress());
    }

    public float getProgress() {
        return MathHelper.clamp((this.time - this.lastTime) / this.duration, 0.0F, 1.0F);
    }

    public float getValue() {
        return this.value;
    }
}
