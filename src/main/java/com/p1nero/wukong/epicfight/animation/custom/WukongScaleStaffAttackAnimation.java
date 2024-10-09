package com.p1nero.wukong.epicfight.animation.custom;

import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class WukongScaleStaffAttackAnimation extends BasicAttackAnimation {
    public WukongScaleStaffAttackAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
        super(convertTime, antic, contact, recovery, collider, colliderJoint, path, armature);
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
        if(WukongWeaponCategories.isWeaponValid(entityPatch)){
            CompoundTag tag = entityPatch.getOriginal().getMainHandItem().getOrCreateTag();
            tag.putBoolean("WK_shouldScaleItem", false);
            if(entityPatch instanceof ServerPlayerPatch serverPlayerPatch){
                serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager().setData(WukongSkillDataKeys.DAMAGE_REDUCE.get(), -1.0F);
            }
        }
    }
}
