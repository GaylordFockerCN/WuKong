package com.p1nero.wukong.epicfight;

import yesman.epicfight.skill.SkillCategory;

public enum SkillCategories implements SkillCategory
{
    BATTLE_STYLE(true, true, true),
    COMBAT_ART(true, true, true),
    BURST_ART(true, true, true),
    ULTIMATE_ART(true, true, true);

    boolean Save;
    boolean Sync;
    boolean Modifiable;
    int ID;

    SkillCategories(boolean ShouldSave, boolean ShouldSync, boolean Modifiable){
        this.Modifiable = Modifiable;
        this.Save = ShouldSave;
        this.Sync = ShouldSync;
        this.ID = SkillCategory.ENUM_MANAGER.assign(this);
    }

    @Override
    public boolean shouldSave()
    {
        return this.Save;
    }

    @Override
    public boolean shouldSynchronize()
    {
        return this.Sync;
    }

    @Override
    public boolean learnable()
    {
        return this.Modifiable;
    }
    @Override
    public int universalOrdinal()
    {
        return this.ID;
    }
}