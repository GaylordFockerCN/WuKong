package com.p1nero.wukong.network.packet.client;
import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
/**
 * 同步数据
 */
public record ClientSyncPlayerCapabilityPacket(CompoundTag old) implements BasePacket {
    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(old);
    }
    public static ClientSyncPlayerCapabilityPacket decode(FriendlyByteBuf buf){
        return new ClientSyncPlayerCapabilityPacket(buf.readNbt());
    }
    @Override
    public void execute(Player player) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
            Minecraft.getInstance().player.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent((wkPlayer -> {
                wkPlayer.loadNBTData(old);
            }));
        }
    }
}