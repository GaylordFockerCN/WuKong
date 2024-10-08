package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 无敌时间缩短到后摇结束
 */
public class WukongDodgeAnimation extends DodgeAnimation {

    public WukongDodgeAnimation(float convertTime, float delayTime, String path, float width, float height, Armature armature, boolean isPerfect) {
        super(convertTime, delayTime, path, width, height, armature);
        this.addEvents(AnimationEvent.TimeStampedEvent.create(delayTime, ((livingEntityPatch, staticAnimation, objects) -> {
            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch && WukongWeaponCategories.isWeaponValid(livingEntityPatch)) {
                serverPlayerPatch.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                    SkillContainer weaponInnate = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                    if (weaponInnate.getDataManager().getDataValue(WukongSkillDataKeys.IS_CHARGING.get()) && !wkPlayer.isPerfectDodge() && !isPerfect) {
                        weaponInnate.getSkill().setConsumptionSynchronize(serverPlayerPatch, 1);
                        weaponInnate.getSkill().setStackSynchronize(serverPlayerPatch, 0);
                    }
                    weaponInnate.getDataManager().setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), false, serverPlayerPatch.getOriginal());//无论如何都要中断蓄力
                });
            }
        }), AnimationEvent.Side.SERVER));
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

    public WukongDodgeAnimation(float convertTime, float delayTime, String path, float width, float height, Armature armature) {
        this(convertTime, delayTime, path, width, height, armature, false);
    }

    /**
     * 触发完美闪避才改状态
     */
    @Override
    public void begin(LivingEntityPatch<?> entityPatch) {
        super.begin(entityPatch);
        if(entityPatch instanceof ServerPlayerPatch playerPatch){
            playerPatch.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> wkPlayer.setPerfectDodge(false));
        }
    }

}
