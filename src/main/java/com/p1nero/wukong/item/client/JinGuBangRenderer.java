package com.p1nero.wukong.item.client;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.item.JinGuBang;
import net.minecraft.resources.ResourceLocation;
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
}
