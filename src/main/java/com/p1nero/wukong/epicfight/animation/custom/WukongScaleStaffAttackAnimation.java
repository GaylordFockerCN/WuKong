package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class WukongScaleStaffAttackAnimation extends BasicAttackAnimation {
    private float damageReduce;
    public WukongScaleStaffAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature, float damageReduce) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
        this.damageReduce = damageReduce;
    }
    public WukongScaleStaffAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
    }

    /**
     * 取消加棍势
     * 设置减伤
     */
    @Override
    public boolean isBasicAttackAnimation() {
        return false;
    }

    @Override
    public void begin(LivingEntityPatch<?> entityPatch) {
        super.begin(entityPatch);
        entityPatch.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
            wkPlayer.setDamageReduce(damageReduce);
        });
    }

    /**
     * 保险，复位棍子的缩放
     * 复位减伤
     */
    @Override
    public void end(LivingEntityPatch<?> entityPatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        entityPatch.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
            wkPlayer.setDamageReduce(-1.0F);
        });
        if(entityPatch.isLogicalClient() && WukongWeaponCategories.isWeaponValid(entityPatch)){
            CompoundTag tag = entityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
        }
    }
}
