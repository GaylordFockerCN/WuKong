package com.p1nero.wukong.epicfight.weapon;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.StaffStyle;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongWeaponCapabilityPresets {

    public static final Function<Item, CapabilityItem.Builder> STAFF = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.STAFF)
            .styleProvider((livingEntityPatch) -> {
                if(livingEntityPatch instanceof ServerPlayerPatch playerPatch){
                    if(playerPatch.getSkill(WukongSkillSlots.STAFF_STYLE).getSkill() instanceof StaffStyle style){
                        return style.getStyle();
                    }
                }
                return WukongStyles.WUKONG_COMMON;
            }).collider(WukongColliders.STAFF)
            .hitSound(EpicFightSounds.BLUNT_HIT)
            .hitParticle(EpicFightParticles.HIT_BLUNT.get())
            .canBePlacedOffhand(false)
            .comboCancel((style) -> false)
            //学棍势之前只是普通滴棍子（可以改成劈棍默认也行
            .newStyleCombo(WukongStyles.WUKONG_COMMON,
                    Animations.SPEAR_ONEHAND_AUTO)
            .innateSkill(WukongStyles.WUKONG_COMMON, (itemstack) -> WukongSkills.CHOP_CHARGED)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.IDLE,
                    Animations.BIPED_RUN_LONGSWORD)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.WALK,
                    Animations.LONGSWORD_GUARD)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.CHASE,
                    Animations.BIPED_RUN_LONGSWORD)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.RUN,
                    Animations.BIPED_RUN_LONGSWORD)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.SWIM,
                    Animations.LONGSWORD_GUARD)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.BLOCK,
                    Animations.LONGSWORD_GUARD)
            //劈棍
            .newStyleCombo(WukongStyles.CHOP,
                    WukongAnimations.CHOP_AUTO1,
                    WukongAnimations.CHOP_AUTO2,
                    WukongAnimations.CHOP_AUTO3,
                    WukongAnimations.CHOP_AUTO4,
                    WukongAnimations.CHOP_AUTO5,
                    WukongAnimations.CHOP_AUTO5,//冲刺
                    WukongAnimations.CHOP_AUTO1)//空中
            .innateSkill(WukongStyles.CHOP, (itemstack) -> WukongSkills.CHOP_CHARGED)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.IDLE,
                    WukongAnimations.CHOP_IDLE)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.WALK,
                    WukongAnimations.CHOP_WALK)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.CHASE,
                    WukongAnimations.CHOP_RUN)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.RUN,
                    WukongAnimations.CHOP_RUN)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.SWIM,
                    WukongAnimations.CHOP_WALK)
            //戳棍
            .newStyleCombo(WukongStyles.POKE,
                    WukongAnimations.POKE_AUTO1,
                    WukongAnimations.POKE_AUTO2,
                    WukongAnimations.POKE_AUTO3,
                    WukongAnimations.POKE_AUTO4,
                    WukongAnimations.POKE_AUTO5,
                    WukongAnimations.POKE_AUTO5,//冲刺
                    WukongAnimations.POKE_AUTO1)//空中
            .innateSkill(WukongStyles.POKE, (itemstack) -> WukongSkills.POKE_CHARGED)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.IDLE,
                    WukongAnimations.POKE_IDLE)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.WALK,
                    WukongAnimations.POKE_WALK)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.CHASE,
                    WukongAnimations.POKE_RUN)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.RUN,
                    WukongAnimations.POKE_RUN)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.SWIM,
                    WukongAnimations.POKE_WALK)
            //立棍
                .newStyleCombo(WukongStyles.STAND,
                    WukongAnimations.STAND_AUTO1,
                    WukongAnimations.STAND_AUTO2,
                    WukongAnimations.STAND_AUTO3,
                    WukongAnimations.STAND_AUTO4,
                    WukongAnimations.STAND_AUTO5,
                    WukongAnimations.STAND_AUTO5,//冲刺
                    WukongAnimations.STAND_AUTO1)//空中
            .innateSkill(WukongStyles.STAND, (itemstack) -> WukongSkills.STAND_CHARGED)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.IDLE,
                    WukongAnimations.STAND_IDLE)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.WALK,
                    WukongAnimations.STAND_WALK)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.CHASE,
                    WukongAnimations.STAND_RUN)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.RUN,
                    WukongAnimations.STAND_RUN)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.SWIM,
                    WukongAnimations.STAND_WALK)
            ;

    @SubscribeEvent
    public static void register(WeaponCapabilityPresetRegistryEvent event) {
        event.getTypeEntry().put("staff", STAFF);
    }

}
