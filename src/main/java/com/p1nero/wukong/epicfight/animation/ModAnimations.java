package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.WukongMoveset;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModAnimations {

    //劈棍
    public static StaticAnimation CHOP_IDLE;
    public static StaticAnimation CHOP_WALK;
    public static StaticAnimation CHOP_RUN;
    //轻击 1~5
    public static StaticAnimation CHOP_AUTO1;
    public static StaticAnimation CHOP_AUTO2;
    public static StaticAnimation CHOP_AUTO3;
    public static StaticAnimation CHOP_AUTO4;
    public static StaticAnimation CHOP_AUTO5;
    //衍生 1 2
    public static StaticAnimation CHOP_DERIVE1;
    public static StaticAnimation CHOP_DERIVE2;
    //不同星级的重击
    public static StaticAnimation CHOP_CHARGED0;
    public static StaticAnimation CHOP_CHARGED1;
    public static StaticAnimation CHOP_CHARGED2;
    public static StaticAnimation CHOP_CHARGED3;
    public static StaticAnimation CHOP_CHARGED4;

    //戳棍
    public static StaticAnimation POKE_IDLE;
    public static StaticAnimation POKE_WALK;
    public static StaticAnimation POKE_RUN;
    //轻击 1~5
    public static StaticAnimation POKE_AUTO1;
    public static StaticAnimation POKE_AUTO2;
    public static StaticAnimation POKE_AUTO3;
    public static StaticAnimation POKE_AUTO4;
    public static StaticAnimation POKE_AUTO5;
    //衍生 1 2
    public static StaticAnimation POKE_DERIVE1;
    public static StaticAnimation POKE_DERIVE2;
    //不同星级的重击
    public static StaticAnimation POKE_CHARGED0;
    public static StaticAnimation POKE_CHARGED1;
    public static StaticAnimation POKE_CHARGED2;
    public static StaticAnimation POKE_CHARGED3;
    public static StaticAnimation POKE_CHARGED4;

    //立棍
    public static StaticAnimation STAND_IDLE;
    public static StaticAnimation STAND_WALK;
    public static StaticAnimation STAND_RUN;
    //轻击 1~5
    public static StaticAnimation STAND_AUTO1;
    public static StaticAnimation STAND_AUTO2;
    public static StaticAnimation STAND_AUTO3;
    public static StaticAnimation STAND_AUTO4;
    public static StaticAnimation STAND_AUTO5;
    //衍生 1 2
    public static StaticAnimation STAND_DERIVE1;
    public static StaticAnimation STAND_DERIVE2;
    //不同星级的重击
    public static StaticAnimation STAND_CHARGED0;
    public static StaticAnimation STAND_CHARGED1;
    public static StaticAnimation STAND_CHARGED2;
    public static StaticAnimation STAND_CHARGED3;
    public static StaticAnimation STAND_CHARGED4;

    @SubscribeEvent
    public static void registerAnimations(AnimationRegistryEvent event) {
        event.getRegistryMap().put(WukongMoveset.MOD_ID, ModAnimations::build);
    }

    private static void build() {
        HumanoidArmature biped = Armatures.BIPED;
    }

}
