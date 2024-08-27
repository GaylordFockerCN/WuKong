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
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponCapability;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongWeaponCapabilityPresets {

    public static final Function<Item, CapabilityItem.Builder> STAFF = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
            .styleProvider((livingEntityPatch) -> {
                if(livingEntityPatch instanceof PlayerPatch<?> playerPatch){
                    SkillContainer container = playerPatch.getSkill(WukongSkillSlots.STAFF_STYLE);
                    if(container.getSkill() instanceof StaffStyle style){
                        return style.getStyle(container);
                    }
                }
                return WukongStyles.WUKONG_COMMON;
            }).collider(WukongColliders.WK_STAFF)
            .hitSound(EpicFightSounds.BLUNT_HIT)
            .hitParticle(EpicFightParticles.HIT_BLUNT.get())
            .canBePlacedOffhand(false)
            .comboCancel((style) -> false)
            .passiveSkill(WukongSkills.STAFF_FLOWER)
            //学棍势之前第五段重击就是技能
            .newStyleCombo(WukongStyles.WUKONG_COMMON,
                    WukongAnimations.STAFF_AUTO1,
                    WukongAnimations.STAFF_AUTO2,
                    WukongAnimations.STAFF_AUTO3,
                    WukongAnimations.STAFF_AUTO4,
                    Animations.SPEAR_DASH,
                    Animations.SPEAR_TWOHAND_AIR_SLASH)
            .innateSkill(WukongStyles.WUKONG_COMMON, (itemstack) -> WukongSkills.COMMON)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.IDLE,
                    WukongAnimations.IDLE)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.WALK,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.CHASE,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.RUN,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.SWIM,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.WUKONG_COMMON,
                    LivingMotions.JUMP,
                    WukongAnimations.JUMP)

            //劈棍
            .newStyleCombo(WukongStyles.CHOP,
                    WukongAnimations.STAFF_AUTO1,
                    WukongAnimations.STAFF_AUTO2,
                    WukongAnimations.STAFF_AUTO3,
                    WukongAnimations.STAFF_AUTO4,
                    WukongAnimations.STAFF_AUTO5,
//                    WukongAnimations.CHOP_AUTO5,//冲刺
//                    WukongAnimations.STAFF_AUTO1)//空中

                    Animations.SPEAR_DASH,
                    Animations.SPEAR_TWOHAND_AIR_SLASH)
            .innateSkill(WukongStyles.CHOP, (itemstack) -> WukongSkills.CHOP_CHARGED)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.IDLE,
                    WukongAnimations.IDLE)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.WALK,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.CHASE,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.RUN,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.SWIM,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.CHOP,
                    LivingMotions.JUMP,
                    WukongAnimations.JUMP)
            //戳棍
            .newStyleCombo(WukongStyles.POKE,
                    WukongAnimations.STAFF_AUTO1,
                    WukongAnimations.STAFF_AUTO2,
                    WukongAnimations.STAFF_AUTO3,
                    WukongAnimations.STAFF_AUTO4,
                    WukongAnimations.STAFF_AUTO5,
//                    WukongAnimations.STAFF_AUTO5,//冲刺
//                    WukongAnimations.STAFF_AUTO1)//空中
                    Animations.SPEAR_ONEHAND_AUTO,
                    Animations.SPEAR_TWOHAND_AUTO1,
                    Animations.SPEAR_TWOHAND_AUTO2,
                    Animations.SPEAR_DASH,
                    Animations.SPEAR_TWOHAND_AIR_SLASH)
            .innateSkill(WukongStyles.POKE, (itemstack) -> WukongSkills.POKE_CHARGED)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.IDLE,
                    WukongAnimations.IDLE)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.WALK,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.CHASE,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.RUN,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.SWIM,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.POKE,
                    LivingMotions.JUMP,
                    WukongAnimations.JUMP)
            //立棍
                .newStyleCombo(WukongStyles.STAND,
                        WukongAnimations.STAFF_AUTO1,
                        WukongAnimations.STAFF_AUTO2,
                        WukongAnimations.STAFF_AUTO3,
                        WukongAnimations.STAFF_AUTO4,
                        WukongAnimations.STAFF_AUTO5,
                        WukongAnimations.STAFF_AUTO5,//冲刺
                        WukongAnimations.STAFF_AUTO1)//空中
            .innateSkill(WukongStyles.STAND, (itemstack) -> WukongSkills.STAND_CHARGED)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.IDLE,
                    WukongAnimations.IDLE)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.WALK,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.CHASE,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.RUN,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.SWIM,
                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.STAND,
                    LivingMotions.JUMP,
                    WukongAnimations.JUMP)
            ;

    public static final Function<Item, CapabilityItem.Builder> CHOP_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.CHOP)
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)
                    //劈棍
                    .newStyleCombo(WukongStyles.CHOP,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO5,//冲刺
                            WukongAnimations.STAFF_AUTO1)//空中
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
                            WukongAnimations.CHOP_WALK);

    public static final Function<Item, CapabilityItem.Builder> POKE_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.POKE)
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)

                    //戳棍
                    .newStyleCombo(WukongStyles.POKE,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO5,//冲刺
                            WukongAnimations.STAFF_AUTO1)//空中
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
                            WukongAnimations.POKE_WALK);

    public static final Function<Item, CapabilityItem.Builder> STAND_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.STAND)
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)

                    //立棍
                    .newStyleCombo(WukongStyles.STAND,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO5,//冲刺
                            WukongAnimations.STAFF_AUTO1)//空中
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
        event.getTypeEntry().put("wk_staff", STAFF);
        event.getTypeEntry().put("chop_only", CHOP_ONLY);
        event.getTypeEntry().put("poke_only", POKE_ONLY);
        event.getTypeEntry().put("stand_only", STAND_ONLY);
    }

}
