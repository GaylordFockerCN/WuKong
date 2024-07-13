package com.p1nero.wukong.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.skill.ChargedAttack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = BattleModeGui.class, remap = false)
public class BattleModeGuiMixin {

    @Unique
    private static final ResourceLocation BACK_GROUND = new ResourceLocation(WukongMoveset.MOD_ID,"textures/item/loong_roar/blade.png");

    /**
     * 把颜色去掉
     */
    @Inject(method = "drawWeaponInnateIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", shift = At.Shift.AFTER))
    private void modifyColor(LocalPlayerPatch playerPatch, SkillContainer container, PoseStack matStack, float partialTicks, CallbackInfo ci){
        if(container.getSkill() instanceof ChargedAttack){
            RenderSystem.setShaderColor(1,1,1,1);
        }
    }

    @Inject(method = "drawWeaponInnateIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", shift = At.Shift.AFTER))
    private void modifyTexture(LocalPlayerPatch playerPatch, SkillContainer container, PoseStack matStack, float partialTicks, CallbackInfo ci){
        if(container.getSkill() instanceof ChargedAttack){
            if(container.getSkill() instanceof ChargedAttack){
//            RenderSystem.setShaderTexture(0, BACK_GROUND);
                int stack = container.getStack();
                ItemStack itemStack = container.getExecuter().getOriginal().getMainHandItem();
                int style = 0;
                if(EpicFightCapabilities.getItemStackCapability(itemStack).getStyle(container.getExecuter()) instanceof WukongStyles wukongStyle){
                    style = wukongStyle.ordinal();
                }

                drawBackground();

                boolean fullstack = playerPatch.getOriginal().isCreative() || container.isFull();
                float cooldownRatio = !fullstack && !container.isActivated() ? container.getResource(partialTicks) : 1.0F;
                RenderSystem.setShaderTexture(0, new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/" + stack + "_" + ((int) Math.ceil(cooldownRatio * 9)) + ".png"));
            }
        }
    }

    private static void drawBackground(){
        RenderSystem.setShaderTexture(0, BACK_GROUND);

    }


    public void drawTexturedModalRectFixCoord(BattleModeGui ingameGui, Matrix4f matrix, float xCoord, float yCoord, int minU, int minV, int maxU, int maxV, int col) {
        drawTexturedModalRectFixCoord(matrix, xCoord, yCoord, (float)maxU, (float)maxV, (float)ingameGui.getBlitOffset(), (float)minU, (float)minV, (float)maxU, (float)maxV, col);
    }

    public static void drawTexturedModalRectFixCoord(Matrix4f matrix, float minX, float minY, float maxX, float maxY, float z, float minU, float minV, float maxU, float maxV, int col) {
        float cor = 0.00390625F;
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, minX, minY + maxX, z).uv(minU * cor, (minV + maxV) * cor).color(col).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY + maxX, z).uv((minU + maxU) * cor, (minV + maxV) * cor).color(col).endVertex();
        bufferbuilder.vertex(matrix, minX + maxY, minY, z).uv((minU + maxU) * cor, minV * cor).color(col).endVertex();
        bufferbuilder.vertex(matrix, minX, minY, z).uv(minU * cor, minV * cor).color(col).endVertex();
        tesselator.end();
    }


//    @ModifyArg(method = "drawWeaponInnateIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"), index = 1)
//    private ResourceLocation modifyArg(ResourceLocation resourceLocation) {
//        AtomicReference<ResourceLocation> toReturn = new AtomicReference<>(resourceLocation);
//        if(Minecraft.getInstance().player != null){
//            Minecraft.getInstance().player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
//                if(entityPatch instanceof LocalPlayerPatch playerPatch){
//                    SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
//                    if(container.getSkill() instanceof ChargedAttack){
////            RenderSystem.setShaderTexture(0, BACK_GROUND);
//                        int stack = container.getStack();
//                        ItemStack itemStack = container.getExecuter().getOriginal().getMainHandItem();
//                        int style = 0;
//                        if(EpicFightCapabilities.getItemStackCapability(itemStack).getStyle(container.getExecuter()) instanceof WukongStyles wukongStyle){
//                            style = wukongStyle.ordinal();
//                        }
//                        int percent;
//                        toReturn.set(new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/" + style + "_" + stack + ".png"));
//                    }else {
//                        toReturn.set(container.getSkill().getSkillTexture());
//                    }
//                }
//            });
//        }
//        return toReturn.get();
//    }

    /**
     * 把字去掉
     */
    @ModifyArg(method = "drawWeaponInnateIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"), index = 1)
    private String modifyArg(String s) {
        if(wukong$checkIsWukongSkill()){
            return "";
        }
        return s;
    }

    @Unique
    private boolean wukong$checkIsWukongSkill(){
        AtomicBoolean isWukongSkill = new AtomicBoolean(false);
        if(Minecraft.getInstance().player != null){
            Minecraft.getInstance().player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
                if(entityPatch instanceof LocalPlayerPatch playerPatch){
                    SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                    if(container.getSkill() instanceof ChargedAttack){
                        isWukongSkill.set(true);
                    }
                }
            });
        }
        return isWukongSkill.get();
    }

}
