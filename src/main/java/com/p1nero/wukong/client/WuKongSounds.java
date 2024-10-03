package com.p1nero.wukong.client;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class WuKongSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WukongMoveset.MOD_ID);

    public static RegistryObject<SoundEvent> PERFECT_DODGE = registerSoundEvent("perfect_dodge");
    public static RegistryObject<SoundEvent> STACK1 = registerSoundEvent("stack1");
    public static RegistryObject<SoundEvent> STACK2 = registerSoundEvent("stack2");
    public static RegistryObject<SoundEvent> STACK3 = registerSoundEvent("stack3");
    public static RegistryObject<SoundEvent> STACK4 = registerSoundEvent("stack4");
    public static RegistryObject<SoundEvent> HIT_GROUND = registerSoundEvent("hit_ground");
    public static List<RegistryObject<SoundEvent>> stackSounds = new ArrayList<>();
    static {
        stackSounds.add(STACK1);
        stackSounds.add(STACK2);
        stackSounds.add(STACK3);
        stackSounds.add(STACK4);
    }
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(WukongMoveset.MOD_ID, name)));
    }

}