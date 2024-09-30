package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import io.netty.buffer.Unpooled;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
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
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

public class WukongDodge extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("d2d011cc-f30f-11ed-a05b-0242ac114515");
    private static final SkillDataManager.SkillDataKey<Integer> COUNT = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//闪避计数器
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
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event -> {
            Player player = event.getPlayerPatch().getOriginal();
//            event.getPlayerPatch().playSound();TODO 播放音效 播动画
            SkillContainer weaponInnateContainer = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
            weaponInnateContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), weaponInnateContainer.getResource() + 5);//获得棍势
            if(player.level instanceof ServerLevel serverLevel){
                serverLevel.sendParticles(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), player.getX(), player.getY(), player.getZ(), 1, Double.longBitsToDouble(player.getId()), 0.0, 0.0, 0.0);
            }
        }));
    }

    @OnlyIn(Dist.CLIENT)
    public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
        Input input = ((LocalPlayer)executer.getOriginal()).input;
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
        int count = dataManager.getDataValue(COUNT);
        executer.playAnimationSynchronized(this.animations[count][i].get(), 0.0F);
        dataManager.setDataSync(COUNT, ++count % 2, executer.getOriginal());
        executer.changeModelYRot(yaw);
    }

    public boolean isExecutableState(PlayerPatch<?> executer) {
        EntityState playerState = executer.getEntityState();
        return !executer.isUnstable() && playerState.canUseSkill() && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable() && executer.getOriginal().getVehicle() == null;
    }

    public static class Builder extends Skill.Builder<WukongDodge> {
        protected StaticAnimationProvider[][] animations = new StaticAnimationProvider[3][4];

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
    }
}
