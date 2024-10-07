package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.StaffPassive;
import com.p1nero.wukong.epicfight.skill.custom.StaffStance;
import com.p1nero.wukong.epicfight.skill.custom.WukongDodgeSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillDataKey;
import java.util.function.Supplier;

public class WukongSkillDataKeys {
    private static final Supplier<RegistryBuilder<SkillDataKey<?>>> BUILDER = () -> new RegistryBuilder<SkillDataKey<?>>().addCallback(SkillDataKey.getRegistryCallback());
    public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(new ResourceLocation(EpicFightMod.MODID, "skill_data_keys"), WukongMoveset.MOD_ID);
    public static final Supplier<IForgeRegistry<SkillDataKey<?>>> REGISTRY;
    //棍式
    public static final RegistryObject<SkillDataKey<Integer>> STANCE;
    //重击
    public static final RegistryObject<SkillDataKey<Boolean>> KEY_PRESSING;//技能键是否按下
    public static final RegistryObject<SkillDataKey<Integer>> CHARGED4_TIMER;//四段棍势持续时间
    public static final RegistryObject<SkillDataKey<Integer>> RED_TIMER;//亮灯时间
    public static final RegistryObject<SkillDataKey<Integer>> LAST_STACK;//上一次的层数，用于判断是否加层
    public static final RegistryObject<SkillDataKey<Integer>> STARS_CONSUMED;//本次攻击是否消耗星（是否强化）
    public static final RegistryObject<SkillDataKey<Boolean>> IS_IN_SPECIAL_ATTACK;//是否正在切手技（用来判断无敌时间）
    public static final RegistryObject<SkillDataKey<Boolean>> IS_CHARGING;//是否正在蓄力
    public static final RegistryObject<SkillDataKey<Integer>> DERIVE_TIMER;//衍生合法时间计时器
    public static final RegistryObject<SkillDataKey<Boolean>> CAN_FIRST_DERIVE;//是否可以使用第一段衍生
    public static final RegistryObject<SkillDataKey<Boolean>> CAN_SECOND_DERIVE;//是否可以使用第二段衍生
    public static final RegistryObject<SkillDataKey<Boolean>> CAN_JUMP_HEAVY;//是否可以使用跳跃重击
    public static final RegistryObject<SkillDataKey<Boolean>> PLAY_SOUND;//是否播放棍势消耗音效
    public static final RegistryObject<SkillDataKey<Boolean>> PROTECT_NEXT_FALL;//防止坠机
    //闪避
    public static final RegistryObject<SkillDataKey<Integer>> COUNT;//闪避计数器
    public static final RegistryObject<SkillDataKey<Integer>> DIRECTION;//方向，用于播放完美闪避
    public static final RegistryObject<SkillDataKey<Integer>> RESET_TIMER;//回归第一段的时间
    public static final RegistryObject<SkillDataKey<Boolean>> SOUND_PLAYED;//是否播过音效，防止重复播放
    //棍花
    public static final RegistryObject<SkillDataKey<Boolean>> PLAYING_STAFF_SPIN;

    static {
        REGISTRY = DATA_KEYS.makeRegistry(BUILDER);

        STANCE = DATA_KEYS.register("stance", () ->
                SkillDataKey.createIntKey(0, true, SmashHeavyAttack.class));

        KEY_PRESSING = DATA_KEYS.register("key_pressing", () ->
                SkillDataKey.createBooleanKey(false, true, SmashHeavyAttack.class));
        CHARGED4_TIMER = DATA_KEYS.register("charged4_timer", () ->
                SkillDataKey.createIntKey(0, true, SmashHeavyAttack.class));
        RED_TIMER = DATA_KEYS.register("red_timer", () ->
                SkillDataKey.createIntKey(0, true, SmashHeavyAttack.class));
        LAST_STACK = DATA_KEYS.register("last_stack", () ->
                SkillDataKey.createIntKey(0, true, SmashHeavyAttack.class));
        STARS_CONSUMED = DATA_KEYS.register("stars_consumed", () ->
                SkillDataKey.createIntKey(0, true, SmashHeavyAttack.class));
        IS_IN_SPECIAL_ATTACK = DATA_KEYS.register("is_in_special_attack", () ->
                SkillDataKey.createBooleanKey(false, true, SmashHeavyAttack.class));
        IS_CHARGING = DATA_KEYS.register("is_charging", () ->
                SkillDataKey.createBooleanKey(false, true, SmashHeavyAttack.class));
        DERIVE_TIMER = DATA_KEYS.register("derive_timer", () ->
                SkillDataKey.createIntKey(0, true, SmashHeavyAttack.class));
        CAN_FIRST_DERIVE = DATA_KEYS.register("can_first_derive", () ->
                SkillDataKey.createBooleanKey(false, true, SmashHeavyAttack.class));
        CAN_SECOND_DERIVE = DATA_KEYS.register("can_second_derive", () ->
                SkillDataKey.createBooleanKey(false, true, SmashHeavyAttack.class));
        CAN_JUMP_HEAVY = DATA_KEYS.register("can_jump_heavy", () ->
                SkillDataKey.createBooleanKey(false, true, SmashHeavyAttack.class));
        PLAY_SOUND = DATA_KEYS.register("play_sound", () ->
                SkillDataKey.createBooleanKey(true, true, SmashHeavyAttack.class));
        PROTECT_NEXT_FALL = DATA_KEYS.register("protect_next_fall", () ->
                SkillDataKey.createBooleanKey(false, true, SmashHeavyAttack.class));

        COUNT = DATA_KEYS.register("count", () ->
                SkillDataKey.createIntKey(0, true, WukongDodgeSkill.class));
        DIRECTION = DATA_KEYS.register("direction", () ->
                SkillDataKey.createIntKey(0, true, WukongDodgeSkill.class));
        RESET_TIMER = DATA_KEYS.register("reset_timer", () ->
                SkillDataKey.createIntKey(0, true, WukongDodgeSkill.class));
        SOUND_PLAYED = DATA_KEYS.register("sound_played", () ->
                SkillDataKey.createBooleanKey(false, true, WukongDodgeSkill.class));

        PLAYING_STAFF_SPIN = DATA_KEYS.register("playing_staff_spin", () ->
                SkillDataKey.createBooleanKey(false, true, StaffPassive.class));

    }
}
