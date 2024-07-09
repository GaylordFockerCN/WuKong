package com.p1nero.wukong.network.packet.client;

import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 发现EF自带，白写了qwq
 * @param style
 */
public record ChangeStaffStylePacket(int style) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(style);
    }

    public static ChangeStaffStylePacket decode(FriendlyByteBuf buf){
        return new ChangeStaffStylePacket(buf.readInt());
    }

    @Override
    public void execute(Player player) {
        if(player != null){
            player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent((entityPatch -> {
                if(entityPatch instanceof ServerPlayerPatch playerPatch){
                    //TODO 判断是否学过
                    switch (style){
                        case 0:
                            playerPatch.getSkill(WukongSkillSlots.STAFF_STYLE).setSkill(WukongSkills.CHOP_STYLE);
                            player.displayClientMessage(Component.nullToEmpty("当前棍势：劈棍势"), true);
                            break;
                        case 1:
                            playerPatch.getSkill(WukongSkillSlots.STAFF_STYLE).setSkill(WukongSkills.POKE_STYLE);
                            player.displayClientMessage(Component.nullToEmpty("当前棍势：戳棍势"), true);
                            break;
                        case 2:
                            playerPatch.getSkill(WukongSkillSlots.STAFF_STYLE).setSkill(WukongSkills.STAND_STYLE);
                            player.displayClientMessage(Component.nullToEmpty("当前棍势：立棍势"), true);
                            break;
                    }
                }
            }));
        }
    }
}
