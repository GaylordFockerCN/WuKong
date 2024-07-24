package com.p1nero.wukong.epicfight.animation;

import com.mojang.math.Vector3f;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.event.CameraAnim;
import com.p1nero.wukong.epicfight.animation.custom.StaffFlowerAttackAnimation;
import com.p1nero.wukong.epicfight.animation.custom.WukongChargedAttackAnimation;
import com.p1nero.wukong.epicfight.skill.HeavyAttack;
import com.p1nero.wukong.epicfight.skill.StaffFlower;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import net.minecraft.util.Mth;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import reascer.wom.animation.attacks.BasicMultipleAttackAnimation;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongAnimations {

    public static StaticAnimation IDLE;
    public static StaticAnimation WALK;
    public static StaticAnimation RUN;
    //棍花
    public static StaticAnimation STAFF_FLOWER_ONE_HAND;
    public static StaticAnimation STAFF_FLOWER_TWO_HAND;

    //轻击 1~5
    public static StaticAnimation STAFF_AUTO1;
    public static StaticAnimation STAFF_AUTO2;
    public static StaticAnimation STAFF_AUTO3;
    public static StaticAnimation STAFF_AUTO4;
    public static StaticAnimation STAFF_AUTO5;
    public static StaticAnimation[] STATIC_ANIMATIONS;

    //劈棍
    public static StaticAnimation CHOP_IDLE;
    public static StaticAnimation CHOP_WALK;
    public static StaticAnimation CHOP_RUN;

    //衍生 1 2
    public static StaticAnimation CHOP_DERIVE1;
    public static StaticAnimation CHOP_DERIVE2;
    //不同星级的重击
    public static StaticAnimation CHOP_CHARGED0;
    public static StaticAnimation CHOP_CHARGED1;
    public static StaticAnimation CHOP_CHARGED2;
    public static StaticAnimation CHOP_CHARGED3;
    public static StaticAnimation CHOP_CHARGED4;

    //戳棍
    public static StaticAnimation POKE_IDLE;
    public static StaticAnimation POKE_WALK;
    public static StaticAnimation POKE_RUN;
    //衍生 1 2
    public static StaticAnimation POKE_DERIVE_PRE;
    public static StaticAnimation POKE_DERIVE1;
    public static StaticAnimation POKE_DERIVE1_BACKSWING;
    public static StaticAnimation POKE_DERIVE2;
    //不同星级的重击
    public static StaticAnimation POKE_PRE;
    public static StaticAnimation POKE_CHARGING;
    public static StaticAnimation POKE_CHARGED0;
    public static StaticAnimation POKE_CHARGED1;
    public static StaticAnimation POKE_CHARGED2;
    public static StaticAnimation POKE_CHARGED3;
    public static StaticAnimation POKE_CHARGED4;

    //立棍
    public static StaticAnimation STAND_IDLE;
    public static StaticAnimation STAND_WALK;
    public static StaticAnimation STAND_RUN;
    //衍生 1 2
    public static StaticAnimation STAND_DERIVE1;
    public static StaticAnimation STAND_DERIVE2;
    //不同星级的重击
    public static StaticAnimation STAND_CHARGED0;
    public static StaticAnimation STAND_CHARGED1;
    public static StaticAnimation STAND_CHARGED2;
    public static StaticAnimation STAND_CHARGED3;
    public static StaticAnimation STAND_CHARGED4;

    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(WukongMoveset.MOD_ID, WukongAnimations::build);
    }

    private static void build() {
        HumanoidArmature biped = Armatures.BIPED;

        RUN = new StaticAnimation(true, "biped/run",biped);

        STAFF_FLOWER_ONE_HAND = new StaffFlowerAttackAnimation(0.97F, biped, "biped/staff_flower/staff_flower_one_hand", 0.05F);
        STAFF_FLOWER_TWO_HAND = new StaffFlowerAttackAnimation(0.90F, biped, "biped/staff_flower/staff_flower_two_hand", 0.08F);

        //前摇完自动接下一个动作
        POKE_DERIVE_PRE = new BasicAttackAnimation(0.00F, 0.0167F, 0.3F, 0.33F, null, biped.toolR,  "biped/poke/poke_derive_pre", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(HeavyAttack.CAN_FIRST_DERIVE, false, serverPlayerPatch.getOriginal());
                            }
                }), AnimationEvent.Side.SERVER),
                AnimationEvent.TimeStampedEvent.create(0.38F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        //如果还按着按钮就接着戳
                        if(dataManager.getDataValue(HeavyAttack.KEY_PRESSING)){
                            serverPlayerPatch.reserveAnimation(POKE_DERIVE1);
                        } else {
                            serverPlayerPatch.reserveAnimation(POKE_DERIVE1_BACKSWING);
                        }
                    }
                }), AnimationEvent.Side.SERVER))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F));

        POKE_DERIVE1 = new BasicMultipleAttackAnimation(0.15F, "biped/poke/poke_derive1", biped,
                new AttackAnimation.Phase(0.0F, 0.00F, 0.25F, 1.0F, 0.26F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.24F, 0.25F, 0.50F, 1.0F, 0.51F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.49F, 0.50F, 0.75F, 1.0F, 0.76F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.74F, 0.74F, 1.0F, 1.0F, 1.0F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 2.5F))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.93F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                                //如果还按着按钮而且有耐力就接着衍生1
                                if(dataManager.getDataValue(HeavyAttack.KEY_PRESSING)){
                                    if(serverPlayerPatch.hasStamina(Config.DERIVE_STAMINA_CONSUME.get().floatValue())){
                                        serverPlayerPatch.consumeStamina(serverPlayerPatch.getOriginal().isCreative() ? 0 : Config.DERIVE_STAMINA_CONSUME.get().floatValue());
                                        serverPlayerPatch.reserveAnimation(POKE_DERIVE1);
                                    } else {
                                        //没耐力就后跳
                                        serverPlayerPatch.reserveAnimation(POKE_DERIVE1_BACKSWING);
                                    }
                                }
                                dataManager.setDataSync(HeavyAttack.IS_REPEATING_DERIVE, false, serverPlayerPatch.getOriginal());
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                                dataManager.setDataSync(HeavyAttack.CAN_FIRST_DERIVE, false, serverPlayerPatch.getOriginal());
                                dataManager.setDataSync(HeavyAttack.IS_REPEATING_DERIVE, true, serverPlayerPatch.getOriginal());
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.00F, ((livingEntityPatch, staticAnimation, objects) -> {
                            CameraAnim.zoomIn();
                        }), AnimationEvent.Side.CLIENT));

        POKE_DERIVE1_BACKSWING = new ActionAnimation(0.15F, "biped/poke/poke_derive_backswing", biped)
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)//解除锁视角的关键！
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.8F, (livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch playerPatch){
                        //设为可二段衍生状态
                        SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        dataManager.setDataSync(HeavyAttack.DERIVE_TIMER, HeavyAttack.MAX_TIMER, playerPatch.getOriginal());
                        dataManager.setDataSync(HeavyAttack.CAN_SECOND_DERIVE, true, playerPatch.getOriginal());
                    }
                }, AnimationEvent.Side.SERVER));

        POKE_DERIVE2 = new BasicAttackAnimation(0.15F, 1.1667F, 1.55F, 1.55F, WukongColliders.POKE_3, biped.toolR, "biped/poke/poke_derive2", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(HeavyAttack.CAN_SECOND_DERIVE, false, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER),
                AnimationEvent.TimeStampedEvent.create(0.00F, ((livingEntityPatch, staticAnimation, objects) -> {
                    CameraAnim.zoomIn(50);
                }), AnimationEvent.Side.CLIENT))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)//为了用自己的视角...
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F));

        POKE_CHARGED0 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.POKE_0, biped.toolR, "biped/poke/poke_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F* Config.DAMAGE_MULTIPLIER.get().floatValue()))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.0F));
        POKE_CHARGED1 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.POKE_1, biped.toolR, "biped/poke/poke_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.7F* Config.DAMAGE_MULTIPLIER.get().floatValue()))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.0F));
        POKE_CHARGED2 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.POKE_2, biped.toolR, "biped/poke/poke_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F* Config.DAMAGE_MULTIPLIER.get().floatValue()))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(3.0F));
        POKE_CHARGED3 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.POKE_3, biped.toolR, "biped/poke/poke_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.5F* Config.DAMAGE_MULTIPLIER.get().floatValue()))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F));
        POKE_CHARGED4 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.POKE_4, biped.toolR, "biped/poke/poke_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.0F * Config.DAMAGE_MULTIPLIER.get().floatValue()))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(7.0F));

        POKE_CHARGING = (new ActionAnimation(0.5F, "biped/poke/poke_charging", biped))
                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1))
                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, (self, pose, entityPatch, time, partialTicks) -> {
                    if (self.isStaticAnimation()) {
                        float xRot = Mth.clamp(entityPatch.getCameraXRot(), -60.0F, 50.0F);
                        float yRot = Mth.clamp(Mth.wrapDegrees(entityPatch.getCameraYRot() - entityPatch.getOriginal().getYRot()), -60.0F, 60.0F);
                        JointTransform chest = pose.getOrDefaultTransform("Chest");
                        chest.frontResult(JointTransform.getRotation(Vector3f.YP.rotationDegrees(yRot)), OpenMatrix4f::mulAsOriginFront);
                        JointTransform head = pose.getOrDefaultTransform("Head");
                        MathUtils.mulQuaternion(Vector3f.XP.rotationDegrees(xRot), head.rotation(), head.rotation());
                    }
                })
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.97F, (livingEntityPatch, staticAnimation, objects) -> {
                        if(livingEntityPatch instanceof ServerPlayerPatch playerPatch){
                            if(playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(HeavyAttack.KEY_PRESSING)){
                                livingEntityPatch.reserveAnimation(POKE_CHARGING);
                            }
                        }
                    }, AnimationEvent.Side.SERVER))
                .newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.INACTION, false)
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false);

        POKE_PRE = new BasicMultipleAttackAnimation(0, "biped/poke/poke_pre", biped,
        new AttackAnimation.Phase(0.0F, 0.05F, 0.15F, 0.75F, 0.25F , biped.toolR, null)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.1F)),
        new AttackAnimation.Phase(0.2F, 0.2F, 0.35F, 0.75F, 0.75F , biped.toolR, null)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.1F)))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.39F, (livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(POKE_CHARGING);
                    if(livingEntityPatch instanceof ServerPlayerPatch playerPatch){
                        playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(HeavyAttack.IS_CHARGING, true, playerPatch.getOriginal());
                    }
                }, AnimationEvent.Side.SERVER));

//        POKE_DERIVE1 = new SelectiveAnimation((entityPatch -> {
//            if(entityPatch instanceof ServerPlayerPatch playerPatch){
//                if(playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(HeavyAttack.STARTS_CONSUMED) > 0){
//                    return 1;
//                }
//            }
//            return 0;
//        }), POKE_DERIVE1_COMMON, POKE_DERIVE1_PLUS);
//        STAFF_FLOWER = new GuardAnimation()
    }

}
