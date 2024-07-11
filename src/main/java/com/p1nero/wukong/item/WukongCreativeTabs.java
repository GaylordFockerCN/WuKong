package com.p1nero.wukong.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WukongCreativeTabs {
    public static final CreativeModeTab ITEMS = new CreativeModeTab("wukong.items") {
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(WukongItems.LOONG_ROAR.get());
        }
    };
}
