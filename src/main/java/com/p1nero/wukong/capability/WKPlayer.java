package com.p1nero.wukong.capability;

import net.minecraft.nbt.CompoundTag;

public class WKPlayer {
    private String lastSkill = "";//用于恢复闪避技能

    public void setLastDodgeSkill(String lastSkill) {
        this.lastSkill = lastSkill;
    }

    public String getLastDodgeSkill() {
        return lastSkill;
    }

    public void saveNBTData(CompoundTag tag){
        tag.putString("lastSkill", lastSkill);
    }

    public void loadNBTData(CompoundTag tag){
        lastSkill = tag.getString("lastSkill");
    }

    public void copyFrom(WKPlayer old){
        lastSkill = old.lastSkill;
    }

}
