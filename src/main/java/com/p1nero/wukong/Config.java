package com.p1nero.wukong;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.DoubleValue DERIVE_CHECK_TIME;
    public static final ForgeConfigSpec.DoubleValue CHARGING_SPEED;
    public static final ForgeConfigSpec.DoubleValue STAFF_FLOWER_STAMINA_CONSUME;
    public static final ForgeConfigSpec.DoubleValue DERIVE_STAMINA_CONSUME;
    public static final ForgeConfigSpec.DoubleValue BASIC_ATTACK_INTERVAL_TICKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITIES_CAN_BE_BLOCKED_BY_STAFF_FLOWER;
    public static final ForgeConfigSpec SPEC;

    public static Set<? extends EntityType<?>> entities_can_be_blocked = new HashSet<>();

    static {
        BASIC_ATTACK_INTERVAL_TICKS = createDouble("Custom time interval between basic attack (ordinal interval is too long!)", "basic_attack_time_interval", 16.0);
        DERIVE_CHECK_TIME = createDouble("The time period that can use derive attack after basic attack.", "derive_check_time", 40.0);
        CHARGING_SPEED = createDouble("The consumption increase or decrease per tick", "charging_speed", 0.5);
        STAFF_FLOWER_STAMINA_CONSUME = createDouble("Stamina consumed per use of the Staff Spin", "staff_flower_stamina_consume", 0.5);
        DERIVE_STAMINA_CONSUME = createDouble("Stamina consumed per use of the Special Attack Loop in Thrust or Pillar", "derive_stamina_consume", 2.0);
        ENTITIES_CAN_BE_BLOCKED_BY_STAFF_FLOWER = BUILDER
                .comment("A list of items considered as sword.")
                .defineListAllowEmpty(List.of("entities can be blocked by staff flower"), () -> List.of("minecraft:arrow"), Config::validateEntityName);
        SPEC = BUILDER.build();
    }

    private static ForgeConfigSpec.BooleanValue createBool(String key, boolean defaultValue){
        return BUILDER
                .translation("config."+WukongMoveset.MOD_ID+"."+key)
                .define(key, defaultValue);
    }
    private static ForgeConfigSpec.BooleanValue createBool(String comment, String key, boolean defaultValue){
        return BUILDER
                .comment(comment)
                .translation("config."+WukongMoveset.MOD_ID+"."+key)
                .define(key, defaultValue);
    }

    private static ForgeConfigSpec.DoubleValue createDouble(String comment ,String key, double defaultValue) {
        return BUILDER
                .comment(comment)
                .translation("config."+WukongMoveset.MOD_ID+"."+key)
                .defineInRange(key, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private static boolean validateEntityName(final Object obj){
        return obj instanceof final String itemName && ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("wukong").requires((commandSourceStack) -> commandSourceStack.hasPermission(2))



        );
    }

    private static int setConfig(ForgeConfigSpec.DoubleValue config, double value, CommandSourceStack stack){
        config.set(value);
        stack.sendSuccess(Component.nullToEmpty("Successfully set to : "+value), true);
        return 0;
    }

    private static int setConfig(ForgeConfigSpec.BooleanValue config, boolean value, CommandSourceStack stack){
        config.set(value);
        stack.sendSuccess(Component.nullToEmpty("Successfully set to : "+value), true);
        return 0;
    }

}
