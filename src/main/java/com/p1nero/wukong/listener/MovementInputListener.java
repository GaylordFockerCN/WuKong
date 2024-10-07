package com.p1nero.wukong.listener;


import com.p1nero.wukong.WukongMoveset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.client.events.engine.ControllEngine;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, value = {Dist.CLIENT})
public class MovementInputListener {
    public static boolean shouldStop;
    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event){
        if(shouldStop){
            Input input = event.getInput();
            input.forwardImpulse = 0.0F;
            input.leftImpulse = 0.0F;
            input.down = false;
            input.up = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            input.shiftKeyDown = false;
            LocalPlayer clientPlayer = Minecraft.getInstance().player;
            if(clientPlayer != null) {
                clientPlayer.setSprinting(false);
                clientPlayer.sprintTriggerTime = -1;
                Minecraft mc = Minecraft.getInstance();
                ControllEngine.setKeyBind(mc.options.keySprint, false);
            }
        }
    }

}
