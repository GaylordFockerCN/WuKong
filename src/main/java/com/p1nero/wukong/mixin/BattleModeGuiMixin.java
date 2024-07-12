package com.p1nero.wukong.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.skill.ChargedAttack;
import net.minecraft.client.Minecraft;
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
    private static final ResourceLocation BACK_GROUND = new ResourceLocation("");

    /**
     * 把颜色去掉
     */
    @Inject(method = "drawWeaponInnateIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V", shift = At.Shift.AFTER))
    private void modifyColor(LocalPlayerPatch playerPatch, SkillContainer container, PoseStack matStack, float partialTicks, CallbackInfo ci){
        if(container.getSkill() instanceof ChargedAttack){
            RenderSystem.setShaderColor(1,1,1,1);
        }
    }

    @ModifyArg(method = "drawWeaponInnateIcon", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"), index = 1)
    private ResourceLocation modifyArg(ResourceLocation resourceLocation) {
        AtomicReference<ResourceLocation> toReturn = new AtomicReference<>(resourceLocation);
        if(Minecraft.getInstance().player != null){
            Minecraft.getInstance().player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
                if(entityPatch instanceof LocalPlayerPatch playerPatch){
                    SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                    if(container.getSkill() instanceof ChargedAttack){
//            RenderSystem.setShaderTexture(0, BACK_GROUND);
                        int stack = container.getStack();
                        ItemStack itemStack = container.getExecuter().getOriginal().getMainHandItem();
                        int style = 0;
                        if(EpicFightCapabilities.getItemStackCapability(itemStack).getStyle(container.getExecuter()) instanceof WukongStyles wukongStyle){
                            style = wukongStyle.ordinal();
                        }
                        toReturn.set(new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/" + style + "_" + stack + ".png"));
                    }else {
                        toReturn.set(container.getSkill().getSkillTexture());
                    }
                }
            });
        }
        return toReturn.get();
    }

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
