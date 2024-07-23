package com.p1nero.wukong.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.epicfight.skill.HeavyAttack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;

@Mixin(value = BattleModeGui.class, remap = false)
public class BattleModeGuiMixin {

    /**
     * 取消绘制技能图标，我要自己画！
     */
    @Inject(method = "drawWeaponInnateIcon", at = @At(value = "HEAD"), cancellable = true)
    private void modifyTexture(LocalPlayerPatch playerPatch, SkillContainer container, PoseStack matStack, float partialTicks, CallbackInfo ci){
        if(container.getSkill() instanceof HeavyAttack){
            ci.cancel();
        }
    }


}