package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongStyles;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;

public class StaffStyle extends Skill {

    public final WukongStyles style;//客户端无效，所以得有datakey
    private static final SkillDataManager.SkillDataKey<Integer> STYLE = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);

    public StaffStyle(Builder builder) {
        super(builder);
        this.style = builder.style;
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(STYLE);
        container.getDataManager().setData(STYLE, style.ordinal());
    }

    public static Builder createStaffStyle(){
        return new Builder().setCategory(WukongSkillCategories.STAFF_STYLE).setResource(Resource.NONE);
    }

    @Override
    public void onRemoved(SkillContainer container) {
        container.getDataManager().setData(STYLE, WukongStyles.SMASH.ordinal());
    }

    /**
     * 得根据key返回，不然客户端不同步。。
     * 很奇怪，onInitiate里面setDataSync会出错
     */
    public WukongStyles getStyle(SkillContainer container) {
        if(!container.getDataManager().hasData(STYLE)){
            container.getDataManager().registerData(STYLE);
            if(!container.getExecuter().isLogicalClient()){
                container.getDataManager().setDataSync(STYLE, style.ordinal(), ((ServerPlayer) container.getExecuter().getOriginal()));
            }
        }
        return WukongStyles.values()[container.getDataManager().getDataValue(STYLE)];
    }

    public static class Builder extends Skill.Builder<StaffStyle>{
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