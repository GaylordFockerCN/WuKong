package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.client.keymapping.WukongKeyMappings;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import net.minecraft.client.player.LocalPlayer;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

/**
 * 防守技能棍花
 */
public class StaffFlower extends Skill {

    private static final SkillDataManager.SkillDataKey<Boolean> PLAYING_STAFF_FLOWER = SkillDataManager.SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac191981");

    public StaffFlower(Builder<? extends Skill> builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getDataManager().registerData(PLAYING_STAFF_FLOWER);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID, (event -> {
        //TODO 抄一下防守
        }));
    }

    @Override
    public void onRemoved(SkillContainer container) {
        super.onRemoved(container);
        container.getExecuter().getEventListener().removeListener(PlayerEventListener.EventType.HURT_EVENT_PRE, EVENT_UUID);
    }

    @Override
    public void updateContainer(SkillContainer container) {
        super.updateContainer(container);
        if(!container.getExecuter().isLogicalClient()){
            return;
        }
        if(WukongKeyMappings.STAFF_FLOWER.isDown() && container.getExecuter().hasStamina(0.1F)){
            if(container.getExecuter().getEntityState().inaction()){
                container.getExecuter().playAnimationSynchronized(WukongAnimations.STAFF_FLOWER, 0);
                container.getExecuter().setStamina(container.getExecuter().getStamina()-0.1F);
            }
            container.getDataManager().setDataSync(PLAYING_STAFF_FLOWER, true, ((LocalPlayer) container.getExecuter().getOriginal()));
        } else {
            container.getDataManager().setDataSync(PLAYING_STAFF_FLOWER, false, ((LocalPlayer) container.getExecuter().getOriginal()));
        }
    }
}
