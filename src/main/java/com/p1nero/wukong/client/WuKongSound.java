package com.p1nero.wukong.client;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class WuKongSound {

    private static SoundEvent createEvent(String sound) {
        ResourceLocation name = new ResourceLocation(WukongMoveset.MOD_ID, sound);
        return new SoundEvent(name).setRegistryName(name);
    }


}
