package com.p1nero.wukong.epicfight.animation.custom;

import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;

/**
 * 无敌时间缩短到后摇结束
 */
public class WukongDodgeAnimation extends DodgeAnimation {
    public WukongDodgeAnimation(float convertTime, String path, float width, float height, Armature armature) {
        super(convertTime, path, width, height, armature);
    }

    public WukongDodgeAnimation(float convertTime, float delayTime, String path, float width, float height, Armature armature) {
        super(convertTime, delayTime, path, width, height, armature);
        this.stateSpectrumBlueprint.clear()
                .newTimePair(0.0F, delayTime)
                    .addState(EntityState.TURNING_LOCKED, true)
                    .addState(EntityState.MOVEMENT_LOCKED, true)
                    .addState(EntityState.UPDATE_LIVING_MOTION, false)
                    .addState(EntityState.CAN_BASIC_ATTACK, false)
                    .addState(EntityState.CAN_SKILL_EXECUTION, false)
                    .addState(EntityState.INACTION, true)
                .newTimePair(0.0F, delayTime)//区别就在这里，把闪避时间缩短到后摇时间
                    .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR);
    }

}
