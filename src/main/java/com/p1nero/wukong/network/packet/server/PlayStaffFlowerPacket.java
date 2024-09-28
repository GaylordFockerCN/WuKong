package com.p1nero.wukong.network.packet.server;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.custom.StaffSpin;
import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 播动画。不知道怎么在服务端同步hh
 */
public record PlayStaffFlowerPacket(boolean isOneHand) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isOneHand);
    }

    public static PlayStaffFlowerPacket decode(FriendlyByteBuf buf){
        return new PlayStaffFlowerPacket(buf.readBoolean());
    }

    @Override
    public void execute(Player player) {
        if(player != null){
            player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent((entityPatch -> {
                if(entityPatch instanceof ServerPlayerPatch playerPatch){
                    playerPatch.playAnimationSynchronized((isOneHand && playerPatch.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(StaffSpin.IS_ONE_HAND))
                            ? WukongAnimations.STAFF_FLOWER_ONE_HAND_LOOP : WukongAnimations.STAFF_FLOWER_TWO_HAND_LOOP, 0);
                    playerPatch.consumeStamina(player.isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
                }
            }));
        }
    }
}
