package com.p1nero.wukong.epicfight.animation.custom;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.gamerule.EpicFightGamerules;
public class BasicMultipleAttackAnimation extends AttackAnimation {
    public BasicMultipleAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        this(convertTime, antic, antic, contact, recovery, collider, colliderJoint, path, armature);
    }
    public BasicMultipleAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        this(convertTime, path, armature, new AttackAnimation.Phase(0.0F, antic, preDelay, contact, recovery, Float.MAX_VALUE, colliderJoint, collider));
    }
    public BasicMultipleAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
        this.newTimePair(0.0F, Float.MAX_VALUE);
        this.addStateRemoveOld(EntityState.TURNING_LOCKED, false);
        this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_LOC_TARGET);
        this.addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
            LivingEntity attackTarget = entitypatch.getTarget();
            if (!(Boolean)self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false) && attackTarget != null) {
                TransformSheet transform = self.getTransfroms().get("Root").copyAll();
                Keyframe[] keyframes = transform.getKeyframes();
                int startFrame = 0;
                int endFrame = transform.getKeyframes().length - 1;
                Vec3f keyLast = keyframes[endFrame].transform().translation();
                Vec3 pos = entitypatch.getOriginal().getEyePosition();
                Vec3 targetpos = attackTarget.position();
                float horizontalDistance = Math.max((float)targetpos.subtract(pos).horizontalDistance() * 1.75F - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
                Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
                float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);

                for(int i = startFrame; i <= endFrame; ++i) {
                    Vec3f translation = keyframes[i].transform().translation();
                    translation.z *= scale;
                }
                transformSheet.readFrom(transform);
            } else {
                transformSheet.readFrom(self.getTransfroms().get("Root"));
            }
        });
    }
    protected void hurtCollidingEntities(LivingEntityPatch<?> entitypatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, AttackAnimation.Phase phase) {
        LivingEntity entity = entitypatch.getOriginal();
        entitypatch.getArmature().initializeTransform();
        float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
        float poseTime = state.attacking() ? elapsedTime : phase.contact;
        List<Entity> list = this.getPhaseByTime(elapsedTime).getCollidingEntities(entitypatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entitypatch));
        if (!list.isEmpty()) {
            HitEntityList hitEntities = new HitEntityList(entitypatch, list, phase.getProperty(AttackPhaseProperty.HIT_PRIORITY).orElse(Priority.DISTANCE));
            int maxStrikes = this.getMaxStrikes(entitypatch, phase);
            while(true) {
                Entity hitten;
                LivingEntity truehittenEntity;
                do {
                    do {
                        do {
                            do {
                                do {
                                    do {
                                        if (entitypatch.getCurrenltyHurtEntities().size() >= maxStrikes || !hitEntities.next()) {
                                            return;
                                        }
                                        hitten = hitEntities.getEntity();
                                        truehittenEntity = this.getTrueEntity(hitten);
                                    } while(truehittenEntity == null);
                                } while(!truehittenEntity.isAlive());
                            } while(entitypatch.getCurrenltyAttackedEntities().contains(truehittenEntity));
                        } while(entitypatch.isTeammate(hitten));
                    } while(!(hitten instanceof LivingEntity) && !(hitten instanceof PartEntity));
                } while(!entity.hasLineOfSight(hitten));
                HurtableEntityPatch<?> hitHurtableEntityPatch = (HurtableEntityPatch)EpicFightCapabilities.getEntityPatch(hitten, HurtableEntityPatch.class);
                EpicFightDamageSource source = this.getEpicFightDamageSource(entitypatch, hitten, phase);
                float anti_stunlock = 1.0F;
                if (hitHurtableEntityPatch != null) {
                    if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).isPresent()) {
                        if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() == StunType.NONE) {
                            if (truehittenEntity instanceof Player) {
                                source.setStunType(StunType.LONG);
                                source.setImpact((float)((double)(source.getImpact() * 4.0F) / (1.0 - truehittenEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE))));
                            } else {
                                source.setStunType(StunType.NONE);
                            }
                        } else if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() == StunType.HOLD && hitHurtableEntityPatch.getOriginal().hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get())) {
                            source.setStunType(StunType.NONE);
                        } else if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() == StunType.FALL && hitHurtableEntityPatch.getOriginal().hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get())) {
                            source.setStunType(StunType.NONE);
                        } else if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() == StunType.KNOCKDOWN && hitHurtableEntityPatch.getOriginal().hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get())) {
                            source.setStunType(StunType.NONE);
                        } else {
                            source = this.getEpicFightDamageSource(entitypatch, hitten, phase);
                        }
                    } else {
                        source = this.getEpicFightDamageSource(entitypatch, hitten, phase);
                    }
                    String replaceTag = "anti_stunlock:" + anti_stunlock + ":" + hitten.tickCount + ":" + this.getId() + "-" + phase.contact;
                    Iterator var20;
                    String tag;
                    if (hitHurtableEntityPatch.isStunned()) {
                        var20 = hitten.getTags().iterator();
                        while(var20.hasNext()) {
                            tag = (String)var20.next();
                            if (tag.contains("anti_stunlock:")) {
                                anti_stunlock = this.applyAntiStunLock(hitten, anti_stunlock, source, phase, tag);
                                break;
                            }
                        }
                    } else {
                        boolean firstAttack = true;
                        for (String s : hitten.getTags()) {
                            tag = s;
                            if (tag.contains("anti_stunlock:")) {
                                float lastHitTime = Float.parseFloat(tag.split(":")[2]);
                                if ((float) hitten.tickCount - lastHitTime <= 20.0F) {
                                    anti_stunlock = this.applyAntiStunLock(hitten, anti_stunlock, source, phase, tag);
                                    firstAttack = false;
                                }
                                break;
                            }
                        }
                        if (firstAttack) {
                            int i = 0;
                            while(i < hitten.getTags().size()) {
                                if (((String)hitten.getTags().toArray()[i]).contains("anti_stunlock:")) {
                                    hitten.getTags().remove(hitten.getTags().toArray()[i]);
                                } else {
                                    ++i;
                                }
                            }
                            hitten.addTag(replaceTag);
                        }
                    }
                    if (anti_stunlock < (hitten instanceof Player ? 0.3F : 0.2F)) {
                        var20 = hitten.getTags().iterator();
                        while(var20.hasNext()) {
                            tag = (String)var20.next();
                            if (tag.contains("anti_stunlock:")) {
                                hitten.removeTag(tag);
                                break;
                            }
                        }
                        source.setStunType(StunType.KNOCKDOWN);
                    }
                    if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).isPresent()) {
                        if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() != StunType.NONE) {
                            source.setImpact(source.getImpact() * anti_stunlock);
                        }
                    } else {
                        source.setImpact(source.getImpact() * anti_stunlock);
                    }
                }
                int prevInvulTime = hitten.invulnerableTime;
                hitten.invulnerableTime = 0;
                AttackResult attackResult = entitypatch.attack(source, hitten, phase.hand);
                hitten.invulnerableTime = prevInvulTime;
                if (attackResult.resultType.dealtDamage()) {
                    if (entitypatch instanceof ServerPlayerPatch playerpatch) {
                        playerpatch.getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_POST, new DealtDamageEvent(playerpatch, truehittenEntity, source, attackResult.damage));
                    }
                    if (source.getStunType() == StunType.KNOCKDOWN) {
                        truehittenEntity.addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 0, true, false, false));
                    }
                    hitten.level.playSound(null, hitten.getX(), hitten.getY(), hitten.getZ(), this.getHitSound(entitypatch, phase), hitten.getSoundSource(), 1.0F, 1.0F);
                    this.spawnHitParticle((ServerLevel)hitten.level, entitypatch, hitten, phase);
                    if (hitHurtableEntityPatch != null && phase.getProperty(AttackPhaseProperty.STUN_TYPE).isPresent() && !hitHurtableEntityPatch.getOriginal().hasEffect(EpicFightMobEffects.STUN_IMMUNITY.get())) {
                        float stunTime;
                        if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() == StunType.NONE && !(truehittenEntity instanceof Player)) {
                            stunTime = (float)((double)(source.getImpact() / anti_stunlock * 0.2F) * (1.0 - truehittenEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                            if (hitHurtableEntityPatch.getOriginal().isAlive()) {
                                hitHurtableEntityPatch.applyStun(anti_stunlock > 0.3F ? StunType.LONG : StunType.KNOCKDOWN, stunTime);
                                if (anti_stunlock <= 0.3F) {
                                    hitHurtableEntityPatch.getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 0, true, false, false));
                                }
                                float power = source.getImpact() * 0.25F;
                                double d1 = entity.getX() - hitten.getX();
                                double d0;
                                for(d0 = entity.getZ() - hitten.getZ(); d1 * d1 + d0 * d0 < 1.0E-4; d0 = (Math.random() - Math.random()) * 0.01) {
                                    d1 = (Math.random() - Math.random()) * 0.01;
                                }
                                power = (float) ((double) power * (1.0 - truehittenEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                                if ((double)power > 0.0) {
                                    hitten.hasImpulse = true;
                                    Vec3 vec3 = hitten.getDeltaMovement();
                                    Vec3 vec31 = (new Vec3(d1, 0.0, d0)).normalize().scale(power);
                                    hitten.setDeltaMovement(vec3.x / 2.0 - vec31.x, hitten.isOnGround() ? Math.min(0.4, vec3.y / 2.0) : vec3.y, vec3.z / 2.0 - vec31.z);
                                }
                            }
                        }
                        if (phase.getProperty(AttackPhaseProperty.STUN_TYPE).get() == StunType.FALL) {
                            stunTime = (float)((double)(source.getImpact() / anti_stunlock * 0.4F) * (1.0 - truehittenEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                            if (hitHurtableEntityPatch.getOriginal().isAlive()) {
                                hitHurtableEntityPatch.applyStun(anti_stunlock > 0.3F ? StunType.SHORT : StunType.KNOCKDOWN, stunTime);
                                if (anti_stunlock <= 0.3F) {
                                    hitHurtableEntityPatch.getOriginal().addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 0, true, false, false));
                                }
                                double power = source.getImpact() / anti_stunlock * 0.25F;
                                double d1 = entity.getX() - hitten.getX();
                                double d2 = entity.getY() - 8.0 - hitten.getY();
                                double d0;
                                for(d0 = entity.getZ() - hitten.getZ(); d1 * d1 + d0 * d0 < 1.0E-4; d0 = (Math.random() - Math.random()) * 0.01) {
                                    d1 = (Math.random() - Math.random()) * 0.01;
                                }
                                if (!(truehittenEntity instanceof Player)) {
                                    power *= 1.0 - truehittenEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                                }
                                if (power > 0.0) {
                                    hitten.hasImpulse = true;
                                    Vec3 vec3 = entity.getDeltaMovement();
                                    Vec3 vec31 = (new Vec3(d1, d2, d0)).normalize().scale(power);
                                    if (!(truehittenEntity instanceof Player) || !(entitypatch instanceof PlayerPatch)) {
                                        hitten.setDeltaMovement(vec3.x / 2.0 - vec31.x, vec3.y / 2.0 - vec31.y, vec3.z / 2.0 - vec31.z);
                                    }
                                }
                                if (truehittenEntity instanceof Player && entitypatch instanceof PlayerPatch) {
                                    truehittenEntity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 5, (int)(power * 4.0 * 6.0), true, false, false));
                                }
                            }
                        }
                    }
                }
                entitypatch.getCurrenltyAttackedEntities().add(truehittenEntity);
                if (attackResult.resultType.shouldCount()) {
                    entitypatch.getCurrenltyHurtEntities().add(truehittenEntity);
                }
            }
        }
    }
    protected void onLoaded() {
        super.onLoaded();
        if (!this.properties.containsKey(AttackAnimationProperty.BASIS_ATTACK_SPEED)) {
            float basisSpeed = Float.parseFloat(String.format(Locale.US, "%.2f", 1.0F / this.totalTime));
            this.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, basisSpeed);
        }
    }
    public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entitypatch, nextAnimation, isEnd);
        boolean stiffAttack = entitypatch.getOriginal().level.getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).get();
        if (!isEnd && !nextAnimation.isMainFrameAnimation() && entitypatch.isLogicalClient() && !stiffAttack) {
            float playbackSpeed = 0.05F * this.getPlaySpeed(entitypatch);
            entitypatch.getClientAnimator().baseLayer.copyLayerTo(entitypatch.getClientAnimator().baseLayer.getLayer(yesman.epicfight.api.client.animation.Layer.Priority.HIGHEST), playbackSpeed);
        }
    }
    public TypeFlexibleHashMap<EntityState.StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
        TypeFlexibleHashMap<EntityState.StateFactor<?>> stateMap = super.getStatesMap(entitypatch, time);
        if (!entitypatch.getOriginal().level.getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).get()) {
            stateMap.put((TypeFlexibleHashMap.TypeKey<Boolean>) EntityState.MOVEMENT_LOCKED, false);
        }
        return stateMap;
    }
    public Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation dynamicAnimation) {
        Vec3 vec3 = super.getCoordVector(entitypatch, dynamicAnimation);
        if (entitypatch.shouldBlockMoving() && this.getProperty(ActionAnimationProperty.CANCELABLE_MOVE).orElse(false)) {
            vec3 = vec3.scale(0.0);
        }
        return vec3;
    }
    public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
        if (layer == yesman.epicfight.api.client.animation.Layer.Priority.HIGHEST) {
            return !JointMaskEntry.BASIC_ATTACK_MASK.isMasked(entitypatch.getCurrentLivingMotion(), joint);
        } else {
            return super.isJointEnabled(entitypatch, layer, joint);
        }
    }
    public JointMask.BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
        if (layer == yesman.epicfight.api.client.animation.Layer.Priority.HIGHEST) {
            List<JointMask> list = JointMaskEntry.BIPED_UPPER_JOINTS_WITH_ROOT;
            int position = list.indexOf(JointMask.of(joint));
            return position >= 0 ? list.get(position).getBindModifier() : null;
        } else {
            return super.getBindModifier(entitypatch, layer, joint);
        }
    }
    public boolean isBasicAttackAnimation() {
        return true;
    }
    public float applyAntiStunLock(Entity hitten, float anti_stunlock, EpicFightDamageSource source, Phase phase, String tag) {
        boolean isPhaseFromSameAnimnation = false;
        String phaseID;
        int i;
        String var10000;
        if (hitten.level.getBlockState(new BlockPos(new Vec3(hitten.getX(), hitten.getY() - 1.0, hitten.getZ()))).isAir() && source.getStunType() != StunType.FALL) {
            var10000 = String.valueOf(this.getId());
            phaseID = var10000 + "-" + phase.contact;
            if (tag.split(":").length > 3) {
                if (String.valueOf(this.getId()).equals(tag.split(":")[3].split("-")[0]) && !String.valueOf(phase.contact).equals(tag.split(":")[3].split("-")[1])) {
                    anti_stunlock = Float.parseFloat(tag.split(":")[1]) * 0.98F;
                    isPhaseFromSameAnimnation = true;
                } else {
                    anti_stunlock = Float.parseFloat(tag.split(":")[1]) * 0.9F;
                }
            }
            for(i = 3; i < tag.split(":").length && i < 7; ++i) {
                if (tag.split(":")[i].equals(phaseID)) {
                    anti_stunlock *= 0.6F;
                }
            }
        } else {
            var10000 = String.valueOf(this.getId());
            phaseID = var10000 + "-" + phase.contact;
            if (tag.split(":").length > 3) {
                if (String.valueOf(this.getId()).equals(tag.split(":")[3].split("-")[0]) && !String.valueOf(phase.contact).equals(tag.split(":")[3].split("-")[1])) {
                    anti_stunlock = Float.parseFloat(tag.split(":")[1]) * 0.98F;
                    isPhaseFromSameAnimnation = true;
                } else {
                    anti_stunlock = Float.parseFloat(tag.split(":")[1]) * 0.8F;
                }
            }
            for(i = 3; i < tag.split(":").length && i < 5; ++i) {
                if (tag.split(":")[i].equals(phaseID)) {
                    anti_stunlock *= 0.6F;
                }
            }
        }
        hitten.removeTag(tag);
        byte maxSavedAttack;
        String replaceTag;
        if (isPhaseFromSameAnimnation) {
            replaceTag = "anti_stunlock:" + anti_stunlock + ":" + hitten.tickCount;
            maxSavedAttack = 6;
        } else {
            replaceTag = "anti_stunlock:" + anti_stunlock + ":" + hitten.tickCount + ":" + this.getId() + "-" + phase.contact;
            maxSavedAttack = 5;
        }
        for(i = 3; i < tag.split(":").length && i < maxSavedAttack; ++i) {
            replaceTag = replaceTag.concat(":" + tag.split(":")[i]);
        }
        hitten.addTag(replaceTag);
        return anti_stunlock;
    }
}