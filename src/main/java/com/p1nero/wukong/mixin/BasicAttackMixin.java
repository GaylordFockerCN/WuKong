package com.p1nero.wukong.mixin;

import com.p1nero.wukong.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.*;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;

import static yesman.epicfight.skill.BasicAttack.setComboCounterWithEvent;

@Mixin(value = BasicAttack.class, remap = false)
public class BasicAttackMixin {

    /**
     * 修改普攻有效间隔时间
     */
    @Inject(method = "updateContainer", at = @At("HEAD"), cancellable = true)
    private void modifyExpiredTicks(SkillContainer container, CallbackInfo ci){
        if (!container.getExecuter().isLogicalClient() && container.getExecuter().getTickSinceLastAction() > Config.BASIC_ATTACK_INTERVAL_TICKS.get() && (Integer)container.getDataManager().getDataValue((SkillDataKey) SkillDataKeys.COMBO_COUNTER.get()) > 0) {
            setComboCounterWithEvent(ComboCounterHandleEvent.Causal.TIME_EXPIRED, (ServerPlayerPatch)container.getExecuter(), container, null, 0);
        }
        ci.cancel();
    }
}
