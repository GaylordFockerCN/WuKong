package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;

public class WukongSkills {

    public static Skill CHOP_CHARGED;
    public static Skill POKE_CHARGED;
    public static Skill STAND_CHARGED;
    public static Skill CHOP_STYLE;
    public static Skill POKE_STYLE;
    public static Skill STAND_STYLE;

    public static Skill STAFF_FLOWER;
    public static Skill Ding;//定身术

    public static void registerSkills() {
//        SkillManager.register((styleBuilder)->new StaffStyle(styleBuilder, WukongStyles.CHOP), StaffStyle.createStaffStyle(), WukongMoveset.MOD_ID, "chop_style");
//        SkillManager.register((styleBuilder)->new StaffStyle(styleBuilder, WukongStyles.STAND), StaffStyle.createStaffStyle(), WukongMoveset.MOD_ID, "stand_style");
//        SkillManager.register((styleBuilder)->new StaffStyle(styleBuilder, WukongStyles.POKE), StaffStyle.createStaffStyle(), WukongMoveset.MOD_ID, "poke_style");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.CHOP), WukongMoveset.MOD_ID, "chop_style");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.POKE), WukongMoveset.MOD_ID, "poke_style");
        SkillManager.register(StaffStyle::new, StaffStyle.createStaffStyle().setStyle(WukongStyles.STAND), WukongMoveset.MOD_ID, "stand_style");
    }


    public static void BuildSkills(SkillBuildEvent event){
//        CHOP_CHARGED = event.build(WukongMoveset.MOD_ID, "chop_charged"); TODO 重击
        CHOP_STYLE = event.build(WukongMoveset.MOD_ID, "chop_style");
        POKE_STYLE = event.build(WukongMoveset.MOD_ID, "poke_style");
        STAND_STYLE = event.build(WukongMoveset.MOD_ID, "stand_style");
    }

}
