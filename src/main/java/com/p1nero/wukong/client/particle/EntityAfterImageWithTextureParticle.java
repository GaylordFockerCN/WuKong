//package com.p1nero.wukong.client.particle;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//import net.minecraft.client.Camera;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.client.particle.Particle;
//import net.minecraft.client.particle.ParticleProvider;
//import net.minecraft.client.particle.ParticleRenderType;
//import net.minecraft.core.particles.SimpleParticleType;
//import net.minecraft.world.entity.Entity;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import org.jetbrains.annotations.NotNull;
//import yesman.epicfight.api.client.model.AnimatedMesh;
//import yesman.epicfight.api.model.Armature;
//import yesman.epicfight.api.utils.math.OpenMatrix4f;
//import yesman.epicfight.client.ClientEngine;
//import yesman.epicfight.client.particle.CustomModelParticle;
//import yesman.epicfight.client.particle.EpicFightParticleRenderTypes;
//import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
//import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
//import yesman.epicfight.world.capabilities.EpicFightCapabilities;
//import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
//
//public class EntityAfterImageWithTextureParticle extends CustomModelParticle<AnimatedMesh> {
//    protected final LivingEntityPatch<?> entityPatch;
//    protected final PatchedLivingEntityRenderer renderer;
//    protected final Armature armature;
//    protected PoseStack poseStack;
//    protected OpenMatrix4f[] matrices;
//    protected float alphaO;
//
//    public EntityAfterImageWithTextureParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, AnimatedMesh particleMesh, LivingEntityPatch<?> entityPatch, PatchedLivingEntityRenderer renderer) {
//        super(level, x, y, z, xd, yd, zd, particleMesh);
//        this.lifetime = 20;
//        this.rCol = 1.0F;
//        this.gCol = 1.0F;
//        this.bCol = 1.0F;
//        this.alphaO = 0.3F;
//        this.alpha = 0.3F;
//        this.entityPatch = entityPatch;
//        this.renderer = renderer;
//        armature = entityPatch.getArmature();
//        PoseStack poseStack = new PoseStack();
//        renderer.mulPoseStack(poseStack, armature, entityPatch.getOriginal(), entityPatch, 1.0F);
//        matrices = renderer.getPoseMatrices(entityPatch, armature, 1.0F);
//        for(int i = 0; i < matrices.length; ++i) {
//            matrices[i] = OpenMatrix4f.mul(matrices[i], armature.searchJointById(i).getToOrigin(), null);
//        }
//    }
//
//    public void tick() {
//        super.tick();
//        this.alphaO = this.alpha;
//        this.alpha = (float)(this.lifetime - this.age) / (float)this.lifetime * 0.8F;
//    }
//
//    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
//        float alpha = this.alphaO + (this.alpha - this.alphaO) * partialTicks;
////        this.renderer.render(entityPatch.getOriginal(), entityPatch, (LivingEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entityPatch.getOriginal()), , poseStack, 1, 1);
//    }
//
//    public @NotNull ParticleRenderType getRenderType() {
//        return EpicFightParticleRenderTypes.TRANSLUCENT;
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static class Provider implements ParticleProvider<SimpleParticleType> {
//        public Provider() {
//        }
//
//        @Override
//        public Particle createParticle(@NotNull SimpleParticleType typeIn, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
//            Entity entity = level.getEntity((int)Double.doubleToLongBits(xSpeed));
//            LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>) EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
//            if (entityPatch != null && ClientEngine.getInstance().renderEngine.hasRendererFor(entityPatch.getOriginal())) {
//                PatchedLivingEntityRenderer renderer = (PatchedLivingEntityRenderer) ClientEngine.getInstance().renderEngine.getEntityRenderer(entityPatch.getOriginal());
//                EntityAfterImageWithTextureParticle particle = new EntityAfterImageWithTextureParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, ClientEngine.getInstance().renderEngine.getEntityRenderer(entityPatch.getOriginal()).getMesh(entityPatch), entityPatch, renderer);
//                return particle;
//            } else {
//                return null;
//            }
//        }
//    }
//}
