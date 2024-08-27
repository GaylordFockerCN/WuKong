package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.HeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.StaffFlower;
import com.p1nero.wukong.item.WukongItems;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;

public class WukongSkills {

    public static Skill COMMON;
    public static Skill CHOP_CHARGED;
    public static Skill POKE_CHARGED;
    public static Skill STAND_CHARGED;
    public static Skill CHOP_STYLE;
    public static Skill POKE_STYLE;
    public static Skill STAND_STYLE;

    public static Skill STAFF_FLOWER;
    public static Skill Ding;//定身术

    public static void registerSkills() {

        SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation("wukong", "staff_auto_5")), WukongMoveset.MOD_ID, "common");
        SkillManager.register(StaffFlower::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE), WukongMoveset.MOD_ID, "staff_flower");
//        SkillManager.register((styleBuilder)->new StaffStyle(styleBuilder, WukongStyles.CHOP), StaffStyle.createStaffStyle(), WukongMoveset.MOD_ID, "chop_style");
//        SkillManager.register((styleBuilder)->new StaffStyle(styleBuilder, WukongStyles.STAND), StaffStyle.createStaffStyle(), WukongMoveset.MOD_ID, "stand_style");
//        SkillManager.register((styleBuilder)->new StaffStyle(styleBuilder, WukongStyles.POKE), StaffStyle.createStaffStyle(), WukongMoveset.MOD_ID, "poke_style");
        SkillManager.register(HeavyAttack::new, HeavyAttack.createChargedAttack()
                .setChargePreAnimation(()-> WukongAnimations.POKE_PRE)
                .setChargingAnimation(()->WukongAnimations.POKE_CHARGING)
                .setAnimationProviders(
                        ()->WukongAnimations.POKE_CHARGED0,
                        ()->WukongAnimations.POKE_CHARGED1,
                        ()->WukongAnimations.POKE_CHARGED2,
                        ()->WukongAnimations.POKE_CHARGED3,
                        ()->WukongAnimations.POKE_CHARGED4)
                .setDeriveAnimations(
                        ()->WukongAnimations.POKE_DERIVE_PRE,
                        ()->WukongAnimations.POKE_DERIVE2)
                .setCanChargeWhenMove(false)
                , WukongMoveset.MOD_ID, "poke_charged");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.CHOP).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "chop_style");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.POKE).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "poke_style");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.STAND).setCreativeTab(WukongItems.CREATIVE_MODE_TAB), WukongMoveset.MOD_ID, "stand_style");
    }


    public static void BuildSkills(SkillBuildEvent event){
        COMMON = event.build(WukongMoveset.MOD_ID, "common");

        STAFF_FLOWER = event.build(WukongMoveset.MOD_ID, "staff_flower");

        CHOP_CHARGED = event.build(WukongMoveset.MOD_ID, "chop_charged");
        POKE_CHARGED = event.build(WukongMoveset.MOD_ID, "poke_charged");
        STAND_CHARGED = event.build(WukongMoveset.MOD_ID, "stand_charged");

        CHOP_STYLE = event.build(WukongMoveset.MOD_ID, "chop_style");
        POKE_STYLE = event.build(WukongMoveset.MOD_ID, "poke_style");
        STAND_STYLE = event.build(WukongMoveset.MOD_ID, "stand_style");
    }

}
