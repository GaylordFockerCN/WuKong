package com.p1nero.wukong.mixin;

import com.p1nero.wukong.epicfight.skill.HeavyAttack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

/**
 * 取消耗星后的攻击造成的棍势增加
 */
@Mixin(value = ServerPlayerPatch.class, remap = false)
public abstract class ServerPlayerPatchMixin extends PlayerPatch<ServerPlayer> {
    @Inject(method = "gatherDamageDealt", at = @At(value = "HEAD"), cancellable = true)
    private void inject(EpicFightDamageSource source, float amount, CallbackInfo ci){
        SkillDataManager manager = this.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
        if(manager.getDataValue(HeavyAttack.CANCEL_NEXT_CONSUMPTION)){
            manager.setDataSync(HeavyAttack.CANCEL_NEXT_CONSUMPTION, false, this.getOriginal());
            ci.cancel();
        }
    }
}
