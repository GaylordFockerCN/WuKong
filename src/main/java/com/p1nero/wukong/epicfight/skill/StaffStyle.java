package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongStyles;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;

public class StaffStyle extends Skill {

    protected final WukongStyles style;

    public StaffStyle(Builder builder) {
        super(builder);
        this.style = builder.style;
    }

    public static Builder createStaffStyle(){
        return new Builder().setCategory(WukongSkillCategories.STAFF_STYLE).setResource(Resource.NONE);
    }

    public WukongStyles getStyle() {
        return style;
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
    }

}