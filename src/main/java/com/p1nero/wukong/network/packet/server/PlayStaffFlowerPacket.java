package com.p1nero.wukong.network.packet.server;

import com.p1nero.wukong.Config;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * 播动画。不知道怎么在服务端同步hh
 */
public record PlayStaffFlowerPacket() implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {

    }

    public static PlayStaffFlowerPacket decode(FriendlyByteBuf buf){
        return new PlayStaffFlowerPacket();
    }

    @Override
    public void execute(Player player) {
        if(player != null){
            player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent((entityPatch -> {
                if(entityPatch instanceof ServerPlayerPatch playerPatch){
                    boolean isOneHand = player.getDeltaMovement().length() < 0.1;
                    playerPatch.playAnimationSynchronized(isOneHand?WukongAnimations.STAFF_FLOWER_ONE_HAND:WukongAnimations.STAFF_FLOWER_TWO_HAND, 0);
                    playerPatch.consumeStamina(player.isCreative() ? 0 : Config.STAFF_FLOWER_STAMINA_CONSUME.get().floatValue());
                }
            }));
        }
    }
}
