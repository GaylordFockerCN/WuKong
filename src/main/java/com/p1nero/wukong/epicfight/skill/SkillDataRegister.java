package com.p1nero.wukong.epicfight.skill;

import yesman.epicfight.skill.SkillDataManager;

public class SkillDataRegister {
    public static <T> void register(SkillDataManager manager, SkillDataManager.SkillDataKey<T> key, T value){
        manager.registerData(key);
        manager.setData(key, value);
    }
}
