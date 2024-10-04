package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.client.event.CameraAnim;
import com.p1nero.wukong.epicfight.animation.custom.*;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongColliders;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.*;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongAnimations {

    public static StaticAnimation IDLE;
    public static StaticAnimation WALK;
    public static StaticAnimation RUN_F;
    public static StaticAnimation RUN;
    public static StaticAnimation DASH;
    public static StaticAnimation JUMP;
    public static StaticAnimation FALL;
    public static StaticAnimation JUMP_ATTACK_LIGHT;
    public static StaticAnimation JUMP_ATTACK_LIGHT_HIT;
    public static StaticAnimation JUMP_ATTACK_LIGHT_FALL;
    public static StaticAnimation JUMP_ATTACK_HEAVY;
    public static StaticAnimation JUMP_ATTACK_HEAVY_END;
    public static StaticAnimation DODGE_F1;
    public static StaticAnimation DODGE_F2;
    public static StaticAnimation DODGE_F3;
    public static StaticAnimation DODGE_FP;
    public static StaticAnimation DODGE_B1;
    public static StaticAnimation DODGE_B2;
    public static StaticAnimation DODGE_B3;
    public static StaticAnimation DODGE_BP;
    public static StaticAnimation DODGE_L1;
    public static StaticAnimation DODGE_L2;
    public static StaticAnimation DODGE_L3;
    public static StaticAnimation DODGE_LP;
    public static StaticAnimation DODGE_R1;
    public static StaticAnimation DODGE_R2;
    public static StaticAnimation DODGE_R3;
    public static StaticAnimation DODGE_RP;
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
    public static StaticAnimation SMASH_CHARGING_LOOP_MOVE;
    public static StaticAnimation SMASH_CHARGING_LOOP_STAND;
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
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));
        RUN_F = new StaticAnimation(true, "biped/run",biped);
        RUN = new SelectiveAnimation((entityPatch) -> {
            Vec3 view = entityPatch.getOriginal().getViewVector(1.0F);
            Vec3 move = entityPatch.getOriginal().getDeltaMovement();
            double dot = view.dot(move);
            return dot < 0.0 ? 1 : 0;
        }, RUN_F, WALK);
        DASH = new StaticAnimation(true, "biped/dash",biped);
        JUMP = new StaticAnimation(0.15F, false, "biped/jump",biped)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));
        FALL = new StaticAnimation(0.15F, true, "biped/fall",biped);
        DODGE_F1 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f1", 0.6F, 0.8F, biped);
        DODGE_B1 = new WukongDodgeAnimation(0.1F, 0.4F,"biped/dodge/dodge_b1", 0.6F, 0.8F, biped);
        DODGE_R1 = new WukongDodgeAnimation(0.1F, 0.4F,"biped/dodge/dodge_r1", 0.6F, 0.8F, biped);
        DODGE_L1 = new WukongDodgeAnimation(0.1F, 0.4F,"biped/dodge/dodge_l1", 0.6F, 0.8F, biped);
        DODGE_F2 = new WukongDodgeAnimation(0.1F, 0.4F, "biped/dodge/dodge_f2", 0.6F, 0.8F, biped);
        DODGE_B2 = new WukongDodgeAnimation(0.1F, 0.4F,"biped/dodge/dodge_b2", 0.6F, 0.8F, biped);
        DODGE_R2 = new WukongDodgeAnimation(0.1F, 0.4F,"biped/dodge/dodge_r2", 0.6F, 0.8F, biped);
        DODGE_L2 = new WukongDodgeAnimation(0.1F, 0.4F,"biped/dodge/dodge_l2", 0.6F, 0.8F, biped);
        DODGE_F3 = new WukongDodgeAnimation(0.1F, 0.8F,"biped/dodge/dodge_f3", 0.6F, 1.35F, biped);
        DODGE_B3 = new WukongDodgeAnimation(0.1F, 0.8F,"biped/dodge/dodge_b3", 0.6F, 1.35F, biped);
        DODGE_R3 = new WukongDodgeAnimation(0.1F, 0.8F,"biped/dodge/dodge_r3", 0.6F, 1.35F, biped);
        DODGE_L3 = new WukongDodgeAnimation(0.1F, 0.8F,"biped/dodge/dodge_l3", 0.6F, 1.35F, biped);
        DODGE_FP = new WukongDodgeAnimation(0.1F, 0.83F,"biped/dodge/dodge_fp", 0.6F, 1.35F, biped);
        DODGE_BP = new WukongDodgeAnimation(0.1F, 0.83F,"biped/dodge/dodge_bp", 0.6F, 1.35F, biped);
        DODGE_RP = new WukongDodgeAnimation(0.1F, 0.83F,"biped/dodge/dodge_rp", 0.6F, 1.35F, biped);
        DODGE_LP = new WukongDodgeAnimation(0.1F, 0.83F,"biped/dodge/dodge_lp", 0.6F, 1.35F, biped);

        STAFF_AUTO1_DASH = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR,  "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                //冲刺攻击重置普攻计数器
                                BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.BASIC_ATTACK_COUNT, serverPlayerPatch, serverPlayerPatch.getSkill(SkillSlots.BASIC_ATTACK), staticAnimation, 1);
                            }
                        }), AnimationEvent.Side.SERVER));
        STAFF_AUTO1 = new BasicAttackAnimation(0.15F, 0.2916F, 0.5000F, 0.5833F, null, biped.toolR,  "biped/auto_1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.9F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        STAFF_AUTO2 = new BasicAttackAnimation(0.15F, 0.6667F, 0.875F, 0.875F, null, biped.toolR,  "biped/auto_2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.8F));
        STAFF_AUTO3 = new BasicMultipleAttackAnimation(0.15F, "biped/auto_3", biped,
                new AttackAnimation.Phase(0.0F, 0.25F, 0.4583F, 0.4583F, 0.4583F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F)),
                new AttackAnimation.Phase(0.4583F, 0.4583F, 0.7083F, 0.7083F, 3.3333F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F)))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));
        STAFF_AUTO4 = new BasicMultipleAttackAnimation(0.15F, "biped/auto_4", biped,
                new AttackAnimation.Phase(0.0F, 0.1F, 0.2F, 0.2F, 0.2F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.2F, 0.2F, 0.4F, 0.4F, 0.4F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.4F, 0.4F, 0.6F, 0.6F, 0.6F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.6F, 0.6F, 0.8F, 0.8F, 0.8F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.5F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_ROD),
                new AttackAnimation.Phase(0.8F, 1.0416F, 1.125F, 1.2583F, 2.5F , biped.toolR, null)
                        .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.0F))
                        .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(5F)))
                        .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F));
        STAFF_AUTO5 = new BasicAttackAnimation(0.01F, 0.9166F,1.15F, 1.9833F, null, biped.toolR,  "biped/auto_5", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.9833F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.2F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) ->
                                livingEntityPatch.playSound(EpicFightSounds.ENTITY_MOVE, 1, 1)), AnimationEvent.Side.SERVER));

        JUMP_ATTACK_LIGHT = new WukongJumpAttackAnimation(0.10F, 0.13F, 0.40F, 0.50F, WukongColliders.JUMP_ATTACK_LIGHT, biped.toolR,  "biped/jump_attack/jump_light_pre", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.45F))
                .addProperty(AnimationProperty.AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))//最多踹一个
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.10F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        JUMP_ATTACK_LIGHT_HIT = new ActionAnimation(0.15F, "biped/jump_attack/jump_light_hit", biped)
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.0F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        JUMP_ATTACK_HEAVY = new WukongScaleStaffAttackAnimation(0.01F, 0.54F,0.67F, 1.25F, null, biped.toolR,  "biped/jump_attack/jump_heavy", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.67F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS,
                        AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) ->
                                livingEntityPatch.playSound(EpicFightSounds.ROLL, 1, 1)), AnimationEvent.Side.SERVER));


        STAFF_FLOWER_ONE_HAND_LOOP = new StaffFlowerAttackAnimation(1.25F, biped, "biped/staff_flower/staff_flower_one_hand", 0.05F, false);
        STAFF_FLOWER_TWO_HAND_LOOP = new StaffFlowerAttackAnimation(0.83F, biped, "biped/staff_flower/staff_flower_two_hand", 0.08F, true);

        //劈start
        //前摇完自动接下一个动作
        SMASH_CHARGING_PRE = new ActionAnimation(0.15F, "biped/smash/smash_charge_pre", biped)
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.reserveAnimation(SMASH_CHARGING_LOOP_STAND);
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_CHARGING, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(
                        AnimationEvent.TimeStampedEvent.create(0.1F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER),
                        AnimationEvent.TimeStampedEvent.create(0.3F, ((livingEntityPatch, staticAnimation, objects) -> {
                            livingEntityPatch.playSound(EpicFightSounds.WHOOSH_ROD, 1, 1);
                        }), AnimationEvent.Side.SERVER));
        SMASH_CHARGING_LOOP_STAND = new StaticAnimation(0.15F, true, "biped/smash/smash_charging", biped);

        SMASH_CHARGED0 = new WukongScaleStaffAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, WukongColliders.STACK_0_1, biped.toolR,  "biped/smash/smash_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.75F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(allStopMovement)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER));
        SMASH_CHARGED1 = new WukongScaleStaffAttackAnimation(0.15F, 0.75F, 0.92F, 1.67F, null, biped.toolR,  "biped/smash/smash_heavy1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(5.6F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 0.75F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(allStopMovement)
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.083F, ((livingEntityPatch, staticAnimation, objects) -> {
                    livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1);
                }), AnimationEvent.Side.SERVER));
        SMASH_CHARGED2 = new WukongScaleStaffAttackAnimation(0.15F, 1.30F, 1.55F, 2.5F, null, biped.toolR,  "biped/smash/smash_heavy2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(8.8F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.30F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        List<AnimationEvent.TimeStampedEvent> sc2List = append(
                AnimationEvent.TimeStampedEvent.create(0.292F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.reset(1.30F),
                        ScaleTime.of(1.45F, 1, 1.8F, 1),
                        ScaleTime.of(2.13F, 1, 1.8F, 1),
                        ScaleTime.reset(2.29F)
                )
        );
        sc2List.add(AnimationEvent.TimeStampedEvent.create(0.833F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        SMASH_CHARGED2
                .addEvents(sc2List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_CHARGED3 = new WukongScaleStaffAttackAnimation(0.15F, 1.792F, 1.958F, 2.667F, null, biped.toolR,  "biped/smash/smash_heavy3", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(11))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.30F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));
        List<AnimationEvent.TimeStampedEvent> sc3List = append(
                AnimationEvent.TimeStampedEvent.create(0.292F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                getScaleEvents(
                        ScaleTime.reset(1.667F),
                        ScaleTime.of(1.792F, 1, 2.4F, 1),
                        ScaleTime.of(1.958F, 1, 2.4F, 1),
                        ScaleTime.of(2.667F, 1, 2F, 1),
                        ScaleTime.reset(2.8F)
                )
        );
        sc3List.add(AnimationEvent.TimeStampedEvent.create(0.833F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        sc3List.add(AnimationEvent.TimeStampedEvent.create(1.125F, ((livingEntityPatch, staticAnimation, objects) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER));
        SMASH_CHARGED3.addEvents(sc3List.toArray(new AnimationEvent.TimeStampedEvent[0]));

        SMASH_CHARGED4 = new WukongScaleStaffAttackAnimation(0.15F, 2.63F, 2.9F, 3.3F, null, biped.toolR,  "biped/smash/smash_heavy4", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
                .addProperty(AnimationProperty.AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(4.0F))
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(15.5F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 2.63F))
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(
                        append(
                                AnimationEvent.TimeStampedEvent.create(0.208F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                                getScaleEvents(
                                        ScaleTime.of(2.4167F, 1, 1, 1),
                                        ScaleTime.of(2.5833F, 1.15F, 1.15F, 1.15F),
                                        ScaleTime.of(2.7083F, 1.15F, 3.15F, 1.15F),
                                        ScaleTime.of(3.3333F, 1.15F, 3.15F, 1.15F),
                                        ScaleTime.of(3.5833F, 1, 1, 1)
                                )
                        ).toArray(new AnimationEvent.TimeStampedEvent[0])
                );

        SMASH_DERIVE1 = new WukongScaleStaffAttackAnimation(0.15F, 0.63F, 0.75F, 1.20F, null, biped.toolR,  "biped/smash/smash_special1", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.SHORT)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        SkillDataManager dataManager = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                        dataManager.setDataSync(SmashHeavyAttack.CAN_FIRST_DERIVE, false, serverPlayerPatch.getOriginal());
                        dataManager.setDataSync(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK, true, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_END_EVENTS,
                    AnimationEvent.TimeStampedEvent.create(0.75F, ((livingEntityPatch, staticAnimation, objects) -> {
                        if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                            serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.IS_IN_SPECIAL_ATTACK, false, serverPlayerPatch.getOriginal());
                        }
                    }), AnimationEvent.Side.SERVER)
                )
                .addEvents(getScaleEvents(
                        ScaleTime.of(0.625F, 1, 1.8F, 1),
                        ScaleTime.of(1.125F, 1, 1.8F, 1),
                        ScaleTime.reset(1.25F)
                ));

        SMASH_DERIVE2 = new WukongScaleStaffAttackAnimation(0.15F, 1.04F, 1.71F, 2.30F, null, biped.toolR,  "biped/smash/smash_special2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.STUN_TYPE, StunType.LONG)
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(3.0F))
                .addProperty(AnimationProperty.ActionAnimationProperty.MOVE_VERTICAL, true)
                .addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false)
                .addProperty(AnimationProperty.ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(0.01F, 1.04F))
                .newTimePair(0.01F, 1.71F)
                .addState(EntityState.ATTACK_RESULT, (damageSource) -> AttackResult.ResultType.MISSED)
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                            if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.CAN_SECOND_DERIVE, false, serverPlayerPatch.getOriginal());
                            }
                        }), AnimationEvent.Side.SERVER))
                .addEvents(
                        append(
                                AnimationEvent.TimeStampedEvent.create(0.042F, ((livingEntityPatch, anim, obj) -> livingEntityPatch.playSound(WuKongSounds.HIT_GROUND.get(), 1, 1)), AnimationEvent.Side.SERVER),
                                getScaleEvents(
                                        ScaleTime.of(0.042F, 1, 1.583F, 1),
                                        ScaleTime.of(0.083F, 1, 1.758F, 1),
                                        ScaleTime.of(0.167F, 1, 1.952F, 1),
                                        ScaleTime.of(0.208F, 1, 2, 1),
                                        ScaleTime.of(1.458F, 1, 2, 1),
                                        ScaleTime.reset(1.460F)
                                )
                        ).toArray(new AnimationEvent.TimeStampedEvent[0])
                );
        //劈end
        //戳start
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
                            if(livingEntityPatch.getOriginal() instanceof LocalPlayer){
                                CameraAnim.zoomIn(CameraAnim.DEFAULT_AIMING_CORRECTION);
                            }
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
                        dataManager.setDataSync(SmashHeavyAttack.DERIVE_TIMER, SmashHeavyAttack.MAX_DERIVE_TIMER, playerPatch.getOriginal());
                        dataManager.setDataSync(SmashHeavyAttack.CAN_SECOND_DERIVE, true, playerPatch.getOriginal());
                    }
                }, AnimationEvent.Side.SERVER));

        THRUST_DERIVE2 = new BasicAttackAnimation(0.15F, 1.1667F, 1.55F, 1.55F, WukongColliders.STACK_3, biped.toolR, "biped/thrust/thrust_derive2", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.0F))
                .addEvents(AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(SmashHeavyAttack.CAN_SECOND_DERIVE, false, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER),
                AnimationEvent.TimeStampedEvent.create(0.00F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch.getOriginal() instanceof LocalPlayer) {
                        CameraAnim.zoomIn(new Vec3f(1.0F, 0, 1.35F), 50);
                    }
                }), AnimationEvent.Side.CLIENT))
                .addStateRemoveOld(EntityState.TURNING_LOCKED, false)//为了用自己的视角...
                .addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.0F));

        THRUST_CHARGED0 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.3F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(1.0F));
        THRUST_CHARGED1 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.STACK_0_1, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.7F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.0F));
        THRUST_CHARGED2 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.STACK_2, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(3.0F));
        THRUST_CHARGED3 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.STACK_3, biped.toolR, "biped/thrust/thrust_charged", biped)
                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(3.5F))
                .addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F));
        THRUST_CHARGED4 = new WukongChargedAttackAnimation(0, 0.15F, 0.25F, 1.5F, WukongColliders.STACK_4, biped.toolR, "biped/thrust/thrust_charged", biped)
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

    public static void addItemEffectTimer(ServerPlayer serverPlayer, int leftTime){
        serverPlayer.getMainHandItem().getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent((capabilityItem -> {
            if(capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)){
                serverPlayer.getMainHandItem().getOrCreateTag().putInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY, leftTime);
            }
        }));
    }

    public static List<AnimationEvent.TimeStampedEvent> append(AnimationEvent.TimeStampedEvent e, AnimationEvent.TimeStampedEvent... oldArr){
        List<AnimationEvent.TimeStampedEvent> list = new ArrayList<>(List.of(oldArr));
        list.add(e);
        return list;
    }

    /**
     * 添加物品缩放，并插值
     * 用途：{@link com.p1nero.wukong.mixin.ItemRendererMixin}
     *
     */
    public static AnimationEvent.TimeStampedEvent[] getScaleEvents(ScaleTime... ticks){
        int lastTick = ticks[ticks.length-1].tick;
        AnimationEvent.TimeStampedEvent[] timeStampedEvents = new AnimationEvent.TimeStampedEvent[lastTick];
        ticks = interpolate(ticks, lastTick);
        timeStampedEvents[0] = AnimationEvent.TimeStampedEvent.create(0.01F, ((livingEntityPatch, staticAnimation, objects) -> {
            CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
        }), AnimationEvent.Side.CLIENT);
        timeStampedEvents[lastTick-1] = AnimationEvent.TimeStampedEvent.create(0.05F * lastTick, ((livingEntityPatch, staticAnimation, objects) -> {
            CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
        }), AnimationEvent.Side.CLIENT);
        for(int i = 1; i < lastTick-1; i++){
            float x = ticks[i].x;
            float y = ticks[i].y;
            float z = ticks[i].z;
            timeStampedEvents[i] = AnimationEvent.TimeStampedEvent.create(0.05F * i, ((livingEntityPatch, staticAnimation, objects) -> {
                CompoundTag tag = livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag();
                tag.putBoolean("WK_shouldScaleItem", true);
                tag.putFloat("WK_XScale", x);
                tag.putFloat("WK_YScale", y);
                tag.putFloat("WK_ZScale", z);
            }), AnimationEvent.Side.CLIENT);
        }
        return timeStampedEvents;
    }

    /**
     * 插值处理
     * @param scaleTimes 需要插值的时间点，按tick算！
     * @param lastTick 最后一个tick，将对0~lastTick的每个tick插值处理
     * @return 插值后的数组
     */
    public static ScaleTime[] interpolate(ScaleTime[] scaleTimes, int lastTick) {
        ScaleTime[] results = new ScaleTime[lastTick + 1];

        // Fill known values
        for (ScaleTime scaleTime : scaleTimes) {
            if (scaleTime.tick <= lastTick) {
                results[scaleTime.tick] = scaleTime;
            }
        }

        // Perform linear interpolation
        for (int i = 0; i <= lastTick; i++) {
            if (results[i] == null) {
                // Find the two surrounding points
                ScaleTime before = null;
                ScaleTime after = null;

                for (int j = i - 1; j >= 0; j--) {
                    if (results[j] != null) {
                        before = results[j];
                        break;
                    }
                }

                for (int j = i + 1; j <= lastTick; j++) {
                    if (results[j] != null) {
                        after = results[j];
                        break;
                    }
                }

                if (before != null && after != null) {
                    // Linear interpolation
                    float t = (float) (i - before.tick) / (after.tick - before.tick);
                    float x = before.x + t * (after.x - before.x);
                    float y = before.y + t * (after.y - before.y);
                    float z = before.z + t * (after.z - before.z);
                    results[i] = new ScaleTime(i, x, y, z);
                }
            }
        }

        // Fill in nulls with the closest known value (forward filling)
        for (int i = 0; i <= lastTick; i++) {
            if (results[i] == null) {
                if(i > 0){
                    results[i] = results[i - 1]; // Copy the last known value
                } else {
                    results[i] = new ScaleTime(i, 1, 1, 1);
                }
            }
        }

        return results;
    }

    public record ScaleTime(int tick, float x, float y, float z){
        public static ScaleTime of(float time, float x, float y, float z){
            return new ScaleTime(((int) (time * 20)), x, y, z);
        }
        public static ScaleTime reset(float time){
            return new ScaleTime(((int) (time * 20)), 1, 1, 1);
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.player instanceof ServerPlayer serverPlayer){
            serverPlayer.getMainHandItem().getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent((capabilityItem -> {
                if(capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF)){
                    CompoundTag mainHandItem = serverPlayer.getMainHandItem().getOrCreateTag();
                    mainHandItem.putInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY, Math.max(0, mainHandItem.getInt(WukongMoveset.ITEM_HAS_EFFECT_TIMER_KEY) - 1));
                }
            }));
        }
    }

}
