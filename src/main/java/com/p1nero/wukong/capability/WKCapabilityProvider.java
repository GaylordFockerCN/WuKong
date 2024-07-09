package com.p1nero.wukong.capability;

import com.p1nero.wukong.WukongMoveset;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

    public static Capability<WKPlayer> SS_PLAYER = CapabilityManager.get(new CapabilityToken<>() {});

    private WKPlayer WKPlayer = null;
    
    private final LazyOptional<WKPlayer> optional = LazyOptional.of(this::createSSPlayer);

    private WKPlayer createSSPlayer() {
        if(this.WKPlayer == null){
            this.WKPlayer = new WKPlayer();
        }

        return this.WKPlayer;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
        if(capability == SS_PLAYER){
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        createSSPlayer().saveNBTData(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        createSSPlayer().loadNBTData(tag);
    }

    @Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID)
    public static class Registration {
        @SubscribeEvent
        public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
               if(!event.getObject().getCapability(WKCapabilityProvider.SS_PLAYER).isPresent()){
                   event.addCapability(new ResourceLocation(WukongMoveset.MOD_ID, "ss_player"), new WKCapabilityProvider());
               }
            }
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            if(event.isWasDeath()) {
                event.getOriginal().getCapability(WKCapabilityProvider.SS_PLAYER).ifPresent(oldStore -> {
                    event.getOriginal().getCapability(WKCapabilityProvider.SS_PLAYER).ifPresent(newStore -> {
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
