package com.p1nero.wukong.epicfight;

import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public enum WukongSkillSlot implements SkillSlot {
    CHOP,
    POKE,
    STAND;
    SkillCategory category;
    int id;

    WukongSkillSlot(){
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
