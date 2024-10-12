package com.p1nero.wukong.capability;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.network.PacketRelay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WKCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<WKPlayer> WK_PLAYER = CapabilityManager.get(new CapabilityToken<>() {});

    private WKPlayer wkPlayer = null;
    
    private final LazyOptional<WKPlayer> optional = LazyOptional.of(this::createWKPlayer);

    private WKPlayer createWKPlayer() {
        if(this.wkPlayer == null){
            this.wkPlayer = new WKPlayer();
        }

        return this.wkPlayer;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == WK_PLAYER){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createWKPlayer().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createWKPlayer().loadNBTData(tag);
    }

    @Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID)
    public static class Registration {
        @SubscribeEvent
        public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
               if(!event.getObject().getCapability(WKCapabilityProvider.WK_PLAYER).isPresent()){
                   event.addCapability(new ResourceLocation(WukongMoveset.MOD_ID, "wk_player"), new WKCapabilityProvider());
               }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            event.getOriginal().reviveCaps();
            if(event.isWasDeath()) {
                event.getOriginal().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(oldStore -> {
                    event.getEntity().getCapability(WKCapabilityProvider.WK_PLAYER).ifPresent(newStore -> {
                        newStore.copyFrom(oldStore);
                    });
                });
            }
        }

        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            event.register(WKPlayer.class);
        }

    }


}
