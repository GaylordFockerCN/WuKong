package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 攻击到则后跳
 */
public class WukongJumpAttackAnimation extends BasicAttackAnimation {


    public WukongJumpAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongJumpAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongJumpAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, hand, collider, colliderJoint, path, armature);
    }

    public WukongJumpAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
    }

    protected void attackTick(LivingEntityPatch<?> entityPatch) {
        AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(this);
        float elapsedTime = player.getElapsedTime();
        float prevElapsedTime = player.getPrevElapsedTime();
        EntityState state = this.getState(entityPatch, elapsedTime);
        EntityState prevState = this.getState(entityPatch, prevElapsedTime);
        Phase phase = this.getPhaseByTime(elapsedTime);
        if (state.getLevel() == 1 && !state.turningLocked() && entityPatch instanceof MobPatch<?> mobpatch) {
            mobpatch.getOriginal().getNavigation().stop();
            entityPatch.getOriginal().attackAnim = 2.0F;
            LivingEntity target = entityPatch.getTarget();
            if (target != null) {
                entityPatch.rotateTo(target, entityPatch.getYRotLimit(), false);
            }
        }

        if (prevState.attacking() || state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2) {
            if (!prevState.attacking() || phase != this.getPhaseByTime(prevElapsedTime) && (state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2)) {
                entityPatch.playSound(this.getSwingSound(entityPatch, phase), 0.0F, 0.0F);
                entityPatch.removeHurtEntities();
            }

            this.hurtCollidingEntities(entityPatch, prevElapsedTime, elapsedTime, prevState, state, phase);
            if(!entityPatch.getCurrenltyAttackedEntities().isEmpty()){
                entityPatch.playAnimationSynchronized(WukongAnimations.JUMP_ATTACK_LIGHT_HIT, 0.15F);
            }
        }

    }

}
