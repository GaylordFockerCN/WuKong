package com.p1nero.wukong.epicfight.animation.custom;

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
    public WukongScaleStaffAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongScaleStaffAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
    }

    public WukongScaleStaffAttackAnimation(float convertTime, float antic, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, hand, collider, colliderJoint, path, armature);
    }

    public WukongScaleStaffAttackAnimation(float convertTime, String path, Armature armature, Phase... phases) {
        super(convertTime, path, armature, phases);
    }

    /**
     * 取消加棍势
     */
    @Override
    public boolean isBasicAttackAnimation() {
        return false;
    }

    /**
     * 保险，复位棍子的缩放
     */
    @Override
    public void end(LivingEntityPatch<?> entityPatch, DynamicAnimation nextAnimation, boolean isEnd) {
        super.end(entityPatch, nextAnimation, isEnd);
        if(entityPatch.isLogicalClient()){
            CompoundTag tag = entityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
        }
    }
}
