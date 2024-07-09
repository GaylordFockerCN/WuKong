package com.p1nero.wukong.epicfight;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum WukongSkillSlots implements SkillSlot {
    STAFF_STYLE(WukongSkillCategories.STAFF_STYLE);
    final SkillCategory category;
    final int id;

    WukongSkillSlots(WukongSkillCategories category){
        this.category = category;
        this.id = SkillSlot.ENUM_MANAGER.assign(this);
    }


    @Override
    public SkillCategory category() {
        return category;
    }

    @Override
    public int universalOrdinal() {
        return id;
    }
}
