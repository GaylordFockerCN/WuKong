package com.p1nero.wukong.item;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.ModList;
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
    public static final RegistryObject<Item> STAFF = ITEMS.register("staff", () -> new TestStaff(Tiers.NETHERITE, 1, -3, (new Item.Properties()).defaultDurability(114514).rarity(Rarity.COMMON).tab(CREATIVE_MODE_TAB)));
    public static final RegistryObject<Item> JIN_GU_BANG;
    static {
        if(ModList.get().isLoaded("geckolib3")){
            JIN_GU_BANG = ITEMS.register("jingubang", () -> new JinGuBang(Tiers.NETHERITE, 1, -3, (new Item.Properties()).defaultDurability(2777).rarity(Rarity.EPIC).tab(CREATIVE_MODE_TAB)));
        } else {
            JIN_GU_BANG = STAFF;
        }
    }
}
