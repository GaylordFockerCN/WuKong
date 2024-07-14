package com.p1nero.wukong.epicfight.animation;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.epicfight.skill.HeavyAttack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
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
 * 根据棍势加伤
 */
public class WukongChargedAttackAnimation extends BasicAttackAnimation {
    public WukongChargedAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongChargedAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongChargedAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, hand, collider, colliderJoint, path, armature);
    }

    public WukongChargedAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
    }

    @Override
    protected void hurtCollidingEntities(LivingEntityPatch<?> entityPatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase) {
        LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
        entityPatch.getArmature().initializeTransform();
        float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
        float poseTime = state.attacking() ? elapsedTime : phase.contact;
        List<Entity> list = this.getPhaseByTime(elapsedTime).getCollidingEntities(entityPatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entityPatch));
        if (!list.isEmpty()) {
            HitEntityList hitEntities = new HitEntityList(entityPatch, list, (HitEntityList.Priority)phase.getProperty(AnimationProperty.AttackPhaseProperty.HIT_PRIORITY).orElse(HitEntityList.Priority.DISTANCE));
            int maxStrikes = this.getMaxStrikes(entityPatch, phase);

            while(true) {
                Entity hitten;
                LivingEntity trueEntity;
                do {
                    do {
                        do {
                            do {
                                do {
                                    if (entityPatch.getCurrenltyHurtEntities().size() >= maxStrikes || !hitEntities.next()) {
                                        return;
                                    }

                                    hitten = hitEntities.getEntity();
                                    trueEntity = this.getTrueEntity(hitten);
                                } while(trueEntity == null);
                            } while(!trueEntity.isAlive());
                        } while(entityPatch.getCurrenltyAttackedEntities().contains(trueEntity));
                    } while(entityPatch.isTeammate(hitten));
                } while(!(hitten instanceof LivingEntity) && !(hitten instanceof PartEntity));

                if (entity.hasLineOfSight(hitten)) {
                    EpicFightDamageSource source = this.getEpicFightDamageSource(entityPatch, hitten, phase);
                    int prevInvulTime = hitten.invulnerableTime;
                    hitten.invulnerableTime = 0;
                    AttackResult attackResult = entityPatch.attack(source, hitten, phase.hand);
                    hitten.invulnerableTime = prevInvulTime;
                    if (attackResult.resultType.dealtDamage()) {
                        if (entityPatch instanceof ServerPlayerPatch playerPatch) {
                            SkillDataManager dataManager = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
                            int starts = dataManager.getDataValue(HeavyAttack.STARTS_CONSUMED);
                            float chargedPower = Math.min(dataManager.getDataValue(CHARGING_TIMER) / 20.0F, 8);
                            float damage = attackResult.damage;
                            damage *= 1 + starts==4 ? 4 : starts/1.5F;
                            damage += chargedPower;
                            damage *= Config.DAMAGE_MULTIPLIER.get();
                            System.out.println(damage);
                            playerPatch.getEventListener().triggerEvents(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(playerPatch, trueEntity, source, damage));
                        }

                        hitten.level.playSound(null, hitten.getX(), hitten.getY(), hitten.getZ(), this.getHitSound(entityPatch, phase), hitten.getSoundSource(), 1.0F, 1.0F);
                        this.spawnHitParticle((ServerLevel)hitten.getLevel(), entityPatch, hitten, phase);
                    }

                    entityPatch.getCurrenltyAttackedEntities().add(trueEntity);
                    if (attackResult.resultType.shouldCount()) {
                        entityPatch.getCurrenltyHurtEntities().add(trueEntity);
                    }
                }
            }
        }
    }
}
