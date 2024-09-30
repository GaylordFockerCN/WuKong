package com.p1nero.wukong.network.packet.client;

import com.p1nero.wukong.network.packet.BasePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.particle.EpicFightParticles;

/**
 * 手动加残影，不知道为何serverLevel.sendParticles(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), player.getX(), player.getY(), player.getZ(), 0, Double.longBitsToDouble(player.getId()), 0.0, 0.0, 1.0);无效
 */
public record AddEntityAfterImageParticle(int id) implements BasePacket {

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
    }

    public static AddEntityAfterImageParticle decode(FriendlyByteBuf buf){
        return new AddEntityAfterImageParticle(buf.readInt());
    }

    @Override
    public void execute(Player player) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().level != null){
           Entity entity = Minecraft.getInstance().level.getEntity(id);
           if(entity != null){
               Minecraft.getInstance().level.addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0.0, 0.0);
           }
        }
    }
}
