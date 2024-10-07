package com.p1nero.wukong.epicfight.skill.custom;

import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongStyles;
import com.p1nero.wukong.epicfight.skill.WukongSkillDataKeys;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;

public class StaffStance extends Skill {

    public final WukongStyles style;//客户端无效，所以得有datakey

    public StaffStance(Builder builder) {
        super(builder);
        this.style = builder.style;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        if(container.getExecuter().getOriginal() instanceof ServerPlayer serverPlayer){
            container.getDataManager().setDataSync(WukongSkillDataKeys.STANCE.get(), style.ordinal(), serverPlayer);
        }
    }

    public static Builder createStaffStyle(){
        return new Builder().setCategory(WukongSkillCategories.STAFF_STYLE).setResource(Resource.NONE);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        container.getDataManager().setData(WukongSkillDataKeys.STANCE.get(), WukongStyles.SMASH.ordinal());
    }

    /**
     * 得根据key返回，不然客户端不同步。。
     * 很奇怪，onInitiate里面setDataSync会出错
     */
    public WukongStyles getStyle(SkillContainer container) {
        if(!container.getDataManager().hasData(WukongSkillDataKeys.STANCE.get())){
            container.getDataManager().registerData(WukongSkillDataKeys.STANCE.get());
            if(!container.getExecuter().isLogicalClient()){
                container.getDataManager().setDataSync(WukongSkillDataKeys.STANCE.get(), style.ordinal(), ((ServerPlayer) container.getExecuter().getOriginal()));
            }
        }
        return WukongStyles.values()[container.getDataManager().getDataValue(WukongSkillDataKeys.STANCE.get())];
    }

    public static class Builder extends Skill.Builder<StaffStance>{
        protected WukongStyles style;

        public Builder setStyle(WukongStyles style){
            this.style = style;
            return this;
        }

        @Override
        public Builder setResource(Resource resource) {
            this.resource = resource;
            return this;
        }

        @Override
        public Builder setCategory(SkillCategory category) {
            this.category = category;
            return this;
        }

        @Override
        public Builder setCreativeTab(CreativeModeTab tab) {
            this.tab = tab;
            return this;
        }
    }

}