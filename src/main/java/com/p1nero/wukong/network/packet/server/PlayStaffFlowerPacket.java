package com.p1nero.wukong.network.packet.server;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 根据客户端的需求来播动画
 */
public record PlayStaffFlowerPacket(boolean isTwoHand) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(isTwoHand);
    }

    public static PlayStaffFlowerPacket decode(FriendlyByteBuf buf){
        return new PlayStaffFlowerPacket(buf.readBoolean());
    }

    @Override
    public void execute(Player player) {
        if(player != null){
            player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent((entityPatch -> {
                if(entityPatch instanceof ServerPlayerPatch playerPatch){
                    playerPatch.playAnimationSynchronized((isTwoHand)
                            ? WukongAnimations.STAFF_SPIN_TWO_HAND_LOOP : WukongAnimations.STAFF_SPIN_ONE_HAND_LOOP, 0);
                    playerPatch.consumeStamina(player.isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
                }
            }));
        }
    }
}
