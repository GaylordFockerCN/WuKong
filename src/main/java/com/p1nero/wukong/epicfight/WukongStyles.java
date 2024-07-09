package com.p1nero.wukong.epicfight;

import yesman.epicfight.world.capabilities.item.Style;

public enum WukongStyles implements Style {

    CHOP(false),
    POKE(false),
    STAND(false),
    WUKONG_COMMON(false);
    final boolean canUseOffhand;
    final int id;

    WukongStyles(boolean canUseOffhand) {
        this.id = Style.ENUM_MANAGER.assign(this);
        this.canUseOffhand = canUseOffhand;
    }

    public int universalOrdinal() {
        return this.id;
    }

    public boolean canUseOffhand() {
        return this.canUseOffhand;
    }

}
