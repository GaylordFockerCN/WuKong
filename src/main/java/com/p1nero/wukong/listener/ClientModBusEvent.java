package com.p1nero.wukong.listener;


import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.particle.EntityAfterImageWithTextureParticle;
import com.p1nero.wukong.client.particle.WuKongParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid= WukongMoveset.MOD_ID, value=Dist.CLIENT, bus= Mod.EventBusSubscriber.Bus.MOD)
public class ClientModBusEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onParticleRegistry(final ParticleFactoryRegisterEvent event) {
        ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
        particleEngine.register(WuKongParticles.ENTITY_AFTER_IMAGE_WITH_TEXTURE.get(), new EntityAfterImageWithTextureParticle.Provider());
    }

}
