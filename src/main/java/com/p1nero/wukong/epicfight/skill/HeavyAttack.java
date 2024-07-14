package com.p1nero.wukong.epicfight.skill;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.ActionEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 重击，根据层数不同使用不同动作
 */
public class HeavyAttack extends WeaponInnateSkill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114514");
    public static final int MAX_TIMER = 20;//在此期间内再按才被视为衍生
    private static boolean shouldRepeatDerive1;
    private static boolean chargeable;
    private static final SkillDataManager.SkillDataKey<Integer> RED_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    public static final SkillDataManager.SkillDataKey<Integer> STARTS_CONSUMED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    public static final SkillDataManager.SkillDataKey<Boolean> IS_CHARGING_PRE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final SkillDataManager.SkillDataKey<Boolean> IS_CHARGING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    public static final SkillDataManager.SkillDataKey<Integer> CHARGING_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    private static final SkillDataManager.SkillDataKey<Boolean> IS_REPEATING_DERIVE1 = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
    private static final SkillDataManager.SkillDataKey<Boolean> CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    protected final StaticAnimation[] animations;//倒一是衍生1，倒二是衍生2，0~4是五种重击
    protected final StaticAnimation deriveAnimation1;
    protected final StaticAnimation deriveAnimation2;
    @Nullable
    protected final StaticAnimation charging;
    @Nullable
    protected StaticAnimation pre;

    public static Builder createChargedAttack(){
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE)
                //default animations for test
                .setAnimationLocation(new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_forward"),
                        new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_backward"),
                        new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_left"),
                        new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_right"),
                        new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_forward"),
                        new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_backward"),
                        new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_left"),
                        new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_right"));
    }

    public HeavyAttack(Builder builder) {
        super(builder);
        shouldRepeatDerive1 = builder.repeatDerive1;
        chargeable = builder.chargeable;
        if(chargeable){
            charging = builder.chargingAnimation.get();
            pre = builder.pre.get();
        }else {
            charging = null;
            pre = null;
        }
        if(builder.animationProviders == null){
            this.animations = new StaticAnimation[builder.animationLocations.length];
            for(int i = 0; i < builder.animationLocations.length; i++) {
                WukongMoveset.LOGGER.info("loading heavy attack animations from resource location: {}", builder.animationLocations[i].toString());
                this.animations[i] = EpicFightMod.getInstance().animationManager.findAnimationByPath(builder.animationLocations[i].toString());
            }
        } else {
            this.animations = new StaticAnimation[builder.animationProviders.length];
            for(int i = 0; i < builder.animationProviders.length; i++) {
                WukongMoveset.LOGGER.info("loading heavy attack animations: {}", builder.animationProviders[i].get());
                this.animations[i] = builder.animationProviders[i].get();
            }
        }

        deriveAnimation1 = animations[animations.length-1];
        deriveAnimation2 = animations[animations.length-2];
    }

    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link HeavyAttack#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        //如果用了星则要强化衍生
        boolean stackConsumed = container.getStack() > 0;
        if(stackConsumed){
            dataManager.setDataSync(RED_TIMER, MAX_TIMER, player);
        }
        dataManager.setDataSync(STARTS_CONSUMED, container.getStack(), player);//0星也是星！
        if(dataManager.getDataValue(DERIVE_TIMER) > 0){
            if(dataManager.getDataValue(CAN_FIRST_DERIVE)){
                if(stackConsumed){
                    //TODO 消耗星加特效buff
                }
                executer.playAnimationSynchronized(deriveAnimation1, 0.2F);
                executer.getEventListener().triggerEvents(PlayerEventListener.EventType.ACTION_EVENT_SERVER, new ActionEvent<>(executer, deriveAnimation1));//TODO 删掉
            }else if(dataManager.getDataValue(CAN_SECOND_DERIVE)){
                if(stackConsumed){
                    //TODO 消耗星加特效buff
                }
                executer.playAnimationSynchronized(deriveAnimation2, 0.2F);
                executer.getEventListener().triggerEvents(PlayerEventListener.EventType.ACTION_EVENT_SERVER, new ActionEvent<>(executer, deriveAnimation2));//TODO 删掉
            }
        } else {
            switch (container.getStack()){
                //TODO 根据棍势数量加特效和buff
            }
            //重击，消耗所有星
            if(chargeable && !dataManager.getDataValue(IS_CHARGING)){
                //开始蓄力，松手在客户端判断
                dataManager.setDataSync(CHARGING_TIMER, 0, player);
                dataManager.setDataSync(IS_CHARGING_PRE, true, player);
                dataManager.setDataSync(IS_CHARGING, true, player);
                executer.playAnimationSynchronized(pre, 0.2F);
            } else {
                executer.playAnimationSynchronized(animations[container.getStack()], 0.2F);
            }
            this.setStackSynchronize(executer, 0);
        }

        super.executeOnServer(executer, args);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        container.getDataManager().registerData(RED_TIMER);
        container.getDataManager().registerData(STARTS_CONSUMED);
        container.getDataManager().registerData(IS_CHARGING_PRE);
        container.getDataManager().registerData(IS_CHARGING);
        container.getDataManager().registerData(CHARGING_TIMER);
        container.getDataManager().registerData(IS_REPEATING_DERIVE1);
        container.getDataManager().registerData(CAN_FIRST_DERIVE);
        container.getDataManager().registerData(CAN_SECOND_DERIVE);
        container.getDataManager().registerData(DERIVE_TIMER);

        //前三星不能破条
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event -> {
                    if(container.getStack() < 3 && Math.ceil(container.getResource(1.0F) * 20) > 10){
                        this.setConsumptionSynchronize(event.getPlayerPatch(), 1);
                        this.setStackSynchronize(event.getPlayerPatch(), container.getStack()+1);
                    }
                }));

        //进行衍生阶段的重置
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    SkillDataManager dataManager = container.getDataManager();
                    if(event.getAnimation().equals(deriveAnimation1)){
                        //用于客户端判断是否要持续衍生1
                        if(shouldRepeatDerive1 && !dataManager.getDataValue(IS_REPEATING_DERIVE1)){
                            dataManager.setDataSync(IS_REPEATING_DERIVE1,true, player);
                        }
                        dataManager.setData(CAN_FIRST_DERIVE, false);
                    } else if(event.getAnimation().equals(deriveAnimation2)){
                        dataManager.setData(CAN_SECOND_DERIVE, false);
                    }
                }));

        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(player.getMainHandItem());
                    boolean isStaff = capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF);
                    List<StaticAnimation> autoAnimations = capabilityItem.getAutoAttckMotion(event.getPlayerPatch());
                    boolean isLightAttack = autoAnimations.contains(event.getAnimation());
                    boolean isLastLightAttack = autoAnimations.get(autoAnimations.size()-3).equals(event.getAnimation());
                    if(!isStaff){
                        return;
                    }
                    //重置属于前摇的时间 （可惜没有普通动画的结束事件）
                    if(chargeable && event.getAnimation().equals(pre)){
                        container.getDataManager().setDataSync(IS_CHARGING_PRE, false, player);
                    }
                    //如是轻击和衍生1则属于可以衍生阶段并重置衍生计算时间
                    if(isLightAttack && !isLastLightAttack) {
                        container.getDataManager().setData(CAN_FIRST_DERIVE, true);
                        container.getDataManager().setDataSync(DERIVE_TIMER, MAX_TIMER, player);
                    }
                    if(event.getAnimation().equals(deriveAnimation1)){
                        container.getDataManager().setData(CAN_SECOND_DERIVE, true);
                        container.getDataManager().setDataSync(DERIVE_TIMER, MAX_TIMER, player);
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
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        //如果按着技能键而且在衍生或蓄力期间则一直循环，否则结束循环状态
        if(container.getExecuter().isLogicalClient()
//                && !container.getExecuter().getEntityState().inaction()
        ){
            SkillDataManager dataManager = container.getDataManager();
            if(dataManager.getDataValue(IS_REPEATING_DERIVE1)){
                if(EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown()){
                    container.getExecuter().playAnimationSynchronized(deriveAnimation1,0);
                } else {
                    dataManager.setData(IS_REPEATING_DERIVE1, false);
                }
            }
            if(dataManager.getDataValue(IS_CHARGING)){
                if(EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown()){
                    container.getDataManager().setData(CHARGING_TIMER, container.getDataManager().getDataValue(CHARGING_TIMER)+1);
                    if(!dataManager.getDataValue(IS_CHARGING_PRE) && !container.getExecuter().getEntityState().inaction()){
                        container.getExecuter().playAnimationSynchronized(charging,0);
                    }
                }else {
                    container.getExecuter().getEntityState().setState(EntityState.INACTION, false);
                    container.getExecuter().playAnimationSynchronized(animations[dataManager.getDataValue(STARTS_CONSUMED)], 0.0F);
                    container.getDataManager().setDataSync(IS_CHARGING, false, ((LocalPlayer) container.getExecuter().getOriginal()));
                }
            }
        }
        //更新计时器
        container.getDataManager().setData(DERIVE_TIMER, Math.max(container.getDataManager().getDataValue(DERIVE_TIMER)-1, 0));
        container.getDataManager().setData(RED_TIMER, Math.max(container.getDataManager().getDataValue(RED_TIMER)-1, 0));
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldDraw(SkillContainer container) {
        if(Minecraft.getInstance().player != null){
            return EpicFightCapabilities.getItemStackCapability(Minecraft.getInstance().player.getMainHandItem()).getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF);
        }
        return false;
    }

    /**
     * 根据棍式和星级画图
     * 取消原本的绘制在 {@link com.p1nero.wukong.mixin.BattleModeGuiMixin}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, PoseStack poseStack, float x, float y) {
        int stack = container.getStack();
        int style = ((StaffStyle) container.getExecuter().getSkill(WukongSkillSlots.STAFF_STYLE).getSkill()).style.ordinal();
        float cooldownRatio = !container.isFull() && !container.isActivated() ? container.getResource(1.0F) : 1.0F;
        int progress = ((int) Math.ceil(cooldownRatio * 20));
        ConfigurationIngame config = EpicFightMod.CLIENT_INGAME_CONFIG;
        Window sr = Minecraft.getInstance().getWindow();
        int width = sr.getGuiScaledWidth();
        int height = sr.getGuiScaledHeight();
        Vec2i pos = config.getWeaponInnatePosition(width, height);
        poseStack.pushPose();
        poseStack.translate(0.0, gui.getSlidingProgression(), 0.0);
        ResourceLocation progressTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/progress/" + progress + ".png");
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/style/" + style + (stack==0?"_0":"_1") + ".png");
        ResourceLocation stackTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stack/" + stack + ".png");
        ResourceLocation goldenLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/gold.png");
        RenderSystem.setShaderTexture(0, progressTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        RenderSystem.setShaderTexture(0, styleTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        RenderSystem.setShaderTexture(0, stackTexture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);

        if(container.getDataManager().getDataValue(RED_TIMER) > 0){
            int start = Math.max(1,Math.min(container.getDataManager().getDataValue(STARTS_CONSUMED), 3));
            ResourceLocation lightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/" + start + ".png");//及时获取stack，新鲜的，热乎的
            RenderSystem.setShaderTexture(0, lightTexture);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        }

        if(container.isFull()){
            RenderSystem.setShaderTexture(0, goldenLightTexture);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            GuiComponent.blit(poseStack, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        }


    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
    }

    public static boolean shouldRepeatDerive1() {
        return shouldRepeatDerive1;
    }

    public static class Builder extends Skill.Builder<HeavyAttack> {

        protected ResourceLocation[] animationLocations;
        @Nullable
        protected StaticAnimationProvider[] animationProviders;
        @Nullable
        StaticAnimationProvider chargingAnimation;
        @Nullable
        StaticAnimationProvider pre;
        protected WukongStyles style;
        protected boolean repeatDerive1;
        protected boolean chargeable;

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

        public Builder setChargingAnimation(StaticAnimationProvider chargingAnimation) {
            this.chargeable = chargingAnimation != null;
            this.chargingAnimation = chargingAnimation;
            return this;
        }

        public Builder setChargePreAnimation(StaticAnimationProvider pre) {
            this.chargeable = pre != null;
            this.pre = pre;
            return this;
        }

        /**
         * 前面五个是0~5星，倒一是衍生1，倒二是衍生2，倒三是轻击最后一段
         */
        public Builder setAnimationLocation(ResourceLocation... animationLocations) {
            this.animationLocations = animationLocations;
            return this;
        }

        /**
         * 前面五个是0~5星，倒一是衍生1，倒二是衍生2，倒三是轻击最后一段
         */
        public Builder setAnimationProviders(StaticAnimationProvider... animationProviders) {
            this.animationProviders = animationProviders;
            return this;
        }

    }

}
