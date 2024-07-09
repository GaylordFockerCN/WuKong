package com.p1nero.wukong.client.keymapping;

import com.p1nero.wukong.network.PacketHandler;
import com.p1nero.wukong.network.PacketRelay;
import com.p1nero.wukong.network.packet.client.ChangeStaffStylePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongKeyMappings {
    public static final MyKeyMapping CHOP_STYLE = new MyKeyMapping("key.wukong.chop_style", GLFW.GLFW_KEY_Z, "key.wukong.category");
    public static final MyKeyMapping STAND_STYLE = new MyKeyMapping("key.wukong.stand_style", GLFW.GLFW_KEY_X, "key.wukong.category");
    public static final MyKeyMapping POKE_STYLE = new MyKeyMapping("key.wukong.poke_style", GLFW.GLFW_KEY_C, "key.wukong.category");
    public static final MyKeyMapping STAFF_FLOWER = new MyKeyMapping("key.wukong.staff_flower", GLFW.GLFW_KEY_V, "key.wukong.category");

    @SubscribeEvent
    public static void registerKeys(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(CHOP_STYLE);
        ClientRegistry.registerKeyBinding(STAND_STYLE);
        ClientRegistry.registerKeyBinding(POKE_STYLE);
        ClientRegistry.registerKeyBinding(STAFF_FLOWER);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(CHOP_STYLE.isDown()){
            PacketRelay.sendToServer(PacketHandler.INSTANCE, new ChangeStaffStylePacket(0));
        }
        if(POKE_STYLE.isDown()){
            PacketRelay.sendToServer(PacketHandler.INSTANCE, new ChangeStaffStylePacket(1));
        }
        if(CHOP_STYLE.isDown()){
            PacketRelay.sendToServer(PacketHandler.INSTANCE, new ChangeStaffStylePacket(2));
        }

    }

}
