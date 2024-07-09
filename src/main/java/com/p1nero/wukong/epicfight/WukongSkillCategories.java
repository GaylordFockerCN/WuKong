package com.p1nero.wukong.epicfight;

import yesman.epicfight.skill.SkillCategory;

public enum WukongSkillCategories implements SkillCategory
{
    //棍势
    STAFF_STYLE(true, true, true);

    final boolean save;
    final boolean sync;
    final boolean modifiable;
    final int id;

    WukongSkillCategories(boolean ShouldSave, boolean ShouldSync, boolean Modifiable){
        this.modifiable = Modifiable;
        this.save = ShouldSave;
        this.sync = ShouldSync;
        this.id = SkillCategory.ENUM_MANAGER.assign(this);
    }

    @Override
    public boolean shouldSave()
    {
        return this.save;
    }

    @Override
    public boolean shouldSynchronize()
    {
        return this.sync;
    }

    @Override
    public boolean learnable()
    {
        return this.modifiable;
    }
    @Override
    public int universalOrdinal()
    {
        return this.id;
    }
}