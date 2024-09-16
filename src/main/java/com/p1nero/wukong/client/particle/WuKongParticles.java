package com.p1nero.wukong.client.particle;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WuKongParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, WukongMoveset.MOD_ID);
    public static final RegistryObject<SimpleParticleType> ENTITY_AFTER_IMAGE_WITH_TEXTURE = PARTICLES.register("after_image_with_texture", () -> new SimpleParticleType(true));

}
