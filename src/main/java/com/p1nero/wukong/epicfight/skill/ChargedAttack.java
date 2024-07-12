package com.p1nero.wukong.epicfight.skill;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.WukongStyles;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Arrays;
import java.util.UUID;

/**
 * 重击，根据层数不同使用不同动作
 */
public class ChargedAttack extends WeaponInnateSkill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114514");
    public static final int MAX_TIMER = 20;
    private static boolean shouldRepeatDerive1;
    private static final SkillDataManager.SkillDataKey<Boolean> REPEATING_DERIVE1 = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final SkillDataManager.SkillDataKey<Boolean> STACK_CONSUMED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    private static final SkillDataManager.SkillDataKey<Boolean> CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    protected final StaticAnimation[] animations;//倒一是衍生1，倒二是衍生2，0~4是五种重击
    protected final StaticAnimation deriveAnimation1;
    protected final StaticAnimation deriveAnimation2;

    public static Builder createChargedAttack(){
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE)
                //default animations for test
                .setAnimations(Animations.SWEEPING_EDGE,
                        Animations.DANCING_EDGE,
                        Animations.SPEAR_ONEHAND_AIR_SLASH,
                        Animations.SWORD_DASH,
                        Animations.AXE_AIRSLASH,
                        Animations.SPEAR_GUARD,
                        Animations.SPEAR_DASH);
//                .setAnimationLocations(new ResourceLocation("epicfight", "biped/combat/fist_auto1"),
//                        new ResourceLocation("epicfight", "biped/combat/fist_auto2"),
//                        new ResourceLocation("epicfight", "biped/combat/fist_auto3"),
//                        new ResourceLocation("epicfight", "biped/combat/fist_dash"),
//                        new ResourceLocation("epicfight", "biped/combat/fist_air_slash"),
//                        new ResourceLocation("epicfight", "biped/combat/sweeping_edge"),
//                        new ResourceLocation("epicfight", "biped/combat/dancing_edge"));
    }

    public ChargedAttack(Builder builder) {
        super(builder);
        shouldRepeatDerive1 = builder.repeatDerive1;
        if(builder.animations == null){
            this.animations = new StaticAnimation[builder.animationLocations.length];
            for(int i = 0; i < builder.animationLocations.length; ++i) {
                WukongMoveset.LOGGER.info("loading charged attack animations: {}", builder.animationLocations[i].toString());
                this.animations[i] = EpicFightMod.getInstance().animationManager.findAnimationByPath(builder.animationLocations[i].toString());
            }
        }else {
            this.animations = builder.animations;
        }

        deriveAnimation1 = animations[animations.length-1];
        deriveAnimation2 = animations[animations.length-2];

    }

    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link ChargedAttack#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        //如果用了豆则要强化衍生
        boolean stackConsumed = container.getStack() > 0;
        dataManager.setData(STACK_CONSUMED, container.getStack() > 0);

        if(dataManager.getDataValue(CAN_FIRST_DERIVE)){
            if(stackConsumed){
                //TODO 消耗星加特效buff
            }
            executer.playAnimationSynchronized(deriveAnimation1, 0.2F);
        }else if(dataManager.getDataValue(CAN_SECOND_DERIVE)){
            if(stackConsumed){
                //TODO 消耗星加特效buff
            }
            executer.playAnimationSynchronized(deriveAnimation2, 0.2F);
        }else if(dataManager.getDataValue(DERIVE_TIMER) == 0){
            //重击，消耗所有棍势
            executer.playAnimationSynchronized(animations[container.getStack()], 0.2F);
            switch (container.getStack()){
                //TODO 根据棍势数量加特效和buff
            }
            this.setStackSynchronize(executer, 0);
        }
        super.executeOnServer(executer, args);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        container.getDataManager().registerData(REPEATING_DERIVE1);
        container.getDataManager().registerData(STACK_CONSUMED);
        container.getDataManager().registerData(CAN_FIRST_DERIVE);
        container.getDataManager().registerData(CAN_SECOND_DERIVE);
        container.getDataManager().registerData(DERIVE_TIMER);

        //进行衍生阶段的重置
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
            if(event.getAnimation().equals(deriveAnimation1)){
                //用于客户端判断持续衍生1
                if(shouldRepeatDerive1){
                    container.getDataManager().setDataSync(REPEATING_DERIVE1,true, event.getPlayerPatch().getOriginal());
                }
                container.getDataManager().setData(CAN_FIRST_DERIVE, false);
            }
            if(event.getAnimation().equals(deriveAnimation2)){
                container.getDataManager().setData(CAN_SECOND_DERIVE, false);
            }
        }));

        //如果不是衍生2和第五段重击则属于可以衍生阶段并重置衍生计算时间
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event -> {
                    if(Arrays.asList(animations).contains(event.getAnimation())){
                        container.getDataManager().setData(STACK_CONSUMED, false);
                        if(!event.getAnimation().equals(deriveAnimation2)
                                    && !event.getAnimation().equals(animations[animations.length-3])){
                            if(event.getAnimation().equals(deriveAnimation1)){
                                container.getDataManager().setData(CAN_SECOND_DERIVE, true);
                            } else {
                                container.getDataManager().setData(CAN_FIRST_DERIVE, true);
                            }
                            container.getDataManager().setData(DERIVE_TIMER, MAX_TIMER);
                        }
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

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        //如果按着技能键而且在衍生期间则一直循环，否则结束循环状态
        if(container.getExecuter().isLogicalClient()){
            if(isRepeatingDerive1(container) && EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown()){
                container.getExecuter().playAnimationSynchronized(deriveAnimation1,0);
            } else {
                container.getDataManager().setData(REPEATING_DERIVE1, false);
            }
        }
        //更新计时器
        container.getDataManager().setData(DERIVE_TIMER, Math.max(container.getDataManager().getDataValue(DERIVE_TIMER)-1, 0));
    }

    public static boolean isRepeatingDerive1(SkillContainer container){
        return container.getDataManager().getDataValue(REPEATING_DERIVE1);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return false;
    }

    /**
     * 根据棍式和星级画图
     * @deprecated 迁移到 {@link com.p1nero.wukong.mixin.BattleModeGuiMixin}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    @Deprecated
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        int stack = container.getStack();
        int style = ((StaffStyle) container.getExecuter().getSkill(WukongSkillSlots.STAFF_STYLE).getSkill()).style.ordinal();
        poseStack.pushPose();
        poseStack.translate(0.0, gui.getSlidingProgression(), 0.0);
        ResourceLocation currentStyle = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/"+style+"_"+stack+".png");
        RenderSystem.setShaderTexture(0, currentStyle);
        GuiComponent.blit(poseStack, (int)x, (int)y, 24, 24, 0.0F, 0.0F, 1, 1, 1, 1);
    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
    }

    public static boolean shouldRepeatDerive1() {
        return shouldRepeatDerive1;
    }

    public static class Builder extends Skill.Builder<ChargedAttack> {

        protected ResourceLocation[] animationLocations;
        protected StaticAnimation[] animations;
        protected WukongStyles style;
        protected boolean repeatDerive1;

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

        public Builder shouldRepeatDerive1(boolean repeatDerive1){
            this.repeatDerive1 = repeatDerive1;
            return this;
        }

        /**
         * 很奇怪用不了史诗战斗原版的动作
         */
        public Builder setAnimationLocations(ResourceLocation... animationLocations) {
            this.animationLocations = animationLocations;
            return this;
        }

        /**
         * 不知道为什么会变null
         * @deprecated
         */
        @Deprecated
        public Builder setAnimations(StaticAnimation... animations) {
            this.animations = animations;
            return this;
        }

    }

}
