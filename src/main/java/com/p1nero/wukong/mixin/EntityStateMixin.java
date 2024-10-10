package com.p1nero.wukong.mixin;

import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
/**
 * 棍子能对倒地的敌人继续造成伤害
 */
@Mixin(value = EntityState.class, remap = false)
public abstract class EntityStateMixin {
    @Shadow public abstract boolean knockDown();
    @Inject(method = "attackResult", at = @At("HEAD"), cancellable = true)
    private void inject(DamageSource damagesource, CallbackInfoReturnable<AttackResult.ResultType> cir){
        if(damagesource instanceof EpicFightDamageSource epicFightDamageSource){
            epicFightDamageSource.getHurtItem().getCapability(EpicFightCapabilities.CAPABILITY_ITEM).ifPresent(capabilityItem -> {
                if(capabilityItem.getWeaponCategory().equals(WukongWeaponCategories.WK_STAFF) && this.knockDown()){
                    cir.setReturnValue(AttackResult.ResultType.SUCCESS);
                }
            });
        }
    }
}