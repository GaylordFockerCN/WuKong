package com.p1nero.wukong;

import com.mojang.logging.LogUtils;
import com.p1nero.wukong.client.particle.WuKongParticles;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.item.WukongItems;
import com.p1nero.wukong.network.PacketHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Mod("wukong")
public class WukongMoveset
{
    public static final String MOD_ID = "wukong";
    public static final String ITEM_HAS_EFFECT_TIMER_KEY = "wukong_has_effect_timer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WukongMoveset()
    {
        SkillCategory.ENUM_MANAGER.loadPreemptive(WukongSkillCategories.class);
        SkillSlot.ENUM_MANAGER.loadPreemptive(WukongSkillSlots.class);
        WeaponCategory.ENUM_MANAGER.loadPreemptive(WukongWeaponCategories.class);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        WukongItems.ITEMS.register(bus);
        WuKongParticles.PARTICLES.register(bus);
        bus.addListener(SmashHeavyAttack::register);
        PacketHandler.register();
        WukongSkills.registerSkills();

        IEventBus fg_bus = MinecraftForge.EVENT_BUS;
        fg_bus.addListener(WukongSkills::BuildSkills);
        fg_bus.addListener(WukongAnimations::onPlayerTick);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
