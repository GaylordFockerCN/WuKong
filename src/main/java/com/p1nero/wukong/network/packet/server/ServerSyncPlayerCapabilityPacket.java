package com.p1nero.wukong.network.packet.server;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
/**
 * 同步数据
 */
public record ServerSyncPlayerCapabilityPacket(CompoundTag old) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(old);
    }
    public static ServerSyncPlayerCapabilityPacket decode(FriendlyByteBuf buf){
        return new ServerSyncPlayerCapabilityPacket(buf.readNbt());
    }
    @Override
    public void execute(Player player) {
        if(player != null){
            player.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent((wkPlayer -> {
                wkPlayer.loadNBTData(old);
            }));
        }
    }
}