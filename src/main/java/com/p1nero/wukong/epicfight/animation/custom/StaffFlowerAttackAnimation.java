package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.client.event.CameraAnim;
import com.p1nero.wukong.epicfight.skill.custom.StaffSpin;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 尝试修改动画播放的move lock
 * 后面直接监听输入事件取消input了。。
 */
public class StaffFlowerAttackAnimation extends BasicMultipleAttackAnimation {

    public StaffFlowerAttackAnimation(float end, HumanoidArmature biped, String path, float damageMultiplier, boolean isTwoHand){
        super(0, path, biped,
                        new AttackAnimation.Phase(0.0F, 0.00F, 0.25F, end, 0.26F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.24F, 0.25F, 0.50F, end, 0.51F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.49F, 0.50F, 0.75F, end, 0.76F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.74F, 0.74F, 1.0F, end, end , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)));
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(end - 0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                if(!WukongWeaponCategories.isWeaponValid(serverPlayerPatch)){
                                    return;
                                }
                                SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
                                passiveContainer.getDataManager().setDataSync(StaffSpin.PLAYING_STAFF_SPIN, false, serverPlayerPatch.getOriginal());
                                if(passiveContainer.getDataManager().getDataValue(StaffSpin.KEY_PRESSING)){
                                    if(serverPlayerPatch.hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue())){
                                        serverPlayerPatch.consumeStamina(serverPlayerPatch.getOriginal().isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
                                        serverPlayerPatch.reserveAnimation(staticAnimation);
                                    }
                                }
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                if(!WukongWeaponCategories.isWeaponValid(serverPlayerPatch)){
                                    return;
                                }
                                SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
                                passiveContainer.getDataManager().setDataSync(StaffSpin.PLAYING_STAFF_SPIN, true, serverPlayerPatch.getOriginal());
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(isTwoHand){
                                CameraAnim.zoomIn(new Vec3f(-1.0F, 0.0F, 1.25F), 20);
                            }
                        }), AnimationEvent.Side.CLIENT));

    }

    @Override
    public void end(LivingEntityPatch<?> entityPatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        if(entityPatch.isLogicalClient() && CameraAnim.isAiming()){
            CameraAnim.zoomOut(20);//保险
        }
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
