package com.p1nero.wukong.client;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WuKongSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WukongMoveset.MOD_ID);

    public static RegistryObject<SoundEvent> PERFECT_DODGE = registerSoundEvent("perfect_dodge");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(WukongMoveset.MOD_ID, name)));
    }

}