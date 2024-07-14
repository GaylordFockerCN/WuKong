package com.p1nero.wukong;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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
    public static final ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER;
//    public static final ForgeConfigSpec.DoubleValue RAIN_CUTTER_COOLDOWN;
//    public static final ForgeConfigSpec.DoubleValue YAKSHAS_MASK_COOLDOWN;
//    public static final ForgeConfigSpec.DoubleValue STELLAR_RESTORATION_COOLDOWN;
//    public static final ForgeConfigSpec.BooleanValue FORCE_FLY_ANIM;
//    public static final ForgeConfigSpec.BooleanValue ENABLE_INERTIA;
//    public static final ForgeConfigSpec.DoubleValue INERTIA_TICK_BEFORE;
//    public static final ForgeConfigSpec.DoubleValue FLY_SPEED_SCALE;
//    public static final ForgeConfigSpec.DoubleValue STAMINA_CONSUME_PER_TICK;
//    public static final ForgeConfigSpec.DoubleValue MAX_ANTICIPATION_TICK;
//    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_FLY;
//    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEMS_CAN_NOT_FLY;
//
    static final ForgeConfigSpec SPEC;

    static {

//        BUILDER.push("Skill Cooldown");
        DAMAGE_MULTIPLIER = createDouble("The damage multiplier of all Wukong Skill Attack", "damage_multiplier", 1.0);
//        RAIN_CUTTER_COOLDOWN = createDouble("the cooldown ticks of Rain Cutter skill", "rain_cutter_cooldown", 816);
//        YAKSHAS_MASK_COOLDOWN = createDouble("the cooldown ticks of Yaksha's Mask skill", "yaksha_mask_cooldown", 749);
//        STELLAR_RESTORATION_COOLDOWN = createDouble("the cooldown ticks of Stellar Restoration skill", "stellar_restoration_cooldown", 312);
//        BUILDER.pop();
//
//        BUILDER.push("Sword Soaring");
//        FORCE_FLY_ANIM = createBool("force_fly_anim", false);
//        ENABLE_INERTIA = createBool("enable_inertia", true);
//        INERTIA_TICK_BEFORE = createDouble("the inertia end.(delay time) only work when enable_inertia is true. Shouldn't larger than 100!!!","inertia_tick_before", 10);
//        FLY_SPEED_SCALE = createDouble("the ratio of flying speed to view vector","fly_speed_scale", 0.6);
//        STAMINA_CONSUME_PER_TICK = createDouble("the stamina consumed per end when flying" ,"stamina_consume_per_tick", 0.05);
//        MAX_ANTICIPATION_TICK = createDouble("ticks of end taking off","max_anticipation_tick", 10);
//        ITEMS_CAN_FLY = BUILDER
//                .comment("A list of items considered as sword.")
//                .defineListAllowEmpty(List.of("items can fly"), () -> List.of("minecraft:iron_ingot"), Config::validateItemName);
//        ITEMS_CAN_NOT_FLY = BUILDER
//                .comment("A list of items not considered as sword.")
//                .defineListAllowEmpty(List.of("items can't fly"), () -> List.of("minecraft:iron_ingot"), Config::validateItemName);
//        BUILDER.pop();
//
        SPEC = BUILDER.build();
    }

    public static Set<Item> swordItems = new HashSet<>();
    public static Set<Item> notSwordItems = new HashSet<>();

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

    private static boolean validateItemName(final Object obj){
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
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
