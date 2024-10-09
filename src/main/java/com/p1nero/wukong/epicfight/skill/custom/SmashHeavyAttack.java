package com.p1nero.wukong.epicfight.skill.custom;

import com.mojang.blaze3d.platform.Window;
import com.p1nero.wukong.Config;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.animation.custom.WukongDodgeAnimation;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec2i;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.*;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.List;
import java.util.UUID;

/**
 * 劈棍重击
 */
public class SmashHeavyAttack extends WeaponInnateSkill {

    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114514");
    protected final StaticAnimationProvider[] animations;//0~4共有五种重击
    public static final int MAX_CHARGED4_TICKS = 300;//15s
    protected StaticAnimationProvider deriveAnimation1;
    protected StaticAnimationProvider deriveAnimation2;
    @NotNull
    protected StaticAnimationProvider jumpAttackHeavy;
    @NotNull
    protected StaticAnimationProvider charging;
    @NotNull
    protected StaticAnimationProvider chargePre;

    public static Builder createChargedAttack(){
        return new Builder().setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.NONE);
    }

    public SmashHeavyAttack(Builder builder) {
        super(builder);
        charging = builder.chargingAnimation;
        chargePre = builder.pre;

        this.animations = builder.animationProviders;

        deriveAnimation1 = builder.derive1;
        deriveAnimation2 = builder.derive2;
        jumpAttackHeavy = builder.jumpAttackHeavy;
    }


    /**
     * 在计时周期内使用技能才算使用衍生，否则视为重击
     * 长按循环第一段衍生的判断在{@link SmashHeavyAttack#updateContainer(SkillContainer)}
     */
    @Override
    public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
        SkillContainer container = executer.getSkill(SkillSlots.WEAPON_INNATE);
        SkillDataManager dataManager = container.getDataManager();
        ServerPlayer player = executer.getOriginal();
        dataManager.setDataSync(WukongSkillDataKeys.STARS_CONSUMED.get(), container.getStack(), player);//0星也是星！
        if(dataManager.getDataValue(WukongSkillDataKeys.CAN_JUMP_HEAVY.get())){
            dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);//放里面，防止瞎按技能键就防坠机的bug
            //跳跃攻击，也消耗所有棍势
            dataManager.setDataSync(WukongSkillDataKeys.CAN_JUMP_HEAVY.get(), false, player);
            if(container.getStack() > 0){//0星是null会中断
                executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
            }
            executer.playAnimationSynchronized(jumpAttackHeavy.get(), 0.15F);
            resetConsumption(container, executer, false);
        } else if(player.onGround()){
            //如果用了星则要强化衍生
            boolean stackConsumed = container.getStack() > 0;
            if(dataManager.getDataValue(WukongSkillDataKeys.DERIVE_TIMER.get()) > 0 && stackConsumed && !container.isFull()){//有星才能用破棍式，且满星直接放大（也防bug）
                if(dataManager.getDataValue(WukongSkillDataKeys.CAN_FIRST_DERIVE.get())){
                    dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimation1.get(), 0.2F);
                }else if(dataManager.getDataValue(WukongSkillDataKeys.CAN_SECOND_DERIVE.get())){
                    dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);
                    executer.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                    this.setStackSynchronize(executer, container.getStack() - 1);
                    executer.playAnimationSynchronized(deriveAnimation2.get(), 0.2F);
                }
            } else {
                //重击，消耗所有星，开始蓄力，松手在客户端判断
                if(!dataManager.getDataValue(WukongSkillDataKeys.IS_CHARGING.get())){
                    executer.playAnimationSynchronized(chargePre.get(), 0.2F);
                }
            }

        }

        super.executeOnServer(executer, args);
    }

    /**
     * 清空耐力并播红光和音效
     * @param playSound 如果是通过蓄力而释放的就不播音效
     */
    private void resetConsumption(SkillContainer container, ServerPlayerPatch executer, boolean playSound){
        if(playSound && container.getStack() > 0){
            int cnt = container.getStack();
            new Thread(()->{
                for(int i = 0; i < cnt; i++){
                    executer.playSound(WuKongSounds.stackSounds.get(i).get(), 1, 1);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        WukongMoveset.LOGGER.error("interrupted when play stack sounds!", e);
                    }
                }
            }).start();
        } else {
            container.getDataManager().setDataSync(WukongSkillDataKeys.PLAY_SOUND.get(), true, executer.getOriginal());
        }
        container.getDataManager().setDataSync(WukongSkillDataKeys.RED_TIMER.get(), Config.DERIVE_CHECK_TIME.get().intValue(), executer.getOriginal());//通知客户端该亮红灯了
        this.setStackSynchronize(executer, 0);
        this.setConsumptionSynchronize(executer, 1);
    }

    @Override
    public void onInitiate(SkillContainer container) {

        //长按期间禁止跳跃
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event -> {
            if (event.getPlayerPatch().isBattleMode() && EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown()) {
                Input input = event.getMovementInput();
                input.jumping = false;
            }
        }));

        //成功识破加棍势，并重置普攻计数器，下次从三段普攻开始
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
            if(container.getDataManager().getDataValue(WukongSkillDataKeys.IS_IN_SPECIAL_ATTACK.get())){
                container.getSkill().setConsumptionSynchronize(event.getPlayerPatch(), container.getResource() + 10);//获得棍势
                BasicAttack.setComboCounterWithEvent(ComboCounterHandleEvent.Causal.ANOTHER_ACTION_ANIMATION, event.getPlayerPatch(), event.getPlayerPatch().getSkill(SkillSlots.BASIC_ATTACK), deriveAnimation1.get(), 2);
                event.setAmount(0);
                event.setCanceled(true);
            }

            float damageReduce = container.getDataManager().getDataValue(WukongSkillDataKeys.DAMAGE_REDUCE.get());
            //霸体
            if(damageReduce > 0){
                if(event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource){
                    epicFightDamageSource.setStunType(StunType.NONE);
                }
                event.setAmount(event.getAmount() * (1 - damageReduce));
                //TODO 减伤
//                LivingEntityPatch<?> attackerPatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
//                this.processDamage(event.getPlayerPatch(), event.getDamageSource(), AttackResult.ResultType.SUCCESS,(1 - damageReduce) * event.getAmount(), attackerPatch);
                event.setResult(AttackResult.ResultType.MISSED);
                event.setCanceled(true);
            }

            //防止坠机
            if (event.getDamageSource().is(DamageTypes.FALL) && container.getDataManager().getDataValue(WukongSkillDataKeys.PROTECT_NEXT_FALL.get())) {
                event.setAmount(0);
                event.setCanceled(true);
                container.getDataManager().setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), false);
            }
        }));

        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event -> {
                    ServerPlayerPatch serverPlayerPatch = event.getPlayerPatch();
                    ServerPlayer player = serverPlayerPatch.getOriginal();
                    CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(player.getMainHandItem());
                    if(!WukongWeaponCategories.isWeaponValid(event.getPlayerPatch())){
                        return;
                    }

                    //蓄力的时候做动作是非法的，应该清空棍势
                    if(container.getDataManager().getDataValue(WukongSkillDataKeys.IS_CHARGING.get()) && !event.getAnimation().equals(chargePre.get()) && !(event.getAnimation() instanceof WukongDodgeAnimation)){
                        this.setConsumptionSynchronize(serverPlayerPatch, 1);
                        this.setStackSynchronize(serverPlayerPatch, 0);
                        container.getDataManager().setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), false, player);
                    }

                    //普攻后立即右键可以衍生
                    List<AnimationProvider<?>> autoAnimations = capabilityItem.getAutoAttckMotion(event.getPlayerPatch());
                    for(int i = 0; i < autoAnimations.size(); i++){
                        if(autoAnimations.get(i).get().equals(event.getAnimation()) && i < 4){
                            container.getDataManager().setDataSync(WukongSkillDataKeys.CAN_FIRST_DERIVE.get(), true, player);
                            container.getDataManager().setDataSync(WukongSkillDataKeys.DERIVE_TIMER.get(), Config.DERIVE_CHECK_TIME.get().intValue(), player);
                            return;
                        }
                    }
                }));

        //刷新四蓄计时器，识破打中则可接二段
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_DAMAGE, EVENT_UUID, (event -> {
                    ServerPlayer player = event.getPlayerPatch().getOriginal();
                    if(container.isFull()){
                        container.getDataManager().setDataSync(WukongSkillDataKeys.CHARGED4_TIMER.get(), MAX_CHARGED4_TICKS, player);
                    }
                    if(event.getDamageSource().getAnimation().equals(deriveAnimation1)){
                        container.getDataManager().setDataSync(WukongSkillDataKeys.CAN_SECOND_DERIVE.get(), true, player);
                        container.getDataManager().setDataSync(WukongSkillDataKeys.DERIVE_TIMER.get(), Config.DERIVE_CHECK_TIME.get().intValue(), player);
                    }
                }));

        //根据星数改跳跃重击和破、斩棍式伤害
        container.getExecuter().getEventListener().addEventListener(
                PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_ATTACK, EVENT_UUID, (event -> {
                    int starCnt = container.getDataManager().getDataValue(WukongSkillDataKeys.STARS_CONSUMED.get());
                    if(event.getDamageSource().getAnimation().equals(jumpAttackHeavy)){
                        float mul = switch (starCnt) {
                            case 1 -> 3;
                            case 2 -> 4.5F;
                            case 3 -> 6.2F;
                            case 4 -> 8.75F;
                            default -> 1.45F;
                        };
                        event.getDamageSource().setDamageModifier(ValueModifier.multiplier(mul));
                    } else if(event.getDamageSource().getAnimation().equals(deriveAnimation1)){
                        float mul = starCnt == 0 ? 1.0F : 1.96F;
                        event.getDamageSource().setDamageModifier(ValueModifier.multiplier(mul));
                    } else if(event.getDamageSource().getAnimation().equals(deriveAnimation2)){
                        float mul = switch (starCnt) {
                            case 1 -> 4.7F;
                            case 2 -> 4.9F;
                            case 3, 4 -> 5.1F;
                            default -> 4.48F;
                        };
                        event.getDamageSource().setDamageModifier(ValueModifier.multiplier(mul));
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
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_ATTACK, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.DEALT_DAMAGE_EVENT_DAMAGE, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
        listener.removeListener(PlayerEventListener.EventType.FALL_EVENT, EVENT_UUID);
    }

    /**
     * copy from {@link yesman.epicfight.events.EntityEvents#attackEvent(LivingAttackEvent)}
     */
    public void processDamage(PlayerPatch<?> playerPatch, DamageSource damageSource, AttackResult.ResultType attackResult, float amount, @Nullable LivingEntityPatch<?> attackerPatch){
        AttackResult result = playerPatch != null ? AttackResult.of(attackResult, amount) : AttackResult.success(amount);

        if (attackerPatch != null) {
            attackerPatch.setLastAttackResult(result);
        }
        EpicFightDamageSource deflictedDamage = EpicFightDamageSources.copy(damageSource);
        deflictedDamage.addRuntimeTag(EpicFightDamageType.PARTIAL_DAMAGE);
        if(playerPatch != null){
            playerPatch.getOriginal().hurt(deflictedDamage, result.damage);
        }
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        SkillDataManager dataManager = container.getDataManager();
        if(container.getExecuter().isLogicalClient()){
            //KEY_PRESSING用于服务端判断是否继续播动画
            boolean isKeyDown = EpicFightKeyMappings.WEAPON_INNATE_SKILL.isDown();
            dataManager.setDataSync(WukongSkillDataKeys.KEY_PRESSING.get(), isKeyDown, ((LocalPlayer) container.getExecuter().getOriginal()));
        } else {
            ServerPlayerPatch serverPlayerPatch = ((ServerPlayerPatch) container.getExecuter());
            ServerPlayer serverPlayer = serverPlayerPatch.getOriginal();

            //层数变化检测以播音效
            if(container.getStack() > dataManager.getDataValue(WukongSkillDataKeys.LAST_STACK.get()) && dataManager.getDataValue(WukongSkillDataKeys.IS_CHARGING.get())){
                serverPlayerPatch.playSound(WuKongSounds.stackSounds.get(container.getStack() - 1).get(), 1, 1);
                dataManager.setDataSync(WukongSkillDataKeys.PLAY_SOUND.get(), false, serverPlayer);
            }
            dataManager.setData(WukongSkillDataKeys.LAST_STACK.get(), container.getStack());

            //跳重击的判断
            if(!serverPlayer.onGround()){
                dataManager.setDataSync(WukongSkillDataKeys.CAN_JUMP_HEAVY.get(), true, serverPlayer);
            } else if(dataManager.getDataValue(WukongSkillDataKeys.CAN_JUMP_HEAVY.get())){
                dataManager.setDataSync(WukongSkillDataKeys.CAN_JUMP_HEAVY.get(), false, serverPlayer);
            }

            //更新计时器
            dataManager.setDataSync(WukongSkillDataKeys.DERIVE_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.DERIVE_TIMER.get()) - 1, 0), serverPlayer);//切手技有效时间计算
            dataManager.setDataSync(WukongSkillDataKeys.RED_TIMER.get(), Math.max(dataManager.getDataValue(WukongSkillDataKeys.RED_TIMER.get()) - 1, 0), serverPlayer);//使用技能星数显示
            if(dataManager.getDataValue(WukongSkillDataKeys.DERIVE_TIMER.get()) <= 0){
                dataManager.setDataSync(WukongSkillDataKeys.CAN_FIRST_DERIVE.get(), false, serverPlayer);
                dataManager.setDataSync(WukongSkillDataKeys.CAN_SECOND_DERIVE.get(), false, serverPlayer);
            }

            if(dataManager.getDataValue(WukongSkillDataKeys.IS_CHARGING.get())){
                //防止切物品产生的bug
                if(!WukongWeaponCategories.isWeaponValid(serverPlayerPatch)){
                    dataManager.setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), false, serverPlayer);
                    this.setConsumptionSynchronize(serverPlayerPatch, 1);
                    this.setStackSynchronize(serverPlayerPatch, 0);
                    return;
                }
                //蓄力的加条
                if(container.getStack() < 3){
                    this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() + Config.CHARGING_SPEED.get().floatValue());
                }
                //松手则清空棍势打重击
                if(!dataManager.getDataValue(WukongSkillDataKeys.KEY_PRESSING.get())){
                    dataManager.setDataSync(WukongSkillDataKeys.IS_CHARGING.get(), false, serverPlayer);
                    dataManager.setData(WukongSkillDataKeys.PROTECT_NEXT_FALL.get(), true);//MAN
                    serverPlayerPatch.playAnimationSynchronized(animations[container.getStack()].get(), 0.0F);//有几星就几星重击
                    dataManager.setDataSync(WukongSkillDataKeys.STARS_CONSUMED.get(), container.getStack(), serverPlayer);//设置消耗星数，方便客户端绘制
                    resetConsumption(container, serverPlayerPatch, dataManager.getDataValue(WukongSkillDataKeys.PLAY_SOUND.get()));
                }
            }

            //破条则加stack清空蓄力条
            if(container.getStack() < 3 && Math.ceil(container.getResource(1.0F) * 20) > 10) {
                this.setConsumptionSynchronize(serverPlayerPatch, 1);
                this.setStackSynchronize(serverPlayerPatch, container.getStack() + 1);
            }

            //四蓄的掉棍势时间判断
            int current = dataManager.getDataValue(WukongSkillDataKeys.CHARGED4_TIMER.get());
            if(current > 0){
                dataManager.setDataSync(WukongSkillDataKeys.CHARGED4_TIMER.get(), current - 1, serverPlayer);
            }
            float consumption = Config.CHARGING_SPEED.get().floatValue() / 5;
            if(current == 1 && container.isFull()){
                this.setStackSynchronize(serverPlayerPatch, 3);
                this.setConsumptionSynchronize(serverPlayerPatch, container.getMaxResource() - consumption);
            }
            if(current == 0 && container.getStack() >= 3 && container.getResource() > consumption + 0.1){
                this.setConsumptionSynchronize(serverPlayerPatch, container.getResource() - consumption);
            }

        }

    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldDraw(SkillContainer container) {
        return WukongWeaponCategories.isWeaponValid(container.getExecuter());
    }

    /**
     * 根据棍式和星级画图
     * 取消原本的绘制在 {@link com.p1nero.wukong.mixin.BattleModeGuiMixin}
     */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y) {
        int stack = container.getStack();
        int style = container.getExecuter().getHoldingItemCapability(InteractionHand.MAIN_HAND).getStyle(container.getExecuter()).universalOrdinal() - WukongStyles.SMASH.universalOrdinal();
        float cooldownRatio = !container.isFull() && !container.isActivated() ? container.getResource(1.0F) : 1.0F;
        int progress = ((int) Math.ceil(cooldownRatio * 20));
        EpicFightOptions config = EpicFightMod.CLIENT_CONFIGS;
        Window sr = Minecraft.getInstance().getWindow();
        int width = sr.getGuiScaledWidth();
        int height = sr.getGuiScaledHeight();
        Vec2i pos = config.getWeaponInnatePosition(width, height);
        ResourceLocation progressTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/progress/" + progress + ".png");
        ResourceLocation styleTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/style/" + style + (stack == 4?"_1":"_0") + ".png");
        ResourceLocation stackTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/stack/" + stack + ".png");
        ResourceLocation goldenLightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/gold.png");
        guiGraphics.blit(progressTexture, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        guiGraphics.blit(styleTexture, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        guiGraphics.blit(stackTexture, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);

        if(container.getDataManager().getDataValue(WukongSkillDataKeys.RED_TIMER.get()) > 0){
            int star = Math.min(container.getDataManager().getDataValue(WukongSkillDataKeys.STARS_CONSUMED.get()), 3);
            if(star > 0){
                ResourceLocation lightTexture = new ResourceLocation(WukongMoveset.MOD_ID, "textures/gui/staff_stack/light/" + star + ".png");//及时获取stack，新鲜的，热乎的
                guiGraphics.blit(lightTexture, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);

            }
        }

        if(container.isFull()){
            guiGraphics.blit(goldenLightTexture, pos.x - 12, pos.y - 12, 48, 48, 0.0F, 0.0F, 2, 2, 2, 2);
        }


    }

    @Override
    public WeaponInnateSkill registerPropertiesToAnimation() {
        return null;
    }

    public static class Builder extends Skill.Builder<SmashHeavyAttack> {
        protected StaticAnimationProvider[] animationProviders;
        protected StaticAnimationProvider derive1;
        protected StaticAnimationProvider derive2;
        protected StaticAnimationProvider jumpAttackHeavy;
        StaticAnimationProvider chargingAnimation;
        StaticAnimationProvider pre;

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

        public Builder setJumpAttackHeavy(StaticAnimationProvider jumpAttackHeavy){
            this.jumpAttackHeavy = jumpAttackHeavy;
            return this;
        }
    }

}
