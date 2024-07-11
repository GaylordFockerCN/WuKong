package com.p1nero.wukong.item;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WukongItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WukongMoveset.MOD_ID);
    public static final RegistryObject<Item> LOONG_ROAR = ITEMS.register("loong_roar", () -> new GoldBandedStaff(Tiers.NETHERITE, 1, -2, (new Item.Properties()).defaultDurability(2777).rarity(Rarity.EPIC).tab(WukongCreativeTabs.ITEMS)));
}
