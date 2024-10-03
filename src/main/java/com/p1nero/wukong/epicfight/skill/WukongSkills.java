package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.*;
import com.p1nero.wukong.item.WukongItems;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.concurrent.atomic.AtomicInteger;

public class WukongSkills {
    public static Skill SMASH_STYLE;
    public static Skill THRUST_STYLE;
    public static Skill PILLAR_STYLE;
    public static Skill SMASH_HEAVY_ATTACK;
    public static Skill THRUST_HEAVY_ATTACK;
    public static Skill PILLAR_HEAVY_ATTACK;
    public static Skill STAFF_SPIN;
    public static Skill WUKONG_DODGE;
    public static Skill Ding;//定身术
    public static int getCurrentStack(Player player){
        AtomicInteger stack = new AtomicInteger(0);
        player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
            if(entityPatch instanceof PlayerPatch<?> patch){
                stack.set(patch.getSkill(SkillSlots.WEAPON_INNATE).getStack());
            }
        });
        return stack.get();
    }

    public static void registerSkills() {
        SkillManager.register(WukongDodge::new, WukongDodge.createDodgeBuilder()
                .setAnimations1(
                        () -> WukongAnimations.DODGE_F1,
                        () -> WukongAnimations.DODGE_B1,
                        () -> WukongAnimations.DODGE_L1,
                        () -> WukongAnimations.DODGE_R1
                )
                .setAnimations2(
                        () -> WukongAnimations.DODGE_F2,
                        () -> WukongAnimations.DODGE_B2,
                        () -> WukongAnimations.DODGE_L2,
                        () -> WukongAnimations.DODGE_R2
                )
                .setAnimations3(
                        () -> WukongAnimations.DODGE_F3,
                        () -> WukongAnimations.DODGE_B3,
                        () -> WukongAnimations.DODGE_L3,
                        () -> WukongAnimations.DODGE_R3
                )
                .setPerfectAnimations(
                        () -> WukongAnimations.DODGE_FP,
                        () -> WukongAnimations.DODGE_BP,
                        () -> WukongAnimations.DODGE_LP,
                        () -> WukongAnimations.DODGE_RP
                ).setCreativeTab(WukongItems.CREATIVE_MODE_TAB),
                WukongMoveset.MOD_ID, "dodge");
        SkillManager.register(StaffSpin::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE), WukongMoveset.MOD_ID, "staff_flower");
        SkillManager.register(SmashHeavyAttack::new, SmashHeavyAttack.createChargedAttack()
                        .setChargePreAnimation(()-> WukongAnimations.SMASH_CHARGING_PRE)
                        .setChargingAnimation(()->WukongAnimations.SMASH_CHARGING_LOOP)
                        .setHeavyAttacks(
                                () -> WukongAnimations.SMASH_CHARGED4,
                                () -> WukongAnimations.SMASH_CHARGED1,
                                () -> WukongAnimations.SMASH_CHARGED2,
                                () -> WukongAnimations.SMASH_CHARGED3,
                                () -> WukongAnimations.SMASH_CHARGED4)
                        .setDeriveAnimations(
                                () -> WukongAnimations.SMASH_DERIVE1,
                                () -> WukongAnimations.SMASH_DERIVE2)
                        .setJumpAttackHeavy(() -> WukongAnimations.JUMP_ATTACK_HEAVY)
                , WukongMoveset.MOD_ID, "smash_charged");
        SkillManager.register(ThrustHeavyAttack::new, ThrustHeavyAttack.createChargedAttack()
                .setChargePreAnimation(()-> WukongAnimations.THRUST_PRE)
                .setChargingAnimation(()->WukongAnimations.THRUST_CHARGING)
                .setAnimationProviders(
                        () -> WukongAnimations.THRUST_CHARGED0,
                        () -> WukongAnimations.THRUST_CHARGED1,
                        () -> WukongAnimations.THRUST_CHARGED2,
                        () -> WukongAnimations.THRUST_CHARGED3,
                        () -> WukongAnimations.THRUST_CHARGED4)
                .setDeriveAnimations(
                        () -> WukongAnimations.THRUST_DERIVE_PRE,
                        () -> WukongAnimations.THRUST_DERIVE2)
                .setCanChargeWhenMove(false)
                , WukongMoveset.MOD_ID, "thrust_charged");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.SMASH).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "smash_style");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.THRUST).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "thrust_style");
        SkillManager.register(StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.PILLAR).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "pillar_style");
    }


    public static void BuildSkills(SkillBuildEvent event){
        WUKONG_DODGE = event.build(WukongMoveset.MOD_ID, "dodge");
        STAFF_SPIN = event.build(WukongMoveset.MOD_ID, "staff_flower");

        SMASH_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "smash_charged");
        THRUST_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "thrust_charged");
        PILLAR_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "stand_charged");

        SMASH_STYLE = event.build(WukongMoveset.MOD_ID, "smash_style");
        THRUST_STYLE = event.build(WukongMoveset.MOD_ID, "thrust_style");
        PILLAR_STYLE = event.build(WukongMoveset.MOD_ID, "pillar_style");
    }

}
