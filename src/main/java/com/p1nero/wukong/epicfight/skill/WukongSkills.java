package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.wukong.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.wukong.StaffPassive;
import com.p1nero.wukong.epicfight.skill.custom.wukong.StaffStance;
import com.p1nero.wukong.epicfight.skill.custom.wukong.WukongDodgeSkill;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongSkills {
    public static Skill SMASH_STYLE;
    public static Skill THRUST_STYLE;
    public static Skill PILLAR_STYLE;
    public static Skill SMASH_HEAVY_ATTACK;
    public static Skill THRUST_HEAVY_ATTACK;
    public static Skill PILLAR_HEAVY_ATTACK;
    public static Skill STAFF_SPIN;
    public static Skill WUKONG_DODGE;
    public static Skill Ding;//定身术
    public static int getCurrentStack(Player player){
        AtomicInteger stack = new AtomicInteger(0);
        player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent(entityPatch -> {
            if(entityPatch instanceof PlayerPatch<?> patch){
                stack.set(patch.getSkill(SkillSlots.WEAPON_INNATE).getStack());
            }
        });
        return stack.get();
    }
    @SubscribeEvent
    public static void BuildSkills(SkillBuildEvent event){
        SkillBuildEvent.ModRegistryWorker registryWorker = event.createRegistryWorker(WukongMoveset.MOD_ID);
        WUKONG_DODGE = registryWorker.build("dodge",WukongDodgeSkill::new, WukongDodgeSkill.createDodgeBuilder()
                        .setAnimations1(
                                () -> WukongAnimations.DODGE_F1,
                                () -> WukongAnimations.DODGE_B1,
                                () -> WukongAnimations.DODGE_L1,
                                () -> WukongAnimations.DODGE_R1
                        )
                        .setAnimations2(
                                () -> WukongAnimations.DODGE_F2,
                                () -> WukongAnimations.DODGE_B2,
                                () -> WukongAnimations.DODGE_L2,
                                () -> WukongAnimations.DODGE_R2
                        )
                        .setAnimations3(
                                () -> WukongAnimations.DODGE_F3,
                                () -> WukongAnimations.DODGE_B3,
                                () -> WukongAnimations.DODGE_L3,
                                () -> WukongAnimations.DODGE_R3
                        )
                        .setPerfectAnimations(
                                () -> WukongAnimations.DODGE_FP,
                                () -> WukongAnimations.DODGE_BP,
                                () -> WukongAnimations.DODGE_LP,
                                () -> WukongAnimations.DODGE_RP
                        )
        );
        STAFF_SPIN = registryWorker.build("staff_spin", StaffPassive::new, Skill.createBuilder().setResource(Skill.Resource.NONE).setCategory(SkillCategories.WEAPON_PASSIVE));
        SMASH_HEAVY_ATTACK = registryWorker.build("smash_heavy_attack", SmashHeavyAttack::new, SmashHeavyAttack.createChargedAttack()
                .setChargePreAnimation(()-> WukongAnimations.SMASH_CHARGING_PRE)
                .setChargingAnimation(()->WukongAnimations.SMASH_CHARGING_LOOP)
                .setHeavyAttacks(
                        () -> WukongAnimations.SMASH_CHARGED0,
                        () -> WukongAnimations.SMASH_CHARGED1,
                        () -> WukongAnimations.SMASH_CHARGED2,
                        () -> WukongAnimations.SMASH_CHARGED3,
                        () -> WukongAnimations.SMASH_CHARGED4)
                .setDeriveAnimations(
                        () -> WukongAnimations.SMASH_DERIVE1,
                        () -> WukongAnimations.SMASH_DERIVE2)
                .setJumpAttackHeavy(() -> WukongAnimations.JUMP_ATTACK_HEAVY)
        );
        SMASH_STYLE = registryWorker.build("smash_style", StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.SMASH));
        THRUST_STYLE = registryWorker.build("thrust_style", StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.THRUST));
        PILLAR_STYLE = registryWorker.build("pillar_style", StaffStance::new, StaffStance.createStaffStyle().setStyle(WukongStyles.PILLAR));
    }

}
