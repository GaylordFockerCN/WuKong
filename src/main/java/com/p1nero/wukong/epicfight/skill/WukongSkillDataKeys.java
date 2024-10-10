package com.p1nero.wukong.epicfight.skill;

import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.skill.custom.StaffPassive;
import com.p1nero.wukong.epicfight.skill.custom.WukongDodgeSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillDataKey;

public class WukongSkillDataKeys {
    public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(new ResourceLocation(EpicFightMod.MODID, "skill_data_keys"), WukongMoveset.MOD_ID);
    //棍式
    public static final RegistryObject<SkillDataKey<Integer>> STANCE = DATA_KEYS.register("stance", () ->
            SkillDataKey.createIntKey(0, false, SmashHeavyAttack.class));
    //重击
    public static final RegistryObject<SkillDataKey<Boolean>> KEY_PRESSING = DATA_KEYS.register("key_pressing", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//技能键是否按下
    public static final RegistryObject<SkillDataKey<Integer>> CHARGED4_TIMER = DATA_KEYS.register("charged4_timer", () ->
            SkillDataKey.createIntKey(0, false, SmashHeavyAttack.class));//四段棍势持续时间
    public static final RegistryObject<SkillDataKey<Integer>> RED_TIMER = DATA_KEYS.register("red_timer", () ->
            SkillDataKey.createIntKey(0, false, SmashHeavyAttack.class));//亮灯时间
    public static final RegistryObject<SkillDataKey<Integer>> LAST_STACK = DATA_KEYS.register("last_stack", () ->
            SkillDataKey.createIntKey(0, false, SmashHeavyAttack.class));//上一次的层数，用于判断是否加层
    public static final RegistryObject<SkillDataKey<Integer>> STARS_CONSUMED = DATA_KEYS.register("stars_consumed", () ->
            SkillDataKey.createIntKey(0, false, SmashHeavyAttack.class));//本次攻击是否消耗星（是否强化）
    public static final RegistryObject<SkillDataKey<Boolean>> IS_IN_SPECIAL_ATTACK = DATA_KEYS.register("is_in_special_attack", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//是否正在切手技（用来判断无敌时间）
    public static final RegistryObject<SkillDataKey<Boolean>> IS_SPECIAL_SUCCESS = DATA_KEYS.register("is_special_success", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//是否正在切手技（用来判断无敌时间）
    public static final RegistryObject<SkillDataKey<Boolean>> IS_CHARGING = DATA_KEYS.register("is_charging", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//是否正在蓄力
    public static final RegistryObject<SkillDataKey<Integer>> DERIVE_TIMER = DATA_KEYS.register("derive_timer", () ->
            SkillDataKey.createIntKey(0, false, SmashHeavyAttack.class));//衍生合法时间计时器
    public static final RegistryObject<SkillDataKey<Boolean>> CAN_FIRST_DERIVE = DATA_KEYS.register("can_first_derive", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//是否可以使用第一段衍生
    public static final RegistryObject<SkillDataKey<Boolean>> CAN_SECOND_DERIVE = DATA_KEYS.register("can_second_derive", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//是否可以使用第二段衍生
    public static final RegistryObject<SkillDataKey<Boolean>> CAN_JUMP_HEAVY = DATA_KEYS.register("can_jump_heavy", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//是否可以使用跳跃重击
    public static final RegistryObject<SkillDataKey<Boolean>> PLAY_SOUND = DATA_KEYS.register("play_sound", () ->
            SkillDataKey.createBooleanKey(true, false, SmashHeavyAttack.class));//是否播放棍势消耗音效
    public static final RegistryObject<SkillDataKey<Boolean>> PROTECT_NEXT_FALL = DATA_KEYS.register("protect_next_fall", () ->
            SkillDataKey.createBooleanKey(false, false, SmashHeavyAttack.class));//防止坠机
    public static final RegistryObject<SkillDataKey<Float>> DAMAGE_REDUCE = DATA_KEYS.register("damage_reduce", () ->
            SkillDataKey.createFloatKey(-1.0F, false, SmashHeavyAttack.class));//防止坠机
    //闪避
    public static final RegistryObject<SkillDataKey<Integer>> COUNT = DATA_KEYS.register("count", () ->
            SkillDataKey.createIntKey(0, false, WukongDodgeSkill.class));//闪避计数器
    public static final RegistryObject<SkillDataKey<Integer>> DIRECTION = DATA_KEYS.register("direction", () ->
            SkillDataKey.createIntKey(0, false, WukongDodgeSkill.class));//方向，用于播放完美闪避
    public static final RegistryObject<SkillDataKey<Integer>> RESET_TIMER = DATA_KEYS.register("reset_timer", () ->
            SkillDataKey.createIntKey(0, false, WukongDodgeSkill.class));//回归第一段的时间
    public static final RegistryObject<SkillDataKey<Boolean>> DODGE_PLAYED = DATA_KEYS.register("dodge_played", () ->
            SkillDataKey.createBooleanKey(false, false, WukongDodgeSkill.class));//是否播过完美闪避，防止重复播放
    //棍花
    public static final RegistryObject<SkillDataKey<Boolean>> PLAYING_STAFF_SPIN = DATA_KEYS.register("playing_staff_spin", () ->
            SkillDataKey.createBooleanKey(false, false, StaffPassive.class));

}
