package com.p1nero.wukong.network;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.network.packet.BasePacket;
import com.p1nero.wukong.network.packet.client.AddEntityAfterImageParticle;
import com.p1nero.wukong.network.packet.server.PlayStaffFlowerPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WukongMoveset.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    private static int index;

    public static synchronized void register() {

        //Client
        register(PlayStaffFlowerPacket.class, PlayStaffFlowerPacket::decode);

        //Server
        register(AddEntityAfterImageParticle.class, AddEntityAfterImageParticle::decode);

    }

    private static <MSG extends BasePacket> void register(final Class<MSG> packet, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.messageBuilder(packet, index++).encoder(BasePacket::encode).decoder(decoder).consumer(BasePacket::handle).add();
    }
}
