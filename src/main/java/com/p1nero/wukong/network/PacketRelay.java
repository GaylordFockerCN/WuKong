package com.p1nero.wukong.network;

import com.p1nero.wukong.capability.WKCapabilityProvider;
import com.p1nero.wukong.network.packet.client.ClientSyncPlayerCapabilityPacket;
import com.p1nero.wukong.network.packet.server.ServerSyncPlayerCapabilityPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketRelay {
    public PacketRelay() {
    }

    public static <MSG> void sendToPlayer(SimpleChannel handler, MSG message, ServerPlayer player) {
        handler.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToNear(SimpleChannel handler, MSG message, double x, double y, double z, double radius, ResourceKey<Level> dimension) {
        handler.send(PacketDistributor.NEAR.with(TargetPoint.p(x, y, z, radius, dimension)), message);
    }

    public static <MSG> void sendToAll(SimpleChannel handler, MSG message) {
        handler.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToServer(SimpleChannel handler, MSG message) {
        handler.sendToServer(message);
    }

    public static <MSG> void sendToDimension(SimpleChannel handler, MSG message, ResourceKey<Level> dimension) {
        handler.send(PacketDistributor.DIMENSION.with(() -> {
            return dimension;
        }), message);
    }

    public static void syncPlayer(ServerPlayer serverPlayer){
        serverPlayer.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
            CompoundTag oldData = new CompoundTag();
            wkPlayer.saveNBTData(oldData);
            sendToPlayer(PacketHandler.INSTANCE, new ClientSyncPlayerCapabilityPacket(oldData), serverPlayer);
        });
    }
    public static void syncPlayer(LocalPlayer localPlayer){
        localPlayer.getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(wkPlayer -> {
            CompoundTag oldData = new CompoundTag();
            wkPlayer.saveNBTData(oldData);
            sendToServer(PacketHandler.INSTANCE, new ServerSyncPlayerCapabilityPacket(oldData));
        });
    }

}
