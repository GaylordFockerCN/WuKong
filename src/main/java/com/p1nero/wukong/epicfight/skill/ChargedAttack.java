package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.epicfight.WukongStyles;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.TickEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Arrays;
import java.util.UUID;

/**
 * 重击，根据层数不同使用不同动作
 */
public class ChargedAttack extends Skill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114514");
    public static final int MAX_TIMER = 20;
    private static final SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER, true);
    private static final SkillDataManager.SkillDataKey<Boolean> CAN_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN, true);
    private static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN, true);
    protected final StaticAnimation[] animations;//倒一是衍生1，倒二是衍生2，0~4是五种重击
    protected final StaticAnimation deriveAnimation1;
    protected final StaticAnimation deriveAnimation2;

    public static Builder createChargedAttack(){
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.STAMINA);
    }

    public ChargedAttack(Builder builder) {
        super(builder);
        this.animations = new StaticAnimation[builder.animations.length];
        for(int i = 0; i < builder.animations.length; ++i) {
            this.animations[i] = EpicFightMod.getInstance().animationManager.findAnimationByPath(builder.animations[i].toString());
        }
        deriveAnimation1 = animations[animations.length-1];
        deriveAnimation2 = animations[animations.length-2];
    }

    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillDataManager dataManager = executer.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
        if(dataManager.getDataValue(CAN_DERIVE)){
            executer.playAnimationSynchronized(deriveAnimation1, 0.2F);
        }
        if(dataManager.getDataValue(CAN_SECOND_DERIVE)){
            executer.playAnimationSynchronized(deriveAnimation2, 0.2F);
        }
        executer.playAnimationSynchronized(animations[1], 0.2F);//TODO 换成层数
        super.executeOnServer(executer, args);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        container.getDataManager().registerData(CAN_DERIVE);
        container.getDataManager().registerData(CAN_SECOND_DERIVE);
        container.getDataManager().registerData(DERIVE_TIMER);
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
            if(event.getAnimation() == animations[animations.length-1]){
                container.getDataManager().setData(CAN_DERIVE, false);
                container.getDataManager().setData(CAN_SECOND_DERIVE, true);
            }
            if(event.getAnimation() == animations[animations.length-2]){
                container.getDataManager().setData(CAN_DERIVE, false);
                container.getDataManager().setData(CAN_SECOND_DERIVE, false);
            }
        }));
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event -> {
                    if(Arrays.asList(animations).contains(event.getAnimation())
                            && event.getAnimation() != animations[animations.length-1]
                                && event.getAnimation() != animations[animations.length-2]){
                        container.getDataManager().setData(CAN_DERIVE, true);
                        container.getDataManager().setData(DERIVE_TIMER, MAX_TIMER);
                    }
                }));
        super.onInitiate(container);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
    }

    /**
     * 非常暴力地减计数器
     */
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        event.player.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
            for(SkillContainer container : capabilitySkill.getSkillContainersFor(SkillCategories.WEAPON_INNATE)){
                if(container.getDataManager().hasData(DERIVE_TIMER)){
                    container.getDataManager().setData(DERIVE_TIMER, Math.max(container.getDataManager().getDataValue(DERIVE_TIMER)-1, 0));
                }
            }
        });
    }

    public static class Builder extends Skill.Builder<ChargedAttack> {

        protected ResourceLocation[] animations;
        protected WukongStyles style;

        public Builder() {
        }

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(Skill.ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Skill.Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public Builder setStyle(WukongStyles style) {
            this.style = style;
            return this;
        }

        public Builder setAnimations(ResourceLocation... animations) {
            this.animations = animations;
            return this;
        }
    }

}
