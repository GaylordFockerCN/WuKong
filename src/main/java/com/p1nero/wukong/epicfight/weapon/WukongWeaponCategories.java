package com.p1nero.wukong.epicfight.weapon;

import net.minecraft.world.InteractionHand;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
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

    /**
     * 判断武器是否是悟空棍子类型
     */
    public static boolean isWeaponValid(LivingEntityPatch<?> playerPatch){
        return playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF);
    }

}
