package com.p1nero.wukong.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import yesman.epicfight.world.item.WeaponItem;

import java.util.function.Consumer;

public class JinGuBang extends WeaponItem implements IAnimatable {
    public final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public JinGuBang(Tier tier, int damageIn, float speedIn, Properties builder) {
        super(tier, damageIn, speedIn, builder);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
//            private ExampleItemRenderer renderer = null;
            // Don't instantiate until ready. This prevents race conditions breaking things
//            @Override public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
//                if (this.renderer == null)
//                    this.renderer = new ExampleItemRenderer();
//
//                return renderer;
//            }
        });
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(this, "stackController", 10, event -> PlayState.CONTINUE));
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
