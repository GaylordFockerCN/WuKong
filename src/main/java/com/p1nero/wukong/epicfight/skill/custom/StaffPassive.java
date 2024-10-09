package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.server.PlayStaffFlowerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 棍花和闪避
 */
public class StaffPassive extends Skill {

    public static final SkillDataManager.SkillDataKey<Boolean> PLAYING_STAFF_SPIN = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac191981");

    public StaffPassive(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, PLAYING_STAFF_SPIN, false);

        //棍花期间禁止移动
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && WukongKeyMappings.STAFF_FLOWER.isDown()) {
                Input input = event.getMovementInput();
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
                input.down = false;
                input.up = false;
                input.left = false;
                input.right = false;
                input.jumping = false;
                input.shiftKeyDown = false;
                LocalPlayer clientPlayer = event.getPlayerPatch().getOriginal();
                clientPlayer.setSprinting(false);
                clientPlayer.sprintTriggerTime = -1;
                Minecraft mc = Minecraft.getInstance();
                ClientEngine.getInstance().controllEngine.setKeyBind(mc.options.keySprint, false);
            }
        }));

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
            if(container.getDataManager().getDataValue(PLAYING_STAFF_SPIN) && (canBeBlocked(event.getDamageSource().getDirectEntity()) || event.getDamageSource().isProjectile())){
                if(!isBlocked(event.getDamageSource(), event.getPlayerPatch().getOriginal())){
                    return;
                }
                event.setCanceled(true);
                event.setResult(AttackResult.ResultType.BLOCKED);
                LivingEntityPatch<?> attackerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
                if (attackerPatch != null) {
                    attackerPatch.setLastAttackEntity(event.getPlayerPatch().getOriginal());
                }
                Entity directEntity = event.getDamageSource().getDirectEntity();
                LivingEntityPatch<?> entityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(directEntity, LivingEntityPatch.class);
                if (entityPatch != null) {
                    entityPatch.onAttackBlocked(event.getDamageSource(), event.getPlayerPatch());
                }
                showBlockedEffect(event.getPlayerPatch(), event.getDamageSource().getDirectEntity());
                SkillContainer skillContainer = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                Skill skill = skillContainer.getSkill();
                if(skill != null){
                    //成功格挡回能量
                    skillContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), skillContainer.getResource() + Config.CHARGING_SPEED.get().floatValue());
                }
            }
        }));

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (dealtDamageEvent -> {
            StaticAnimation animation = dealtDamageEvent.getDamageSource().getAnimation();
            if(animation.equals(WukongAnimations.STAFF_SPIN_ONE_HAND_LOOP) || animation.equals(WukongAnimations.STAFF_SPIN_TWO_HAND_LOOP)){
                //打中加棍势（因为加的要比造成的伤害多）
                SkillContainer skillContainer = dealtDamageEvent.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                Skill skill = skillContainer.getSkill();
                if(skill != null){
                    skillContainer.getSkill().setConsumptionSynchronize(dealtDamageEvent.getPlayerPatch(), skillContainer.getResource() + Config.CHARGING_SPEED.get().floatValue() * 3);
                }
            }
        }));

        //拦截闪避事件，替换为自己的闪避并执行
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event -> {
            PlayerPatch<?> executer = event.getPlayerPatch();
            Skill ordinalSkill = event.getSkillContainer().getSkill();
            if(!ordinalSkill.getCategory().equals(SkillCategories.DODGE)){
                return;
            }
            int dodgeId = event.getSkillContainer().getSlotId();
            if(executer.isLogicalClient()){
                //还原为上一个闪避
                if(!WukongWeaponCategories.isWeaponValid(executer)){
                    executer.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                        if(wkPlayer.getLastDodgeSkill().isEmpty()){
                            return;
                        }
                        Skill old = SkillManager.getSkill(wkPlayer.getLastDodgeSkill());
                        executer.getSkill(SkillSlots.DODGE).setSkill(old);
                        EpicFightNetworkManager.sendToServer(new CPChangeSkill(dodgeId, -1, old.toString(), false));
                    });
                    return;
                }
                //临时替换为悟空闪避
                if(!ordinalSkill.equals(WukongSkills.WUKONG_DODGE) && executer.hasStamina(this.getConsumption())){
                    executer.getSkill(SkillSlots.DODGE).setSkill(WukongSkills.WUKONG_DODGE);
                    EpicFightNetworkManager.sendToServer(new CPChangeSkill(dodgeId, -1, WukongSkills.WUKONG_DODGE.toString(), false));
                    executer.getSkill(SkillSlots.DODGE).sendExecuteRequest((LocalPlayerPatch) executer, ClientEngine.getInstance().controllEngine);
                    executer.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                        wkPlayer.setLastDodgeSkill(ordinalSkill.toString());
                    });
                    event.setCanceled(true);
                }
            }
        }));

    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
        PlayerPatch<?> executer = container.getExecuter();
        executer.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
            if(wkPlayer.getLastDodgeSkill().isEmpty()){
                return;
            }
            Skill old = SkillManager.getSkill(wkPlayer.getLastDodgeSkill());
            executer.getSkill(SkillSlots.DODGE).setSkill(old);
            EpicFightNetworkManager.sendToServer(new CPChangeSkill(executer.getSkill(SkillSlots.DODGE).getSlotId(), -1, old.toString(), false));
        });
        return;
    }

    public static boolean canBeBlocked(Entity entity){
        if(entity == null){
            return false;
        }
        if(Config.entities_can_be_blocked.isEmpty()){
            Config.entities_can_be_blocked = Config.ENTITIES_CAN_BE_BLOCKED_BY_STAFF_FLOWER.get().stream()
                    .map( entityName -> ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityName)))
                    .collect(Collectors.toSet());
        }
        return Config.entities_can_be_blocked.contains(entity.getType());
    }

    /**
     * 判断是否是正面且可被格挡
     */
    private boolean isBlocked(DamageSource damageSource, ServerPlayer player){
        Vec3 sourceLocation = damageSource.getSourcePosition();
        if (sourceLocation != null) {
            Vec3 viewVector = player.getViewVector(1.0F);
            Vec3 toSourceLocation = sourceLocation.subtract((player).position()).normalize();
            if (toSourceLocation.dot(viewVector) > 0.0) {
                if (damageSource instanceof EpicFightDamageSource epicFightDamageSource) {
                    return !epicFightDamageSource.hasTag(SourceTags.GUARD_PUNCTURE);
                }
            }
        }
        return false;
    }

    public static void showBlockedEffect(ServerPlayerPatch playerPatch, Entity directEntity){
        playerPatch.playSound(EpicFightSounds.CLASH, -0.05F, 0.1F);
        ServerPlayer serverPlayer = playerPatch.getOriginal();
        EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(serverPlayer.getLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, directEntity);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(!container.getExecuter().isLogicalClient() || !WukongWeaponCategories.isWeaponValid(container.getExecuter()) || !container.getExecuter().isBattleMode() || !container.getExecuter().getOriginal().isOnGround()){
            return;
        }

        if(WukongKeyMappings.STAFF_FLOWER.isDown() && container.getExecuter().hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue())){
            if(!container.getDataManager().getDataValue(PLAYING_STAFF_SPIN) && Minecraft.getInstance().player != null){
                PacketRelay.sendToServer(PacketHandler.INSTANCE, new PlayStaffFlowerPacket(WukongKeyMappings.W.isDown()));//按w可变双手棍花
                container.getDataManager().setDataSync(PLAYING_STAFF_SPIN, true, ((LocalPlayer) container.getExecuter().getOriginal()));
            }
        }
    }
}
