package com.p1nero.wukong.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 缩放的备案，在yesman修复bug之前这玩意儿真好使
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    public void render(ItemStack itemStack, ItemDisplayContext p_270188_, boolean p_115146_, PoseStack poseStack, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_, CallbackInfo ci){
        CompoundTag tag = itemStack.getOrCreateTag();
        if(tag.getBoolean("WK_shouldScaleItem")){
            poseStack.scale(tag.getFloat("WK_XScale"), tag.getFloat("WK_YScale"), tag.getFloat("WK_ZScale"));
        }
    }

}
