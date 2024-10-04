package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.client.AddEntityAfterImageParticle;
import io.netty.buffer.Unpooled;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 完美闪避回棍势，TODO 蓄力的时候完美闪避保留棍势
 */
public class WukongDodge extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("d2d011cc-f30f-11ed-a05b-0242ac114515");
    private static final SkillDataManager.SkillDataKey<Integer> COUNT = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//闪避计数器
    private static final SkillDataManager.SkillDataKey<Integer> DIRECTION = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//方向
    private static final SkillDataManager.SkillDataKey<Integer> RESET_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//回归第一段的时间
    private static final SkillDataManager.SkillDataKey<Boolean> SOUND_PLAYED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否播过音效，防止重复播放
    public static final int RESET_TICKS = 100;
    protected final StaticAnimationProvider[][] animations;

    public static WukongDodge.Builder createDodgeBuilder() {
        return (new WukongDodge.Builder()).setCategory(SkillCategories.DODGE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
    }

    public WukongDodge(WukongDodge.Builder builder) {
        super(builder);
        animations = builder.animations;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(COUNT);
        container.getDataManager().registerData(DIRECTION);
        container.getDataManager().registerData(RESET_TIMER);
        container.getDataManager().registerData(SOUND_PLAYED);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event -> {
            Player player = event.getPlayerPatch().getOriginal();
            if(!container.getDataManager().getDataValue(SOUND_PLAYED)){
                event.getPlayerPatch().playSound(WuKongSounds.PERFECT_DODGE.get(), 1, 1);
                if(player.level instanceof ServerLevel){
                    PacketRelay.sendToAll(PacketHandler.INSTANCE, new AddEntityAfterImageParticle(player.getId()));//下面那行无效，手动发包解决
//                serverLevel.sendParticles(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), player.getX(), player.getY(), player.getZ(), 0, Double.longBitsToDouble(player.getId()), 0.0, 0.0, 1.0);
                }
                SkillContainer weaponInnateContainer = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                weaponInnateContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), weaponInnateContainer.getResource() + 5);//获得棍势
                container.getDataManager().setData(SOUND_PLAYED, true);
            }
            event.getPlayerPatch().playAnimationSynchronized(this.animations[3][container.getDataManager().getDataValue(DIRECTION)].get(), 0.0F);
        }));
    }

    @OnlyIn(Dist.CLIENT)
    public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
        Input input = executer.getOriginal().input;
        input.tick(false);
        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(forward);
        buf.writeInt(backward);
        buf.writeInt(left);
        buf.writeInt(right);
        return buf;
    }

    @OnlyIn(Dist.CLIENT)
    public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
        int forward = args.readInt();
        int backward = args.readInt();
        int left = args.readInt();
        int right = args.readInt();
        int vertic = forward + backward;
        int horizon = left + right;
        int degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
        CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
        packet.getBuffer().writeInt(vertic >= 0 ? 0 : 1);
        packet.getBuffer().writeFloat((float)degree);
        return packet;
    }

    @OnlyIn(Dist.CLIENT)
    public List<Object> getTooltipArgsOfScreen(List<Object> list) {
        list.add(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.consumption));
        return list;
    }

    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        super.executeOnServer(executer, args);
        int i = args.readInt();
        float yaw = args.readFloat();
        SkillDataManager dataManager = executer.getSkill(SkillSlots.DODGE).getDataManager();
        dataManager.setData(SOUND_PLAYED, false);
        int count = dataManager.getDataValue(COUNT);
//        executer.playAnimationSynchronized(this.animations[0][i].get(), 0.0F);
        executer.playAnimationSynchronized(this.animations[count][i].get(), 0.0F);//轮播
        executer.playSound(EpicFightSounds.ROLL, 1.0F, 1.0F);
        dataManager.setDataSync(DIRECTION, i, executer.getOriginal());//完美闪避用
        if(count != 0){
            dataManager.setDataSync(RESET_TIMER, RESET_TICKS, executer.getOriginal());
        }
        dataManager.setDataSync(COUNT, ++count % 3, executer.getOriginal());
        executer.changeModelYRot(yaw);
    }

    /**
     * 太久则复原第一段
     */
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();
        if(manager.hasData(RESET_TIMER) && manager.getDataValue(RESET_TIMER) > 0){
            manager.setData(RESET_TIMER, manager.getDataValue(RESET_TIMER) - 1);
            if(manager.getDataValue(RESET_TIMER) == 1 && manager.hasData(COUNT)){
                manager.setData(COUNT, 0);
            }
        }
    }

    public boolean isExecutableState(PlayerPatch<?> executer) {
        EntityState playerState = executer.getEntityState();
        return !executer.isUnstable() && playerState.canUseSkill() && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable() && executer.getOriginal().getVehicle() == null;
    }

    public static class Builder extends Skill.Builder<WukongDodge> {
        protected StaticAnimationProvider[][] animations = new StaticAnimationProvider[4][4];//第一个参数分别是1、2、3段和完美闪避，第二个是前、后、左、右

        public Builder() {
        }

        public WukongDodge.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public WukongDodge.Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public WukongDodge.Builder setResource(Skill.Resource resource) {
            this.resource = resource;
            return this;
        }

        public WukongDodge.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public WukongDodge.Builder setAnimations1(StaticAnimationProvider... animations) {
            this.animations[0] = animations;
            return this;
        }

        public WukongDodge.Builder setAnimations2(StaticAnimationProvider... animations) {
            this.animations[1] = animations;
            return this;
        }

        public WukongDodge.Builder setAnimations3(StaticAnimationProvider... animations) {
            this.animations[2] = animations;
            return this;
        }
        public WukongDodge.Builder setPerfectAnimations(StaticAnimationProvider... animations) {
            this.animations[3] = animations;
            return this;
        }
    }
}
