package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.client.AddEntityAfterImageParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.animation.types.EntityState;
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
public class WukongDodgeSkill extends Skill {
    private static final UUID EVENT_UUID = UUID.fromString("d2d011cc-f30f-11ed-a05b-0242ac114515");
      public static final int RESET_TICKS = 100;
    protected final StaticAnimationProvider[][] animations;

    public static WukongDodgeSkill.Builder createDodgeBuilder() {
        return (new WukongDodgeSkill.Builder()).setCategory(SkillCategories.DODGE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
    }

    public WukongDodgeSkill(WukongDodgeSkill.Builder builder) {
        super(builder);
        animations = builder.animations;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event -> {
            Player player = event.getPlayerPatch().getOriginal();
            if(!container.getDataManager().getDataValue(WukongSkillDataKeys.SOUND_PLAYED.get())){
                event.getPlayerPatch().playSound(WuKongSounds.PERFECT_DODGE.get(), 1, 1);
                if(player.level() instanceof ServerLevel){
                    PacketRelay.sendToAll(PacketHandler.INSTANCE, new AddEntityAfterImageParticle(player.getId()));//下面那行无效，手动发包解决
//                serverLevel.sendParticles(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), player.getX(), player.getY(), player.getZ(), 0, Double.longBitsToDouble(player.getId()), 0.0, 0.0, 1.0);
                }
                SkillContainer weaponInnateContainer = event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE);
                weaponInnateContainer.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), weaponInnateContainer.getResource() + 5);//获得棍势
                container.getDataManager().setData(WukongSkillDataKeys.SOUND_PLAYED.get(), true);
            }
            event.getPlayerPatch().playAnimationSynchronized(this.animations[3][container.getDataManager().getDataValue(WukongSkillDataKeys.DIRECTION.get())].get(), 0.0F);
        }));
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
    }

    @OnlyIn(Dist.CLIENT)
    public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
        Input input = executer.getOriginal().input;
        float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(executer.getOriginal()), 0.0F, 1.0F);
        input.tick(false, pulse);
        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
        int vertic = forward + backward;
        int horizon = left + right;
        float yRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
        float degree = (float)(-(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon)) + yRot;
        CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
        packet.getBuffer().writeInt(vertic >= 0 ? 0 : 1);
        packet.getBuffer().writeFloat(degree);
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
        dataManager.setData(WukongSkillDataKeys.SOUND_PLAYED.get(), false);
        int count = dataManager.getDataValue(WukongSkillDataKeys.COUNT.get());
//        executer.playAnimationSynchronized(this.animations[0][i].get(), 0.0F);
        executer.playAnimationSynchronized(this.animations[count][i].get(), 0.0F);//轮播
        executer.playSound(EpicFightSounds.ROLL.get(), 1.0F, 1.0F);
        dataManager.setDataSync(WukongSkillDataKeys.DIRECTION.get(), i, executer.getOriginal());//完美闪避用
        if(count != 0){
            dataManager.setDataSync(WukongSkillDataKeys.RESET_TIMER.get(), RESET_TICKS, executer.getOriginal());
        }
        dataManager.setDataSync(WukongSkillDataKeys.COUNT.get(), ++count % 3, executer.getOriginal());
        executer.setModelYRot(yaw, true);
    }

    /**
     * 太久则复原第一段
     */
    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager manager = container.getDataManager();
        if(manager.hasData(WukongSkillDataKeys.RESET_TIMER.get()) && manager.getDataValue(WukongSkillDataKeys.RESET_TIMER.get()) > 0){
            manager.setData(WukongSkillDataKeys.RESET_TIMER.get(), manager.getDataValue(WukongSkillDataKeys.RESET_TIMER.get()) - 1);
            if(manager.getDataValue(WukongSkillDataKeys.RESET_TIMER.get()) == 1 && manager.hasData(WukongSkillDataKeys.COUNT.get())){
                manager.setData(WukongSkillDataKeys.COUNT.get(), 0);
            }
        }
    }

    public boolean isExecutableState(PlayerPatch<?> executer) {
        EntityState playerState = executer.getEntityState();
        return !executer.isInAir() && playerState.canUseSkill() && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable() && executer.getOriginal().getVehicle() == null;
    }

    public static class Builder extends Skill.Builder<WukongDodgeSkill> {
        protected StaticAnimationProvider[][] animations = new StaticAnimationProvider[4][4];//第一个参数分别是1、2、3段和完美闪避，第二个是前、后、左、右

        public Builder() {
        }

        public WukongDodgeSkill.Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public WukongDodgeSkill.Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public WukongDodgeSkill.Builder setResource(Skill.Resource resource) {
            this.resource = resource;
            return this;
        }

        public WukongDodgeSkill.Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public WukongDodgeSkill.Builder setAnimations1(StaticAnimationProvider... animations) {
            this.animations[0] = animations;
            return this;
        }

        public WukongDodgeSkill.Builder setAnimations2(StaticAnimationProvider... animations) {
            this.animations[1] = animations;
            return this;
        }

        public WukongDodgeSkill.Builder setAnimations3(StaticAnimationProvider... animations) {
            this.animations[2] = animations;
            return this;
        }
        public WukongDodgeSkill.Builder setPerfectAnimations(StaticAnimationProvider... animations) {
            this.animations[3] = animations;
            return this;
        }
    }
}
