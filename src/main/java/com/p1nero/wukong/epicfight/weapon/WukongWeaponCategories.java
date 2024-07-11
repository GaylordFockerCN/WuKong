package com.p1nero.wukong.epicfight.weapon;

import yesman.epicfight.world.capabilities.item.WeaponCategory;

public enum WukongWeaponCategories implements WeaponCategory {
    WK_STAFF;
    private WukongWeaponCategories(){
        this.id = WeaponCategory.ENUM_MANAGER.assign(this);
    }
    final int id;
    @Override
    public int universalOrdinal() {
        return this.id;
    }
}
