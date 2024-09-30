package com.p1nero.wukong.epicfight.skill.custom;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.animation.StaticAnimationProvider;
import com.p1nero.wukong.epicfight.skill.SkillDataRegister;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 劈棍重击
 */
public class PillarHeavyAttack extends WeaponInnateSkill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-22ed-a05b-0242ac114514");
    public static final int MAX_TIMER = Config.DERIVE_CHECK_TIME.get().intValue();//在此期间内再按才被视为衍生
    public static final SkillDataManager.SkillDataKey<Boolean> KEY_PRESSING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//技能键是否按下
    public static final SkillDataManager.SkillDataKey<Boolean> IS_REPEATING_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否处于长按一段衍生
    private static final SkillDataManager.SkillDataKey<Integer> CHARGED4_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//四段棍势持续时间
    public static final int MAX_CHARGED4_TICKS = 300;//15s
    private static final SkillDataManager.SkillDataKey<Integer> RED_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//亮灯时间
    public static final SkillDataManager.SkillDataKey<Integer> STARS_CONSUMED = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//本次攻击是否消耗星（是否强化）
    public static final SkillDataManager.SkillDataKey<Boolean> IS_CHARGING = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否正在蓄力
    public static final SkillDataManager.SkillDataKey<Integer> CHARGING_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//蓄力计时器
    public static final SkillDataManager.SkillDataKey<Integer> DERIVE_TIMER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);//衍生合法时间计时器
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_FIRST_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第一段衍生
    public static final SkillDataManager.SkillDataKey<Boolean> CAN_SECOND_DERIVE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否可以使用第二段衍生
    public static final SkillDataManager.SkillDataKey<Boolean> CANCEL_NEXT_CONSUMPTION = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);//是否取消下次棍势吸取
    protected final StaticAnimation[] animations;//0~4共有五种重击
    protected StaticAnimation deriveAnimation1;
    protected StaticAnimation deriveAnimation2;
    @Nullable
    protected StaticAnimation charging;
    @Nullable
    protected StaticAnimation chargePre;

    public static Builder createChargedAttack(){
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);
    }

    public PillarHeavyAttack(Builder builder) {
        super(builder);
        charging = builder.chargingAnimation.get();
        chargePre = builder.pre.get();

        this.animations = new StaticAnimation[builder.animationProviders.length];
        for(int i = 0; i < builder.animationProviders.length; i++) {
            WukongMoveset.LOGGER.info("loading heavy attack animations: {}", builder.animationProviders[i].get());
            this.animations[i] = builder.animationProviders[i].get();
        }

        deriveAnimation1 = builder.derive1.get();
        deriveAnimation2 = builder.derive2.get();
    }

    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link PillarHeavyAttack#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();

        if(!player.isOnGround()){
            //跳跃攻击

        } else {
            //如果用了星则要强化衍生
            boolean stackConsumed = container.getStack() > 0;
            dataManager.setDataSync(STARS_CONSUMED, container.getStack(), player);//0星也是星！
            if(dataManager.getDataValue(DERIVE_TIMER) > 0){
                this.setStackSynchronize(executer, container.getStack() - 1);//切手技直接消耗星，无需松手判断
                if(dataManager.getDataValue(CAN_FIRST_DERIVE) && stackConsumed){//有星才能用破棍式
                    //TODO 消耗星加特效buff
                    CompoundTag data = player.getMainHandItem().getOrCreateTag();

                    executer.playAnimationSynchronized(deriveAnimation1, 0.2F);
                }else if(dataManager.getDataValue(CAN_SECOND_DERIVE)){
                    if(stackConsumed){
                        //TODO 消耗星加特效buff
                    }
                    executer.playAnimationSynchronized(deriveAnimation2, 0.2F);
                }
            } else {
                //重击，消耗所有星，开始蓄力，松手在客户端判断
                if(!dataManager.getDataValue(IS_CHARGING)){
                    dataManager.setDataSync(CHARGING_TIMER, 0, player);
                    executer.playAnimationSynchronized(chargePre, 0.2F);
                }
            }

        }

        super.executeOnServer(executer, args);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        SkillDataManager manager = container.getDataManager();
        SkillDataRegister.register(manager, IS_REPEATING_DERIVE, false);
        SkillDataRegister.register(manager, KEY_PRESSING, false);
        SkillDataRegister.register(manager, CHARGED4_TIMER, 0);
        SkillDataRegister.register(manager, RED_TIMER, 0);
        SkillDataRegister.register(manager, STARS_CONSUMED, 0);
        SkillDataRegister.register(manager, IS_CHARGING, false);
        SkillDataRegister.register(manager, CHARGING_TIMER, 0);
        SkillDataRegister.register(manager, CAN_FIRST_DERIVE, false);
        SkillDataRegister.register(manager, CAN_SECOND_DERIVE, false);
        SkillDataRegister.register(manager, DERIVE_TIMER, 0);
        SkillDataRegister.register(manager, CANCEL_NEXT_CONSUMPTION, false);

        //长按期间禁止移动
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown()) {
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

        //普攻后立即右键可以衍生
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(player.getMainHandItem());
                    boolean isStaff = capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF);
                    List<StaticAnimation> autoAnimations = capabilityItem.getAutoAttckMotion(event.getPlayerPatch());
                    //autoAnimations 的倒一倒二是冲刺和跳跃攻击，倒三是第五段普攻
                    boolean isLightAttack = autoAnimations.contains(event.getAnimation()) && !event.getAnimation().equals(autoAnimations.get(autoAnimations.size()-1)) && !event.getAnimation().equals(autoAnimations.get(autoAnimations.size()-2));
                    boolean isLastLightAttack = autoAnimations.get(autoAnimations.size()-3).equals(event.getAnimation());
                    if(!isStaff){
                        return;
                    }
                    //释放普攻后重置可衍生时间
                    if(isLightAttack && !isLastLightAttack) {
                        container.getDataManager().setDataSync(CAN_FIRST_DERIVE, true, player);
                        container.getDataManager().setDataSync(DERIVE_TIMER, MAX_TIMER, player);
                    }

                }));

        //刷新四蓄计时器
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    if(container.isFull()){
                        container.getDataManager().setDataSync(CHARGED4_TIMER, MAX_CHARGED4_TICKS, player);
                    }
                }));
        super.onInitiate(container);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        PlayerEventListener listener = container.getExecuter().getEventListener();
        listener.removeListener(PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if(!dataManager.hasData(KEY_PRESSING)){
            dataManager.registerData(KEY_PRESSING);
        }
        if(container.getExecuter().isLogicalClient()){
            //KEY_PRESSING用于服务端判断是否继续播动画
            boolean isKeyDown = EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown();
            dataManager.setDataSync(KEY_PRESSING, isKeyDown, ((LocalPlayer) container.getExecuter().getOriginal()));
        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();

            //更新计时器
            dataManager.setDataSync(DERIVE_TIMER, Math.max(dataManager.getDataValue(DERIVE_TIMER) - 1, 0), serverPlayer);//切手技有效时间计算
            dataManager.setDataSync(RED_TIMER, Math.max(dataManager.getDataValue(RED_TIMER) - 1, 0), serverPlayer);//使用技能星数显示
            if(dataManager.getDataValue(DERIVE_TIMER) <= 0){
                dataManager.setDataSync(CAN_FIRST_DERIVE, false, serverPlayer);
                dataManager.setDataSync(CAN_SECOND_DERIVE, false, serverPlayer);
            }

            //蓄力的加条
            if(dataManager.getDataValue(IS_CHARGING)){
                int stackOld = container.getStack();
                this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() + Config.CHARGING_SPEED.get().floatValue());
                int stackNew = container.getStack();
                if(stackNew > stackOld){
//                    serverPlayerPatch.playAnimationSynchronized(); TODO 播对应的升星动画，生星动画里播对应的loop
                }
                //松手则清空棍势打重击
                if(!dataManager.getDataValue(KEY_PRESSING)){
                    dataManager.setDataSync(CANCEL_NEXT_CONSUMPTION, true, serverPlayer);//重击不加棍势
                    serverPlayerPatch.playAnimationSynchronized(animations[container.getStack()], 0.0F);//有几星就几星重击
                    dataManager.setDataSync(STARS_CONSUMED, container.getStack(), serverPlayer);//设置消耗星数，方便客户端绘制
                    this.setStackSynchronize(serverPlayerPatch, 0);
                    this.setConsumptionSynchronize(serverPlayerPatch, 1);
                    dataManager.setDataSync(RED_TIMER, MAX_TIMER, serverPlayer);//通知客户端该亮红灯了
                    dataManager.setDataSync(IS_CHARGING, false, serverPlayer);
                }
            }

            //破条则加stack清空蓄力条
            if(container.getStack() < 3 && Math.ceil(container.getResource(1.0F) * 20) > 10) {
                this.setConsumptionSynchronize( serverPlayerPatch, 1);
                this.setStackSynchronize( serverPlayerPatch, container.getStack() + 1);
                container.getExecuter().playSound(SoundEvents.EXPERIENCE_ORB_PICKUP,1.0F, 1.0F);
            }

            int current = dataManager.getDataValue(CHARGED4_TIMER);
            //四蓄的时间判断
            if(container.isFull() && current > 0){
                dataManager.setDataSync(CHARGED4_TIMER, current - 1, serverPlayer);
            }
            if(current == 1){
                this.setStackSynchronize(serverPlayerPatch, 3);
                this.setConsumptionSynchronize(serverPlayerPatch, container.getMaxResource() - Config.CHARGING_SPEED.get().floatValue());
            }
            if(current == 0 && container.getStack() >= 3 && container.getResource() > Config.CHARGING_SPEED.get().floatValue() / 2 + 0.1){
                this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() - Config.CHARGING_SPEED.get().floatValue() / 2);
            }

        }



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
        int style = ((StaffStance) container.getExecuter().getSkill(WukongSkillSlots.STAFF_STYLE).getSkill()).style.ordinal();
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
            int star = Math.max(1,Math.min(container.getDataManager().getDataValue(STARS_CONSUMED), 3));
            ResourceLocation lightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/" + star + ".png");//及时获取stack，新鲜的，热乎的
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

    public static class Builder extends Skill.Builder<PillarHeavyAttack> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider derive1;
        protected StaticAnimationProvider derive2;
        StaticAnimationProvider chargingAnimation;
        StaticAnimationProvider pre;

        public Builder() {
        }

        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        public Builder setActivateType(ActivateType activateType) {
            this.activateType = activateType;
            return this;
        }

        public Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }

        public Builder setChargingAnimation(StaticAnimationProvider chargingAnimation) {
            this.chargingAnimation = chargingAnimation;
            return this;
        }

        public Builder setChargePreAnimation(StaticAnimationProvider pre) {
            this.pre = pre;
            return this;
        }

        /**
         * 0~4星重击
         */
        public Builder setHeavyAttacks(StaticAnimationProvider... animationProviders) {
            this.animationProviders = animationProviders;
            return this;
        }

        /**
         * 如果是可长按的衍生则derive1就是pre动画，具体逻辑在动画那里判断
         */
        public Builder setDeriveAnimations(StaticAnimationProvider derive1, StaticAnimationProvider derive2) {
            this.derive1 = derive1;
            this.derive2 = derive2;
            return this;
        }
    }

}
