package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.epicfight.skill.HeavyAttack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;

import static com.p1nero.wukong.epicfight.skill.HeavyAttack.CHARGING_TIMER;

/**
 * 在造成伤害的时间节点给物品添加nbt标签，方便做棍子的人操作缩放
 * 根据棍势加伤，但是还没实现
 */
public class WukongChargedAttackAnimation extends BasicAttackAnimation {
    public WukongChargedAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
        this.addProperty(AnimationProperty.StaticAnimationProperty.PLAY_SPEED_MODIFIER, ((dynamicAnimation, livingEntityPatch, v, v1) -> 1.5F))
                .addStateRemoveOld(EntityState.CAN_BASIC_ATTACK, false)
                .addStateRemoveOld(EntityState.CAN_SKILL_EXECUTION, false);
        this.addEvents(AnimationEvent.TimeStampedEvent.create(antic, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag().putBoolean("playing_wk_charged", true);
        }), AnimationEvent.Side.SERVER));
        this.addEvents(AnimationEvent.TimeStampedEvent.create(contact, ((livingEntityPatch, staticAnimation, objects) -> {
            livingEntityPatch.getOriginal().getMainHandItem().getOrCreateTag().putBoolean("playing_wk_charged", false);
        }), AnimationEvent.Side.SERVER));
    }

    public WukongChargedAttackAnimation setImpact(float impact){
        this.addProperty(AnimationProperty.AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(impact));
        return this;
    }

    /**
     * TODO
     */
    @Override
    protected void hurtCollidingEntities(LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase) {
        super.hurtCollidingEntities(entitypatch, prevElapsedTime, elapsedTime, prevState, state, phase);
    }

}
