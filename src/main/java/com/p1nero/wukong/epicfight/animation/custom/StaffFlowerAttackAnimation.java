package com.p1nero.wukong.epicfight.animation.custom;

import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;

/**
 * 尝试修改动画播放的move lock
 */
public class StaffFlowerAttackAnimation extends BasicMultipleAttackAnimation {
    public StaffFlowerAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
    }

    public StaffFlowerAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    public StaffFlowerAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, hand, collider, colliderJoint, path, armature);
    }

    public StaffFlowerAttackAnimation(float convertTime, String path, Armature armature, boolean Coordsetter, Phase... phases) {
        super(convertTime, path, armature, Coordsetter, phases);
    }

    public StaffFlowerAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
    }

    protected void bindPhaseState(Phase phase) {
        float preDelay = phase.preDelay;

        if (preDelay == 0.0F) {
            preDelay += 0.01F;
        }

        this.stateSpectrumBlueprint
                .newTimePair(phase.start, preDelay)
                .addState(EntityState.PHASE_LEVEL, 1)
                .newTimePair(phase.start, phase.contact + 0.01F)
                .addState(EntityState.CAN_SKILL_EXECUTION, false)
                .newTimePair(phase.start , phase.recovery)
                .addState(EntityState.UPDATE_LIVING_MOTION, false)
                .addState(EntityState.CAN_BASIC_ATTACK, false);
        if(phase.equals(phases[phases.length-1])){
            this.stateSpectrumBlueprint.newTimePair(0, phase.end)
                    .addState(EntityState.MOVEMENT_LOCKED, true);
        }
        this.stateSpectrumBlueprint.newTimePair(phase.start, phase.end)
                .addState(EntityState.INACTION, true)
                .newTimePair(phase.antic, phase.end)
                .addState(EntityState.TURNING_LOCKED, true)
                .newTimePair(preDelay, phase.contact + 0.01F)
                .addState(EntityState.ATTACKING, true)
                .addState(EntityState.PHASE_LEVEL, 2)
                .newTimePair(phase.contact + 0.01F, phase.end)
                .addState(EntityState.PHASE_LEVEL, 3);

    }

}
