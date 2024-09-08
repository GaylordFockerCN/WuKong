package com.p1nero.wukong.epicfight.skill.custom;

import net.minecraft.world.entity.player.Player;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import java.util.UUID;

public class WukongDodge extends DodgeSkill {
    private static final UUID EVENT_UUID = UUID.fromString("d2d057cc-f30f-11ed-a05b-0242ac114515");
    public WukongDodge(Builder builder) {
        super(builder);
    }

    @Override
    public void onInitiate(SkillContainer container) {
        super.onInitiate(container);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event -> {
            Player player = event.getPlayerPatch().getOriginal();
//            event.getPlayerPatch().playSound();TODO 播放音效 播动画
            event.getPlayerPatch().getSkill(SkillSlots.WEAPON_INNATE).getSkill().setConsumptionSynchronize(((ServerPlayerPatch) container.getExecuter()), container.getResource() + 5);//获得棍势
            player.level.addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), player.getX(), player.getY(), player.getZ(), Double.longBitsToDouble(player.getId()), 0.0, 0.0);//留下残影
        }));
    }
}
