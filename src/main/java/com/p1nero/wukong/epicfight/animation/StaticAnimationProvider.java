package com.p1nero.wukong.epicfight.animation;

import yesman.epicfight.api.animation.types.StaticAnimation;

/**
 * This interface is for array use
 */
@FunctionalInterface
public interface StaticAnimationProvider extends AnimationProvider<StaticAnimation> {
    public StaticAnimation get();
}