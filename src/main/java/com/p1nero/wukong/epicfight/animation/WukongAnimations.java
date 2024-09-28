package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.event.CameraAnim;
import com.p1nero.wukong.epicfight.animation.custom.BasicMultipleAttackAnimation;
import com.p1nero.wukong.epicfight.animation.custom.StaffFlowerAttackAnimation;
import com.p1nero.wukong.epicfight.animation.custom.WukongChargedAttackAnimation;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongAnimations {

    public static StaticAnimation IDLE;
    public static StaticAnimation WALK;
    public static StaticAnimation RUN;
    public static StaticAnimation DASH;
    public static StaticAnimation JUMP;
    public static StaticAnimation FALL;
    public static StaticAnimation JUMP_ATTACK_LIGHT;
    public static StaticAnimation JUMP_ATTACK_LIGHT_HIT;
    public static StaticAnimation JUMP_ATTACK_LIGHT_FALL;
    public static StaticAnimation JUMP_ATTACK_HEAVY_START;
    public static StaticAnimation JUMP_ATTACK_HEAVY_END;
    public static StaticAnimation DODGE_F1;
    public static StaticAnimation DODGE_F2;
    public static StaticAnimation DODGE_F3;
    public static StaticAnimation DODGE_F;
    public static StaticAnimation DODGE_B1;
    public static StaticAnimation DODGE_B2;
    public static StaticAnimation DODGE_B3;
    public static StaticAnimation DODGE_B;
    public static StaticAnimation DODGE_L1;
    public static StaticAnimation DODGE_L2;
    public static StaticAnimation DODGE_L3;
    public static StaticAnimation DODGE_L;
    public static StaticAnimation DODGE_R1;
    public static StaticAnimation DODGE_R2;
    public static StaticAnimation DODGE_R3;
    public static StaticAnimation DODGE_R;
    //棍花
    public static StaticAnimation STAFF_FLOWER_ONE_HAND_PRE;
    public static StaticAnimation STAFF_FLOWER_ONE_HAND_LOOP;
    public static StaticAnimation STAFF_FLOWER_ONE_HAND_END;
    public static StaticAnimation STAFF_FLOWER_ONE_HAND_TO_TWO_HAND;
    public static StaticAnimation STAFF_FLOWER_TWO_HAND_PRE;
    public static StaticAnimation STAFF_FLOWER_TWO_HAND_LOOP;
    public static StaticAnimation STAFF_FLOWER_TWO_HAND_END;
    public static StaticAnimation STAFF_FLOWER_TWO_HAND_TO_ONE_HAND;

    //轻击 1~5
    public static StaticAnimation STAFF_AUTO1_DASH;
    public static StaticAnimation STAFF_AUTO1;
    public static StaticAnimation STAFF_AUTO2;
    public static StaticAnimation STAFF_AUTO3;
    public static StaticAnimation STAFF_AUTO4;
    public static StaticAnimation STAFF_AUTO5;

    //劈棍
    //衍生 1 2
    public static StaticAnimation SMASH_DERIVE1;
    public static StaticAnimation SMASH_DERIVE2;
    public static StaticAnimation SMASH_CHARGING_PRE;
    public static StaticAnimation SMASH_CHARGING_LOOP;
    //不同星级的重击
    public static StaticAnimation SMASH_CHARGED0;
    public static StaticAnimation SMASH_CHARGED1;
    public static StaticAnimation SMASH_CHARGED2;
    public static StaticAnimation SMASH_CHARGED3;
    public static StaticAnimation SMASH_CHARGED4;

    //戳棍
    //衍生 1 2
    public static StaticAnimation THRUST_DERIVE_PRE;
    public static StaticAnimation THRUST_DERIVE1;
    public static StaticAnimation THRUST_DERIVE1_BACKSWING;
    public static StaticAnimation THRUST_DERIVE2;
    //不同星级的重击
    public static StaticAnimation THRUST_PRE;
    public static StaticAnimation THRUST_CHARGING;
    public static StaticAnimation THRUST_CHARGED0;
    public static StaticAnimation THRUST_CHARGED1;
    public static StaticAnimation THRUST_CHARGED2;
    public static StaticAnimation THRUST_CHARGED3;
    public static StaticAnimation THRUST_CHARGED4;

    //立棍
    //衍生 1 2
    public static StaticAnimation PILLAR_DERIVE1;
    public static StaticAnimation PILLAR_DERIVE2;
    //不同星级的重击
    public static StaticAnimation PILLAR_CHARGED0;
    public static StaticAnimation PILLAR_CHARGED1;
    public static StaticAnimation PILLAR_CHARGED2;
    public static StaticAnimation PILLAR_CHARGED3;
    public static StaticAnimation PILLAR_CHARGED4;

    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(WukongMoveset.MOD_ID, WukongAnimations::build);
    }

    private static void build() {
        HumanoidArmature biped = Armatures.BIPED;

        //专治各种因为移动导致的动画取消
        AnimationEvent.TimePeriodEvent allStopMovement = AnimationEvent.TimePeriodEvent.create(0.00F, Float.MAX_VALUE, ((livingEntityPatch, staticAnimation, objects) -> {
            if(livingEntityPatch instanceof LocalPlayerPatch localPlayerPatch){
                Input input = localPlayerPatch.getOriginal().input;
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                LocalPlayer clientPlayer = localPlayerPatch.getOriginal();
                clientPlayer.setSprinting(false);
            }
        }), AnimationEvent.Side.CLIENT);

        IDLE = new StaticAnimation(true, "biped/idle",biped);
        WALK = new StaticAnimation(true, "biped/walk",biped)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.3F));
        RUN = new StaticAnimation(true, "biped/run",biped);
        JUMP = new StaticAnimation(0.15F, false, "biped/jump",biped);
        FALL = new StaticAnimation(0.15F, true, "biped/fall",biped);

        STAFF_AUTO1_DASH = new BasicAttackAnimation(0.1F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR,  "biped/auto_1_dash", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        //冲刺攻击重置普攻计数器
                        BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.BASIC_ATTACK_COUNT, serverPlayerPatch, serverPlayerPatch.getSkill(SkillSlots.BASIC_ATTACK), staticAnimation, 1);
                    }
                }), AnimationEvent.Side.SERVER));
        STAFF_AUTO1 = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR,  "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        STAFF_AUTO2 = new BasicAttackAnimation(0.15F, 0.6667F, 0.875F, 0.875F, null, biped.toolR,  "biped/auto_2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        STAFF_AUTO3 = new BasicMultipleAttackAnimation(0.15F, "biped/auto_3", biped,
                new AttackAnimation.Phase(0.0F, 0.25F, 0.4583F, 0.4583F, 0.4583F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F)),
                new AttackAnimation.Phase(0.4583F, 0.4583F, 0.7083F, 0.7083F, 3.3333F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F)))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        STAFF_AUTO4 = new BasicMultipleAttackAnimation(0.15F, "biped/auto_4", biped,
                new AttackAnimation.Phase(0.0F, 0.1F, 0.2F, 0.2F, 0.2F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)),
                new AttackAnimation.Phase(0.2F, 0.2F, 0.4F, 0.4F, 0.4F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)),
                new AttackAnimation.Phase(0.4F, 0.4F, 0.6F, 0.6F, 0.6F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)),
                new AttackAnimation.Phase(0.6F, 0.6F, 0.8F, 0.8F, 0.8F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F)),
                new AttackAnimation.Phase(0.8F, 1.0416F, 1.125F, 1.2583F, 2.5F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5F)))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
//                        .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, )
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        STAFF_AUTO5 = new BasicAttackAnimation(0.01F, 0.9166F,1.25F, 1.9833F, null, biped.toolR,  "biped/auto_5", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.9833F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));

        STAFF_FLOWER_ONE_HAND_LOOP = new StaffFlowerAttackAnimation(0.97F, biped, "biped/staff_flower/staff_flower_one_hand", 0.05F);
        STAFF_FLOWER_TWO_HAND_LOOP = new StaffFlowerAttackAnimation(0.90F, biped, "biped/staff_flower/staff_flower_two_hand", 0.08F);

        //前摇完自动接下一个动作
        SMASH_CHARGING_PRE = new StaticAnimation(0.15F, false, "biped/smash/smash_charge_pre", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(SMASH_CHARGING_LOOP);
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER));
        SMASH_CHARGING_LOOP = new StaticAnimation(0.15F, true, "biped/smash/smash_charging", biped);

        SMASH_CHARGED0 = new BasicAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, null, biped.toolR,  "biped/smash/smash_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.67F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(allStopMovement);
        SMASH_CHARGED1 = new BasicAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, null, biped.toolR,  "biped/smash/smash_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.67F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(allStopMovement);
        SMASH_CHARGED2 = new BasicAttackAnimation(0.15F, 0.75F, 0.92F, 5.0F, null, biped.toolR,  "biped/smash/smash_heavy2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(8.8F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.67F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
//        SMASH_CHARGED3 = new BasicAttackAnimation(0.15F, 0.75F, 0.92F, 5.0F, null, biped.toolR,  "biped/smash/smash_heavy3", biped)
//                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(11))
//                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
//                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.67F))
//                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
//        SMASH_CHARGED4 = new BasicAttackAnimation(0.15F, 0.75F, 0.92F, 5.0F, null, biped.toolR,  "biped/smash/smash_heavy4", biped)
//                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(15.5F))
//                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
//                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.67F))
//                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));

        THRUST_DERIVE_PRE = new AttackAnimation(0.00F, 0.0167F, 0.0167F,0.3F, 0.33F, null, biped.toolR,  "biped/thrust/thrust_derive_pre", biped)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.CAN_FIRST_DERIVE, false, serverPlayerPatch.getOriginal());
                            }
                }), AnimationEvent.Side.SERVER),
                AnimationEvent.TimeStampedEvent.create(0.38F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        //如果还按着按钮就接着戳
                        if(dataManager.getDataValue(SmashHeavyAttack.KEY_PRESSING)){
//                            serverPlayerPatch.reserveAnimation(THRUST_DERIVE1);
                            serverPlayerPatch.playAnimationSynchronized(THRUST_DERIVE1, 0.15F);//不是哥们，怎么改成这个就不会断了
                        } else {
                            serverPlayerPatch.reserveAnimation(THRUST_DERIVE1_BACKSWING);
                        }
                    }
                }), AnimationEvent.Side.SERVER))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F));

        THRUST_DERIVE1 = new AttackAnimation(0.15F, "biped/thrust/thrust_derive1", biped,
                new AttackAnimation.Phase(0.0F, 0.00F, 0.50F, 0.93F, 0.50F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)),
                new AttackAnimation.Phase(0.49F, 0.50F, 1.0F, 1.0F, 1.0F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.05F)))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 2.5F))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)//防止视角变化无效
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.93F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                                //如果还按着按钮而且有耐力就接着衍生1
                                if(dataManager.getDataValue(SmashHeavyAttack.KEY_PRESSING)){
                                    if(serverPlayerPatch.hasStamina(Config.DERIVE_STAMINA_CONSUME.get().floatValue())){
                                        serverPlayerPatch.consumeStamina(serverPlayerPatch.getOriginal().isCreative() ? 0 : Config.DERIVE_STAMINA_CONSUME.get().floatValue());
                                        serverPlayerPatch.reserveAnimation(THRUST_DERIVE1);
                                    } else {
                                        //没耐力就后跳
                                        dataManager.setDataSync(SmashHeavyAttack.IS_REPEATING_DERIVE, false, serverPlayerPatch.getOriginal());
                                        serverPlayerPatch.reserveAnimation(THRUST_DERIVE1_BACKSWING);
                                    }
                                }
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                                dataManager.setDataSync(SmashHeavyAttack.CAN_FIRST_DERIVE, false, serverPlayerPatch.getOriginal());
                                dataManager.setDataSync(SmashHeavyAttack.IS_REPEATING_DERIVE, true, serverPlayerPatch.getOriginal());
                            }
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.00F, ((livingEntityPatch, staticAnimation, objects) -> {
                            CameraAnim.zoomIn(CameraAnim.DEFAULT_AIMING_CORRECTION);
                        }), AnimationEvent.Side.CLIENT));

        THRUST_DERIVE1_BACKSWING = new ActionAnimation(0.15F, "biped/thrust/thrust_derive_backswing", biped)
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)//解除锁视角的关键！
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.8F, (livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch playerPatch){
                        //设为可二段衍生状态
                        SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        dataManager.setDataSync(SmashHeavyAttack.DERIVE_TIMER, SmashHeavyAttack.MAX_TIMER, playerPatch.getOriginal());
                        dataManager.setDataSync(SmashHeavyAttack.CAN_SECOND_DERIVE, true, playerPatch.getOriginal());
                    }
                }, AnimationEvent.Side.SERVER));

        THRUST_DERIVE2 = new BasicAttackAnimation(0.15F, 1.1667F, 1.55F, 1.55F, WukongColliders.THRUST_3, biped.toolR, "biped/thrust/thrust_derive2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.CAN_SECOND_DERIVE, false, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER),
                AnimationEvent.TimeStampedEvent.create(0.00F, ((livingEntityPatch, staticAnimation, objects) -> {
                    CameraAnim.zoomIn(new Vec3f(1.0F, 0, 1.35F), 50);
                }), AnimationEvent.Side.CLIENT))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)//为了用自己的视角...
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));

        THRUST_CHARGED0 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.THRUST_0, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.0F));
        THRUST_CHARGED1 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.THRUST_1, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.7F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.0F));
        THRUST_CHARGED2 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.THRUST_2, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(3.0F));
        THRUST_CHARGED3 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.THRUST_3, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F));
        THRUST_CHARGED4 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.THRUST_4, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(7.0F));

//        THRUST_CHARGING = (new ActionAnimation(0.5F, "biped/thrust/thrust_charging", biped))
//                .addProperty(AnimationProperty.ActionAnimationProperty.STOP_MOVEMENT, true)
//                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1))
//                .addProperty(AnimationProperty.StaticAnimationProperty.POSE_MODIFIER, (self, pose, entityPatch, time, partialTicks) -> {
//                    if (self.isStaticAnimation()) {
//                        float xRot = Mth.clamp(entityPatch.getCameraXRot(), -60.0F, 50.0F);
//                        float yRot = Mth.clamp(Mth.wrapDegrees(entityPatch.getCameraYRot() - entityPatch.getOriginal().getYRot()), -60.0F, 60.0F);
//                        JointTransform chest = pose.getOrDefaultTransform("Chest");
//                        chest.frontResult(JointTransform.getRotation(Vector3f.YP.rotationDegrees(yRot)), OpenMatrix4f::mulAsOriginFront);
//                        JointTransform head = pose.getOrDefaultTransform("Head");
//                        MathUtils.mulQuaternion(Vector3f.XP.rotationDegrees(xRot), head.rotation(), head.rotation());
//                    }
//                })
//                .addEvents(AnimationEvent.TimeStampedEvent.create(0.97F, (livingEntityPatch, staticAnimation, objects) -> {
//                        if(livingEntityPatch instanceof ServerPlayerPatch playerPatch){
//                            if(playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(SmashHeavyAttack.KEY_PRESSING)){
//                                livingEntityPatch.reserveAnimation(THRUST_CHARGING);
//                            }
//                        }
//                    }, AnimationEvent.Side.SERVER))
//                .newTimePair(0.0F, Float.MAX_VALUE)
//                .addStateRemoveOld(EntityState.INACTION, true)
//                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
//                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
//                .addStateRemoveOld(EntityState.ATTACKING, true);
        THRUST_CHARGING = new StaticAnimation(true, "biped/thrust/thrust_charging", biped);

        THRUST_PRE = new BasicMultipleAttackAnimation(0, "biped/thrust/thrust_pre", biped,
        new AttackAnimation.Phase(0.0F, 0.05F, 0.15F, 0.75F, 0.25F , biped.toolR, null)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.1F)),
        new AttackAnimation.Phase(0.2F, 0.25F, 0.35F, 0.75F, 0.75F , biped.toolR, null)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.1F)))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false)
                .addEvents(AnimationEvent.TimeStampedEvent.create(1.39F, (livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(THRUST_CHARGING);
                    if(livingEntityPatch instanceof ServerPlayerPatch playerPatch && playerPatch.getSkill(SkillSlots.WEAPON_INNATE).hasSkill(WukongSkills.SMASH_HEAVY_ATTACK)){
                        playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, playerPatch.getOriginal());
                    }
                }, AnimationEvent.Side.SERVER));

//        THRUST_DERIVE1 = new SelectiveAnimation((entityPatch -> {
//            if(entityPatch instanceof ServerPlayerPatch playerPatch){
//                if(playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().getDataValue(HeavyAttack.STARTS_CONSUMED) > 0){
//                    return 1;
//                }
//            }
//            return 0;
//        }), THRUST_DERIVE1_COMMON, THRUST_DERIVE1_PLUS);
//        STAFF_FLOWER = new GuardAnimation()
    }

}
