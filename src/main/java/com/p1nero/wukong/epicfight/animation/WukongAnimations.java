package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.skill.HeavyAttack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongAnimations {
    //棍花
    public static StaticAnimation STAFF_FLOWER;

    //轻击 1~5
    public static StaticAnimation STAFF_AUTO1;
    public static StaticAnimation STAFF_AUTO2;
    public static StaticAnimation STAFF_AUTO3;
    public static StaticAnimation STAFF_AUTO4;
    public static StaticAnimation STAFF_AUTO5;
    public static StaticAnimation[] STATIC_ANIMATIONS;

    //劈棍
    public static StaticAnimation CHOP_IDLE;
    public static StaticAnimation CHOP_WALK;
    public static StaticAnimation CHOP_RUN;

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
    //衍生 1 2
    public static StaticAnimation POKE_DERIVE1;
    public static StaticAnimation POKE_DERIVE2;
    //不同星级的重击
    public static StaticAnimation POKE_PRE;
    public static StaticAnimation POKE_CHARGING;
    public static StaticAnimation POKE_CHARGED;

    //立棍
    public static StaticAnimation STAND_IDLE;
    public static StaticAnimation STAND_WALK;
    public static StaticAnimation STAND_RUN;
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
        event.getRegistryMap().put(WukongMoveset.MOD_ID, WukongAnimations::build);
    }

    private static void build() {
        HumanoidArmature biped = Armatures.BIPED;
        POKE_CHARGED = new StaticAnimation(false, "biped/poke/poke_charged", biped);
        POKE_CHARGING = new StaticAnimation(false, "biped/poke/poke_charging", biped)
                .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                .addStateRemoveOld(EntityState.INACTION, true);
        POKE_PRE = new StaticAnimation(false, "biped/poke/poke_pre", biped)
                .addStateRemoveOld(EntityState.MOVEMENT_LOCKED, true)
                .addStateRemoveOld(EntityState.INACTION, true).addEvents(AnimationEvent.TimeStampedEvent.create(0.25F, ((livingEntityPatch, staticAnimation, objects) -> {
                    if(livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                        serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setDataSync(HeavyAttack.IS_CHARGING_PRE, false, serverPlayerPatch.getOriginal());
                    }
                }), AnimationEvent.Side.SERVER));

    }

}
