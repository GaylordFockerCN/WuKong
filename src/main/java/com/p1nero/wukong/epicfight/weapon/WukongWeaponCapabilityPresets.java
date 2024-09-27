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
                return WukongStyles.SMASH;//默认劈棍
            }).collider(WukongColliders.WK_STAFF)
            .hitSound(EpicFightSounds.BLUNT_HIT)
            .hitParticle(EpicFightParticles.HIT_BLUNT.get())
            .canBePlacedOffhand(false)
            .comboCancel((style) -> false)
            .passiveSkill(WukongSkills.STAFF_SPIN)
            //劈棍
            .newStyleCombo(WukongStyles.SMASH,
                    WukongAnimations.STAFF_AUTO1,
                    WukongAnimations.STAFF_AUTO2,
                    WukongAnimations.STAFF_AUTO3,
                    WukongAnimations.STAFF_AUTO4,
                    WukongAnimations.STAFF_AUTO5,
                    WukongAnimations.STAFF_AUTO1,
                    Animations.SPEAR_TWOHAND_AIR_SLASH)
            .innateSkill(WukongStyles.SMASH, (itemstack) -> WukongSkills.SMASH_HEAVY_ATTACK)
            .livingMotionModifier(WukongStyles.SMASH,
                    LivingMotions.IDLE,
                    WukongAnimations.IDLE)
//            .livingMotionModifier(WukongStyles.CHOP,
//                    LivingMotions.WALK,
//                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.SMASH,
                    LivingMotions.CHASE,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.SMASH,
                    LivingMotions.RUN,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.SMASH,
                    LivingMotions.SWIM,
                    WukongAnimations.WALK)
//            .livingMotionModifier(WukongStyles.CHOP,
//                    LivingMotions.JUMP,
//                    WukongAnimations.JUMP)
            //戳棍
            .newStyleCombo(WukongStyles.THRUST,
                    WukongAnimations.STAFF_AUTO1,
                    WukongAnimations.STAFF_AUTO2,
                    WukongAnimations.STAFF_AUTO3,
                    WukongAnimations.STAFF_AUTO4,
                    WukongAnimations.STAFF_AUTO5,
                    WukongAnimations.STAFF_AUTO1,
                    Animations.SPEAR_TWOHAND_AIR_SLASH)
            .innateSkill(WukongStyles.THRUST, (itemstack) -> WukongSkills.THRUST_HEAVY_ATTACK)
            .livingMotionModifier(WukongStyles.THRUST,
                    LivingMotions.IDLE,
                    WukongAnimations.IDLE)
//            .livingMotionModifier(WukongStyles.THRUST,
//                    LivingMotions.WALK,
//                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.THRUST,
                    LivingMotions.CHASE,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.THRUST,
                    LivingMotions.RUN,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.THRUST,
                    LivingMotions.SWIM,
                    WukongAnimations.WALK)
//            .livingMotionModifier(WukongStyles.THRUST,
//                    LivingMotions.JUMP,
//                    WukongAnimations.JUMP)
            //立棍
            .newStyleCombo(WukongStyles.PILLAR,
                    WukongAnimations.STAFF_AUTO1,
                    WukongAnimations.STAFF_AUTO2,
                    WukongAnimations.STAFF_AUTO3,
                    WukongAnimations.STAFF_AUTO4,
                    WukongAnimations.STAFF_AUTO5,
                    WukongAnimations.STAFF_AUTO1,
                    WukongAnimations.STAFF_AUTO1)
            .innateSkill(WukongStyles.PILLAR, (itemstack) -> WukongSkills.PILLAR_HEAVY_ATTACK)
            .livingMotionModifier(WukongStyles.PILLAR,
                    LivingMotions.IDLE,
                    WukongAnimations.IDLE)
//            .livingMotionModifier(WukongStyles.STAND,
//                    LivingMotions.WALK,
//                    WukongAnimations.WALK)
            .livingMotionModifier(WukongStyles.PILLAR,
                    LivingMotions.CHASE,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.PILLAR,
                    LivingMotions.RUN,
                    WukongAnimations.RUN)
            .livingMotionModifier(WukongStyles.PILLAR,
                    LivingMotions.SWIM,
                    WukongAnimations.WALK)
//            .livingMotionModifier(WukongStyles.STAND,
//                    LivingMotions.JUMP,
//                    WukongAnimations.JUMP)
            ;

    public static final Function<Item, CapabilityItem.Builder> CHOP_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.SMASH)
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)
                    //劈棍
                    .newStyleCombo(WukongStyles.SMASH,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO1)//空中
                    .innateSkill(WukongStyles.SMASH, (itemstack) -> WukongSkills.SMASH_HEAVY_ATTACK)
                    .livingMotionModifier(WukongStyles.SMASH,
                            LivingMotions.IDLE,
                            WukongAnimations.IDLE)
//                    .livingMotionModifier(WukongStyles.SMASH,
//                            LivingMotions.WALK,
//                            WukongAnimations.SMASH_WALK)
                    .livingMotionModifier(WukongStyles.SMASH,
                            LivingMotions.CHASE,
                            WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.SMASH,
                            LivingMotions.RUN,
                            WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.SMASH,
                            LivingMotions.SWIM,
                            WukongAnimations.WALK);

    public static final Function<Item, CapabilityItem.Builder> THRUST_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.THRUST)
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)

                    //戳棍
                    .newStyleCombo(WukongStyles.THRUST,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO1)//空中
                    .innateSkill(WukongStyles.THRUST, (itemstack) -> WukongSkills.THRUST_HEAVY_ATTACK)
                    .livingMotionModifier(WukongStyles.THRUST,
                            LivingMotions.IDLE,
                            WukongAnimations.IDLE)
                    .livingMotionModifier(WukongStyles.THRUST,
                            LivingMotions.WALK,
                            WukongAnimations.WALK)
                    .livingMotionModifier(WukongStyles.THRUST,
                            LivingMotions.CHASE,
                            WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.THRUST,
                            LivingMotions.RUN,
                            WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.THRUST,
                            LivingMotions.SWIM,
                            WukongAnimations.WALK);

    public static final Function<Item, CapabilityItem.Builder> STAND_ONLY = (item) ->
            (CapabilityItem.Builder) WeaponCapability.builder().category(WukongWeaponCategories.WK_STAFF)
                    .styleProvider((entityPatch) -> WukongStyles.PILLAR)
                    .collider(WukongColliders.WK_STAFF)
                    .hitSound(EpicFightSounds.BLUNT_HIT)
                    .hitParticle(EpicFightParticles.HIT_BLUNT.get())
                    .canBePlacedOffhand(false)
                    .comboCancel((style) -> false)

                    //立棍
                    .newStyleCombo(WukongStyles.PILLAR,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO2,
                            WukongAnimations.STAFF_AUTO3,
                            WukongAnimations.STAFF_AUTO4,
                            WukongAnimations.STAFF_AUTO5,
                            WukongAnimations.STAFF_AUTO1,
                            WukongAnimations.STAFF_AUTO1)//空中
                    .innateSkill(WukongStyles.PILLAR, (itemstack) -> WukongSkills.PILLAR_HEAVY_ATTACK)
                    .livingMotionModifier(WukongStyles.PILLAR,
                            LivingMotions.IDLE,
                            WukongAnimations.IDLE)
                    .livingMotionModifier(WukongStyles.PILLAR,
                            LivingMotions.WALK,
                            WukongAnimations.WALK)
                    .livingMotionModifier(WukongStyles.PILLAR,
                            LivingMotions.CHASE,
                            WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.PILLAR,
                            LivingMotions.RUN,
                            WukongAnimations.RUN)
                    .livingMotionModifier(WukongStyles.PILLAR,
                            LivingMotions.SWIM,
                            WukongAnimations.WALK)
            ;


    @SubscribeEvent
    public static void register(WeaponCapabilityPresetRegistryEvent event) {
        event.getTypeEntry().put("wk_staff", STAFF);
        event.getTypeEntry().put("chop_only", CHOP_ONLY);
        event.getTypeEntry().put("thrust_only", THRUST_ONLY);
        event.getTypeEntry().put("stand_only", STAND_ONLY);
    }

}
