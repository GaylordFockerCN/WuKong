package com.p1nero.wukong.mixin;

import com.p1nero.wukong.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;

import static yesman.epicfight.skill.BasicAttack.setComboCounterWithEvent;

@Mixin(value = BasicAttack.class, remap = false)
public class BasicAttackMixin {

    @Shadow @Final private static SkillDataManager.SkillDataKey<Integer> COMBO_COUNTER;

    /**
     * 修改普攻有效间隔时间
     */
    @Inject(method = "updateContainer", at = @At("HEAD"))
    private void modifyExpiredTicks(SkillContainer container, CallbackInfo ci){
        if (!container.getExecuter().isLogicalClient() && container.getExecuter().getTickSinceLastAction() > Config.BASIC_ATTACK_INTERVAL_TICKS.get().intValue() && container.getDataManager().getDataValue(COMBO_COUNTER) > 0) {
            setComboCounterWithEvent(ComboCounterHandleEvent.Causal.TIME_EXPIRED_RESET, (ServerPlayerPatch)container.getExecuter(), container, null, 0);
        }
    }
}
