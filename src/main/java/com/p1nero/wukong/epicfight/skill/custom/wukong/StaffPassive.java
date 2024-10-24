package com.p1nero.wukong.epicfight.skill.custom.wukong;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.ControllEngine;
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
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys.PLAYING_STAFF_SPIN;

/**
 * 棍花和闪避
 */
public class StaffPassive extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac191981");

    public StaffPassive(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        //自动学闪避
        Skill dodge = container.getExecuter().getSkill(SkillSlots.DODGE).getSkill();
        if(dodge != WukongSkills.WUKONG_DODGE){
            container.getExecuter().getSkill(SkillSlots.DODGE).setSkill(WukongSkills.WUKONG_DODGE);
            container.getExecuter().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> wkPlayer.setLastDodgeSkill(dodge == null ? "" : dodge.toString()));
        }
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
                ControllEngine.setKeyBind(mc.options.keySprint, false);
            }
        }));

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {

            if(event.getDamageSource().is(DamageTypes.LIGHTNING_BOLT) && event.getPlayerPatch().getAnimator().getPlayerFor(null).getAnimation().equals(WukongAnimations.STAFF_AUTO4)){
                event.setAmount(0);
                event.setCanceled(true);
            }

            if(container.getDataManager().getDataValue(PLAYING_STAFF_SPIN.get()) && (canBeBlocked(event.getDamageSource().getDirectEntity()) || event.getDamageSource().is(DamageTypes.MOB_PROJECTILE))){
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

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_DAMAGE, EVENT_UUID, (dealtDamageEvent -> {
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

        //拦截闪避事件，替换为自己的闪避并执行，算是保险
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event -> {
            PlayerPatch<?> executer = event.getPlayerPatch();
            Skill ordinalSkill = event.getSkillContainer().getSkill();
            if(!ordinalSkill.getCategory().equals(SkillCategories.DODGE) || ordinalSkill.equals(WukongSkills.WUKONG_DODGE)){
                return;
            }
            int dodgeId = event.getSkillContainer().getSlotId();
            if(executer.isLogicalClient()){
                //临时替换为悟空闪避
                if(!ordinalSkill.equals(WukongSkills.WUKONG_DODGE) && executer.hasStamina(this.getConsumption())){
                    executer.getSkill(SkillSlots.DODGE).setSkill(WukongSkills.WUKONG_DODGE);
                    EpicFightNetworkManager.sendToServer(new CPChangeSkill(dodgeId, -1, WukongSkills.WUKONG_DODGE.toString(), false));
                    executer.getSkill(SkillSlots.DODGE).sendExecuteRequest((LocalPlayerPatch) executer, ClientEngine.getInstance().controllEngine);
                    executer.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
                        wkPlayer.setLastDodgeSkill(ordinalSkill.toString());
                        PacketRelay.syncPlayer(((LocalPlayer) executer.getOriginal()));
                    });
                    event.setCanceled(true);
                }
            }
        }));

    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        //把技能还原回去
        if(!container.getExecuter().isLogicalClient()){
            PacketRelay.syncPlayer(((ServerPlayer) container.getExecuter().getOriginal()));
        }
        container.getExecuter().getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
            if(wkPlayer.getLastDodgeSkill().isEmpty()){
                container.getExecuter().getSkill(SkillSlots.DODGE).setSkill(null);
            } else {
                container.getExecuter().getSkill(SkillSlots.DODGE).setSkill(SkillManager.getSkill(wkPlayer.getLastDodgeSkill()));
            }
        });

        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_DAMAGE, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);

    }

    public static boolean canBeBlocked(Entity entity){
        if(entity == null){
            return false;
        }
        if(Config.entities_can_be_blocked.isEmpty()){
            Config.entities_can_be_blocked = Config.ENTITIES_CAN_BE_BLOCKED_BY_STAFF_FLOWER.get().stream()
                    .map( entityName -> ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityName)))
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
            return toSourceLocation.dot(viewVector) > 0.0;
        }
        return false;
    }

    public static void showBlockedEffect(ServerPlayerPatch playerPatch, Entity directEntity){
        playerPatch.playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
        ServerPlayer serverPlayer = playerPatch.getOriginal();
        EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(serverPlayer.serverLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, directEntity);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(!container.getExecuter().isLogicalClient() || !WukongWeaponCategories.isWeaponValid(container.getExecuter()) || !container.getExecuter().isBattleMode() || !container.getExecuter().getOriginal().onGround()){
            return;
        }

        if(WukongKeyMappings.STAFF_FLOWER.isDown() && container.getExecuter().hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue())){
            if(!container.getDataManager().getDataValue(PLAYING_STAFF_SPIN.get()) && Minecraft.getInstance().player != null){
                PacketRelay.sendToServer(PacketHandler.INSTANCE, new PlayStaffFlowerPacket(WukongKeyMappings.W.isDown()));//按w可变双手棍花
                container.getDataManager().setDataSync(PLAYING_STAFF_SPIN.get(), true, ((LocalPlayer) container.getExecuter().getOriginal()));
            }
        }
    }
}
