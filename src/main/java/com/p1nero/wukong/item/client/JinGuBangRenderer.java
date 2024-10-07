package com.p1nero.wukong.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.item.JinGuBang;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class JinGuBangRenderer extends GeoItemRenderer<JinGuBang> {
    public JinGuBangRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(WukongMoveset.MOD_ID, "jingubang")));
    }

    @Override
    public void preRender(PoseStack poseStack, JinGuBang jinGuBang, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, jinGuBang, model, bufferSource, buffer, isReRender, partialTick, 0xf000ff, packedOverlay, red, green, blue, alpha);
    }
}
