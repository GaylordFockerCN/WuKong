package com.p1nero.wukong.capability;

import net.minecraft.nbt.CompoundTag;

public class WKPlayer {
    private String lastSkill = "";//用于恢复闪避技能
    private boolean perfectDodge;
    private float damageReduce = -1;

    public void setDamageReduce(float damageReduce) {
        this.damageReduce = damageReduce;
    }

    public float getDamageReduce() {
        return damageReduce;
    }

    public void setLastDodgeSkill(String lastSkill) {
        this.lastSkill = lastSkill;
    }

    public String getLastDodgeSkill() {
        System.out.println(lastSkill);
        return lastSkill;
    }

    public void setPerfectDodge(boolean perfectDodge) {
        this.perfectDodge = perfectDodge;
    }

    public boolean isPerfectDodge() {
        return perfectDodge;
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
