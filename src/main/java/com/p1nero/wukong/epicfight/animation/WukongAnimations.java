package com.p1nero.wukong.epicfight.animation;

import com.mojang.math.Vector3f;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.animation.custom.StaffFlowerAttackAnimation;
import com.p1nero.wukong.epicfight.animation.custom.WukongChargedAttackAnimation;
import com.p1nero.wukong.epicfight.skill.StaffFlower;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
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
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
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
    public static StaticAnimation POKE_DERIVE1;
    public static StaticAnimation POKE_DERIVE1_COMMON;
    public static StaticAnimation POKE_DERIVE1_PLUS;
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

        STAFF_FLOWER_ONE_HAND = new StaffFlowerAttackAnimation(0, "biped/staff_flower/staff_flower_one_hand", biped,
                new AttackAnimation.Phase(0.0F, 0.00F, 0.25F, 1.0F, 0.26F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.24F, 0.25F, 0.50F, 1.0F, 0.51F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.49F, 0.50F, 0.75F, 1.0F, 0.76F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.74F, 0.74F, 1.0F, 1.0F, 1.2F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addEvents(
                    AnimationEvent.TimeStampedEvent.create(0.97F, ((livingEntityPatch, staticAnimation, objects) -> {
                        if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                            SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
                            passiveContainer.getDataManager().setDataSync(StaffFlower.PLAYING_STAFF_FLOWER, false,serverPlayerPatch.getOriginal());
                            if(passiveContainer.getDataManager().getDataValue(StaffFlower.KEY_PRESSING)){
                                if(serverPlayerPatch.hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue())){
                                    serverPlayerPatch.consumeStamina(serverPlayerPatch.getOriginal().isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
                                    serverPlayerPatch.reserveAnimation(STAFF_FLOWER_ONE_HAND);
                                }
                            }
                        }
                    }), AnimationEvent.Side.SERVER),
                    AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                        if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                            SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
                            passiveContainer.getDataManager().setDataSync(StaffFlower.PLAYING_STAFF_FLOWER, true, serverPlayerPatch.getOriginal());
                        }
                    }), AnimationEvent.Side.SERVER));

        STAFF_FLOWER_TWO_HAND = new StaffFlowerAttackAnimation(0, "biped/staff_flower/staff_flower_two_hand", biped,
                new AttackAnimation.Phase(0.0F, 0.00F, 0.25F, 1.0F, 0.26F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.24F, 0.25F, 0.50F, 1.0F, 0.51F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.49F, 0.50F, 0.75F, 1.0F, 0.76F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.74F, 0.74F, 1.0F, 1.0F, 1.2F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.90F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
                                passiveContainer.getDataManager().setDataSync(StaffFlower.PLAYING_STAFF_FLOWER, false,serverPlayerPatch.getOriginal());
                                if(passiveContainer.getDataManager().getDataValue(StaffFlower.KEY_PRESSING)){
                                    if(serverPlayerPatch.hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue())){
                                        serverPlayerPatch.consumeStamina(serverPlayerPatch.getOriginal().isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
                                        serverPlayerPatch.reserveAnimation(STAFF_FLOWER_TWO_HAND);
                                    }
                                }
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
                                passiveContainer.getDataManager().setDataSync(StaffFlower.PLAYING_STAFF_FLOWER, true, serverPlayerPatch.getOriginal());
                            }
                        }), AnimationEvent.Side.SERVER));

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

        //为了不移动所以改用BasicAttackAnimation
        POKE_CHARGING = (new ActionAnimation(1.0F, "biped/poke/poke_charging", biped))
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
        }).newTimePair(0.0F, Float.MAX_VALUE)
                .addStateRemoveOld(EntityState.INACTION, false)
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false);

        //为了被监听就加了伤害了qwq
        POKE_PRE = new BasicMultipleAttackAnimation(0, "biped/poke/poke_pre", biped,
        new AttackAnimation.Phase(0.0F, 0.05F, 0.15F, 0.75F, 0.25F , biped.toolR, null)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F)),
        new AttackAnimation.Phase(0.2F, 0.2F, 0.35F, 0.75F, 0.75F , biped.toolR, null)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.2F)))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false);

        //TODO delete after test
        POKE_DERIVE1 = (new ActionAnimation(1.0F, "biped/poke/poke_charging", biped));
        POKE_DERIVE2 = (new ActionAnimation(1.0F, "biped/poke/poke_charging", biped));

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
