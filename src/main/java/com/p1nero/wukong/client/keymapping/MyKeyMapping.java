package com.p1nero.wukong.client.keymapping;

import net.minecraft.client.KeyMapping;

/**
 * 方便判断是否是松开状态，并且可以判断是奇次还是偶次按下
 */
public class MyKeyMapping extends KeyMapping {
    boolean isRelease;
    boolean lock;
    boolean isEvenNumber;
    int pressCnt = 0;
    public MyKeyMapping(String p_90821_, int p_90822_, String p_90823_) {
        super(p_90821_, p_90822_, p_90823_);
    }

    @Override
    public void setDown(boolean down) {
        super.setDown(down);
        if(down){
            lock = true;
            isRelease = false;
        }else if(lock){
            lock = false;
            isRelease = true;
            isEvenNumber = !isEvenNumber;
            pressCnt++;
        }
    }

    /**
     * 判断是否松开并重置
     */
    public boolean isRelease() {
        if(isRelease){
            isRelease = false;
            return true;
        }
        return false;
    }

    /**
     * 是否是偶数次按下
     */
    public boolean isEvenNumber() {
        return isEvenNumber;
    }

    /**
     * 返回总的按下的次数
     */
    public int getPressCnt() {
        if(pressCnt == 0 && isDown()){
            return 1;
        }
        if(isDown()){
            return pressCnt + 1;
        }
        return pressCnt;
    }
}
