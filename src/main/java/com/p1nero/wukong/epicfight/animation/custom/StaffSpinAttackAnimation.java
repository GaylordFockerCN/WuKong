package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.client.event.CameraAnim;
import com.p1nero.wukong.epicfight.skill.custom.StaffPassive;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.player.LocalPlayer;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 尝试修改动画播放的move lock
 * 后面直接监听输入事件取消input了。。
 */
public class StaffSpinAttackAnimation extends BasicMultipleAttackAnimation {

    public StaffSpinAttackAnimation(float end, HumanoidArmature biped, String path, float damageMultiplier, boolean isTwoHand){
        super(0, path, biped,
                        new AttackAnimation.Phase(0.0F, 0.00F, 0.25F, end, 0.26F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.24F, 0.25F, 0.50F, end, 0.51F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.49F, 0.50F, 0.75F, end, 0.76F , biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)),
                        new AttackAnimation.Phase(0.74F, 0.74F, 1.0F, end, end, biped.toolR, null)
                                .addProperty(AnimationProperty.AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(damageMultiplier)));
        this.addProperty(AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE, false);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addEvents(AnimationProperty.StaticAnimationProperty.ON_BEGIN_EVENTS, AnimationEvent.TimeStampedEvent.create(((livingEntityPatch, staticAnimation, objects) -> {
                            if(isTwoHand && livingEntityPatch.getOriginal() instanceof LocalPlayer){
                                CameraAnim.zoomIn(new Vec3f(-1.0F, 0.0F, 1.25F), 20);
                            }
                        }), AnimationEvent.Side.CLIENT));

    }

    @Override
    public void begin(LivingEntityPatch<?> entityPatch) {
        super.begin(entityPatch);
        if(entityPatch instanceof ServerPlayerPatch serverPlayerPatch && WukongWeaponCategories.isWeaponValid(serverPlayerPatch)){
            SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
            passiveContainer.getDataManager().setDataSync(StaffPassive.PLAYING_STAFF_SPIN, true, serverPlayerPatch.getOriginal());
        }
    }

    @Override
    public void end(LivingEntityPatch<?> entityPatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        if(entityPatch instanceof ServerPlayerPatch serverPlayerPatch && WukongWeaponCategories.isWeaponValid(serverPlayerPatch)){
            SkillContainer passiveContainer = serverPlayerPatch.getSkill(SkillSlots.WEAPON_PASSIVE);
            passiveContainer.getDataManager().setDataSync(StaffPassive.PLAYING_STAFF_SPIN, false, serverPlayerPatch.getOriginal());
        }
        if(entityPatch.isLogicalClient() && CameraAnim.isAiming()){
            CameraAnim.zoomOut(20);//保险
        }
    }

}
