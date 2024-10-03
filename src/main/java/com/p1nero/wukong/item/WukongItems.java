package com.p1nero.wukong.item;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class WukongItems {

    public static final CreativeModeTab CREATIVE_MODE_TAB = new CreativeModeTab("wukong.items") {
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(WukongItems.STAFF.get());
        }
    };

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WukongMoveset.MOD_ID);
    public static final RegistryObject<Item> STAFF = ITEMS.register("staff", () -> new TestStaff(Tiers.NETHERITE, 1, -3, (new Item.Properties()).defaultDurability(2777).rarity(Rarity.EPIC).tab(CREATIVE_MODE_TAB)));

}
