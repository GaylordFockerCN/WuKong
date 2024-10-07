package com.p1nero.wukong.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.item.JinGuBang;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class JinGuBangRenderer extends GeoItemRenderer<JinGuBang> {
    public JinGuBangRenderer() {
        super(new AnimatedGeoModel<>() {
            @Override
            public ResourceLocation getModelLocation(JinGuBang jinGuBang) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "geo/item/jingubang.geo.json");
            }

            @Override
            public ResourceLocation getTextureLocation(JinGuBang jinGuBang) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "textures/item/jingubang.png");
            }

            @Override
            public ResourceLocation getAnimationFileLocation(JinGuBang jinGuBang) {
                return new ResourceLocation(WukongMoveset.MOD_ID, "animations/item/jingubang.animation.json");
            }
        });
    }

    @Override
    public void render(GeoModel model, JinGuBang jinGuBang, float partialTicks, RenderType type, PoseStack matrixStackIn, MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(model, jinGuBang, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, 0xf000ff, packedOverlayIn, red, green, blue, alpha);
    }
}
