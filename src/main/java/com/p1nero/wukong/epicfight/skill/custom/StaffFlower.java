package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.server.PlayStaffFlowerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.gameasset.EpicFightSounds;
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

import java.util.UUID;

/**
 * 防守技能棍花
 */
public class StaffFlower extends Skill {

    public static final SkillDataManager.SkillDataKey<Boolean> PLAYING_STAFF_FLOWER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    public static final SkillDataManager.SkillDataKey<Boolean> IS_ONE_HAND = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    public static final SkillDataManager.SkillDataKey<Boolean> KEY_PRESSING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac191981");

    public StaffFlower(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, PLAYING_STAFF_FLOWER, false);
        SkillDataRegister.register(manager, IS_ONE_HAND, false);
        SkillDataRegister.register(manager, KEY_PRESSING, false);

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
//                clientPlayer.sprintTriggerTime = -1;
                Minecraft mc = Minecraft.getInstance();
                ClientEngine.getInstance().controllEngine.setKeyBind(mc.options.keySprint, false);
            }
        }));

        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
            if(container.getDataManager().getDataValue(PLAYING_STAFF_FLOWER) && (WukongMoveset.canBeBlocked(event.getDamageSource().getDirectEntity()) || event.getDamageSource().isProjectile())){
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
                //成功格挡回能量
                skillContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), skillContainer.getResource() + skillContainer.getMaxResource() / 5);
            }
        }));
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

    /**
     * 判断武器是否是悟空棍子类型
     */
    public static boolean isWeaponValid(PlayerPatch<?> playerPatch){
        return playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(!container.getExecuter().isLogicalClient() || !isWeaponValid(container.getExecuter()) || !container.getExecuter().isBattleMode()){
            return;
        }

        if(WukongKeyMappings.STAFF_FLOWER.isDown() && container.getExecuter().hasStamina(Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue())){
            container.getDataManager().setDataSync(KEY_PRESSING, true, ((LocalPlayer) container.getExecuter().getOriginal()));
            if(!container.getDataManager().getDataValue(PLAYING_STAFF_FLOWER)){
                boolean isOneHand = container.getExecuter().getOriginal().getDeltaMovement().length() < 0.1;
                if(!isOneHand){
                    //不这样判断不知道为什么会播完双手就回去播单手
                    container.getDataManager().setDataSync(IS_ONE_HAND, false, ((LocalPlayer) container.getExecuter().getOriginal()));
                }
                PacketRelay.sendToServer(PacketHandler.INSTANCE, new PlayStaffFlowerPacket(isOneHand));
                container.getDataManager().setDataSync(PLAYING_STAFF_FLOWER, true, ((LocalPlayer) container.getExecuter().getOriginal()));
            }
        } else {
            container.getDataManager().setDataSync(KEY_PRESSING, false, ((LocalPlayer) container.getExecuter().getOriginal()));
            container.getDataManager().setDataSync(IS_ONE_HAND, true, ((LocalPlayer) container.getExecuter().getOriginal()));
        }
    }
}
