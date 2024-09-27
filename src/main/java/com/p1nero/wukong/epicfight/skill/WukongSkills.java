package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.StaffSpin;
import com.p1nero.wukong.epicfight.skill.custom.ThrustHeavyAttack;
import com.p1nero.wukong.item.WukongItems;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;

public class WukongSkills {
    public static Skill SMASH_STYLE;
    public static Skill THRUST_STYLE;
    public static Skill PILLAR_STYLE;
    public static Skill SMASH_HEAVY_ATTACK;
    public static Skill THRUST_HEAVY_ATTACK;
    public static Skill PILLAR_HEAVY_ATTACK;
    public static Skill STAFF_SPIN;
    public static Skill Ding;//定身术

    public static void registerSkills() {
        SkillManager.register(StaffSpin::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE), WukongMoveset.MOD_ID, "staff_flower");
        SkillManager.register(SmashHeavyAttack::new, SmashHeavyAttack.createChargedAttack()
                        .setChargePreAnimation(()-> WukongAnimations.THRUST_PRE)
                        .setChargingAnimation(()->WukongAnimations.THRUST_CHARGING)
                        .setHeavyAttacks(
                                () -> WukongAnimations.THRUST_CHARGED0,
                                () -> WukongAnimations.THRUST_CHARGED1,
                                () -> WukongAnimations.THRUST_CHARGED2,
                                () -> WukongAnimations.THRUST_CHARGED3,
                                () -> WukongAnimations.THRUST_CHARGED4)
                        .setDeriveAnimations(
                                () -> Animations.SWORD_AUTO1,
                                () -> Animations.SWORD_AUTO1)
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
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.SMASH).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "chop_style");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.THRUST).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "thrust_style");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.PILLAR).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "stand_style");
    }


    public static void BuildSkills(SkillBuildEvent event){

        STAFF_SPIN = event.build(WukongMoveset.MOD_ID, "staff_flower");

        SMASH_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "smash_charged");
        THRUST_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "thrust_charged");
        PILLAR_HEAVY_ATTACK = event.build(WukongMoveset.MOD_ID, "stand_charged");

        SMASH_STYLE = event.build(WukongMoveset.MOD_ID, "chop_style");
        THRUST_STYLE = event.build(WukongMoveset.MOD_ID, "thrust_style");
        PILLAR_STYLE = event.build(WukongMoveset.MOD_ID, "stand_style");
    }

}
