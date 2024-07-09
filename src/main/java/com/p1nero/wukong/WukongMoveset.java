package com.p1nero.wukong;

import com.mojang.logging.LogUtils;
import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.network.PacketHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

@Mod("wukong")
public class WukongMoveset
{
    // Directly reference a slf4j logger
    public static final String MOD_ID = "wukong";
    private static final Logger LOGGER = LogUtils.getLogger();

    public WukongMoveset()
    {

        SkillCategory.ENUM_MANAGER.loadPreemptive(WukongSkillCategories.class);
        SkillSlot.ENUM_MANAGER.loadPreemptive(WukongSkillSlots.class);
        WeaponCategory.ENUM_MANAGER.loadPreemptive(WukongWeaponCategories.class);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus fg_bus = MinecraftForge.EVENT_BUS;
        fg_bus.addListener(WukongSkills::BuildSkills);
        fg_bus.addListener(WukongKeyMappings::onClientTick);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        PacketHandler.register();
        WukongSkills.registerSkills();
    }

}
