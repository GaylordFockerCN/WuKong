package com.p1nero.wukong.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * 记录飞行和技能使用的状态，被坑了，这玩意儿也分服务端和客户端...
 */
public class WKPlayer {
    public boolean isPlayingAnim;
    private boolean isFlying;
    private boolean protectNextFall;
    private boolean hasSwordEntity;
    private int swordScreenEntityCount;
    public int rainScreenCooldownTimer;
    private int rainCutterTimer;
    public int rainCutterCooldownTimer;
    private boolean isScreenCutterCoolDown;
    private int yakshaMaskTimer;
    public int yakshaMaskCooldownTimer;
    public boolean canYakshaMask;
    public boolean isYakshaFall;
    public int stellarSwordID;
    public int stayInAirTick;
    private int flyingTick;
    public double flyHeight;
    //客户端和服务端用途不一样
    public boolean isStellarRestorationPressing;
    public boolean isStellarRestorationSecondPressing;
    public int stellarRestorationCooldownTimer;
    private Set<Integer> swordID;
    private int anticipationTick;
    private ItemStack sword;

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean flying) {
        isFlying = flying;
    }

    public boolean isProtectNextFall() {
        return protectNextFall;
    }

    public void setProtectNextFall(boolean protectNextFall) {
        this.protectNextFall = protectNextFall;
    }

    public boolean hasSwordEntity() {
        return hasSwordEntity;
    }

    public void setHasSwordEntity(boolean hasSwordEntity) {
        this.hasSwordEntity = hasSwordEntity;
    }

    public int getSwordScreenEntityCount() {
        return swordScreenEntityCount;
    }

    public void setSwordScreenEntityCount(int swordScreenEntityCount) {
        if(swordScreenEntityCount < 0){
            return;
        }
        this.swordScreenEntityCount = swordScreenEntityCount;
    }

    public int getRainCutterTimer() {
        return rainCutterTimer;
    }

    public void setRainCutterTimer(int rainCutterTimer) {
        this.rainCutterTimer = rainCutterTimer;
    }

    public boolean isScreenCutterCoolDown() {
        return isScreenCutterCoolDown;
    }

    public int getYakshaMaskTimer() {
        return yakshaMaskTimer;
    }

    public void setYakshaMaskTimer(int yakshaMaskTimer) {
        this.yakshaMaskTimer = yakshaMaskTimer;
    }

    public void setScreenCutterCoolDown(boolean screenCutterCoolDown) {
        this.isScreenCutterCoolDown = screenCutterCoolDown;
    }

    public void setSwordID(Set<Integer> swordID) {
        this.swordID = swordID;
    }

    public Set<Integer> getSwordScreensID() {
        if(swordID == null){
            swordID = new HashSet<>();
        }
        return swordID;
    }

    public int getAnticipationTick() {
        return anticipationTick;
    }

    public void setAnticipationTick(int anticipationTick) {
        this.anticipationTick = anticipationTick;
    }

    public int getFlyingTick() {
        return flyingTick;
    }

    public void setFlyingTick(int flyingTick) {
        this.flyingTick = flyingTick;
    }

    public ItemStack getSword() {
        if(sword == null){
            return ItemStack.EMPTY;
        }
        return sword;
    }

    public void setSword(ItemStack sword) {
        this.sword = sword;
    }

    public void saveNBTData(CompoundTag tag){
        tag.putBoolean("isFlying", isFlying);
        tag.putBoolean("protectNextFall", protectNextFall);
        tag.putBoolean("hasEntity", hasSwordEntity);
        tag.putInt("hasSwordScreenEntity", swordScreenEntityCount);
        tag.putInt("rainCutterTimer", rainCutterTimer);
        tag.putBoolean("rainCutterCoolDown", isScreenCutterCoolDown);
        tag.putInt("yakshaMaskTimer", yakshaMaskTimer);
        tag.putInt("anticipationTick", anticipationTick);
        tag.putInt("flyingTick", flyingTick);
        if(sword != null){
            tag.put("sword", sword.serializeNBT());
        }else {
            tag.put("sword", new CompoundTag());
        }
    }

    public void loadNBTData(CompoundTag tag){
        isFlying = tag.getBoolean("isFlying");
        protectNextFall = tag.getBoolean("protectNextFall");
        hasSwordEntity = tag.getBoolean("hasEntity");
//        swordScreenEntityCount = tag.getInt("hasSwordScreenEntity");
        rainCutterTimer = tag.getInt("rainCutterTimer");
        isScreenCutterCoolDown = tag.getBoolean("rainCutterCoolDown");
        yakshaMaskTimer = tag.getInt("yakshaMaskTimer");
        anticipationTick = tag.getInt("anticipationTick");
        flyingTick = tag.getInt("flyingTick");
        sword = ItemStack.of(tag.getCompound("sword"));
    }

    public void copyFrom(WKPlayer old){
        isFlying = old.isFlying;
        protectNextFall = old.protectNextFall;
        hasSwordEntity = old.hasSwordEntity;
//        swordScreenEntityCount = old.swordScreenEntityCount;
        rainCutterTimer = old.rainCutterTimer;
        isScreenCutterCoolDown = old.isScreenCutterCoolDown;
        yakshaMaskTimer = old.yakshaMaskTimer;
        anticipationTick = old.anticipationTick;
        flyingTick = old.flyingTick;
        sword = old.sword;
    }

}
