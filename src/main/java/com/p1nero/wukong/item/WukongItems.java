package com.p1nero.wukong.item;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WukongItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WukongMoveset.MOD_ID);
    public static final RegistryObject<Item> STAFF = ITEMS.register("staff", () -> new TestStaff(Tiers.NETHERITE, -1, -3, (new Item.Properties()).defaultDurability(114514).rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> KANG_JIN = ITEMS.register("kang_jin", () -> new TestStaff(Tiers.NETHERITE, 1, -3, (new Item.Properties()).defaultDurability(2777).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> RED_TIDE = ITEMS.register("red_tide", () -> new RedTide(Tiers.NETHERITE, 1, -3, (new Item.Properties()).defaultDurability(2777).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> JIN_GU_BANG = ITEMS.register("jingubang", () -> new JinGuBang(Tiers.NETHERITE, 2, -3, (new Item.Properties()).stacksTo(1).setNoRepair().rarity(Rarity.EPIC)));
    public static final DeferredRegister<CreativeModeTab> ITEM_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WukongMoveset.MOD_ID);
    public static final RegistryObject<CreativeModeTab> WK = ITEM_TAB.register("wukong_items",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.wukong.items"))
                    .icon(() -> new ItemStack(JIN_GU_BANG.get()))
                    .displayItems((parameters, tabData) -> {
                        tabData.accept(STAFF.get());
                        tabData.accept(KANG_JIN.get());
                        tabData.accept(RED_TIDE.get());
                        tabData.accept(JIN_GU_BANG.get());
                    }).build());

}
