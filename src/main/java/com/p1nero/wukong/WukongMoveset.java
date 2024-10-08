package com.p1nero.wukong;

import com.mojang.logging.LogUtils;
import com.p1nero.wukong.client.WuKongSounds;
import com.p1nero.wukong.client.particle.WuKongParticles;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.WukongSkillSlots;
import com.p1nero.wukong.epicfight.animation.WukongAnimations;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import com.p1nero.wukong.epicfight.skill.custom.SmashHeavyAttack;
import com.p1nero.wukong.epicfight.weapon.WukongWeaponCategories;
import com.p1nero.wukong.item.WukongItems;
import com.p1nero.wukong.network.PacketHandler;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

@Mod("wukong")
public class WukongMoveset{
    public static final String MOD_ID = "wukong";
    public static final String ITEM_HAS_EFFECT_TIMER_KEY = "wukong_has_effect_timer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WukongMoveset(){
        SkillCategory.ENUM_MANAGER.loadPreemptive(WukongSkillCategories.class);
        SkillSlot.ENUM_MANAGER.loadPreemptive(WukongSkillSlots.class);
        WeaponCategory.ENUM_MANAGER.loadPreemptive(WukongWeaponCategories.class);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        WukongItems.ITEMS.register(bus);
        WuKongParticles.PARTICLES.register(bus);
        WuKongSounds.SOUND_EVENTS.register(bus);
        bus.addListener(SmashHeavyAttack::register);
        PacketHandler.register();
        WukongSkills.registerSkills();

        IEventBus fg_bus = MinecraftForge.EVENT_BUS;
        fg_bus.addListener(WukongSkills::BuildSkills);
        fg_bus.addListener(WukongAnimations::onPlayerTick);
        fg_bus.addListener(WukongMoveset::onPlayerLoggedIn);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /**
     * 给予指南
     */
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event){
        if(!Config.GET_GUILD_BOOK.get()){
            return;
        }
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        ListTag bookPages = new ListTag();

        bookPages.add(StringTag.valueOf("如果你是黑猴高手请跳过此傻瓜式教程，记得改技能键键位，玩得愉快，感谢下载！否则请认真查看此教程！由于作者偷懒，于是采用最朴素的方式简述玩法（且此书硬编码，没有翻译）。此书仅出现一次，如有需要可在模组配置文件里再次启用。"));
        bookPages.add(StringTag.valueOf("§4首先请按下ESC，点击选项，再点击控制，再点击按键绑定，向下拉，找到史诗战斗，将“武器进阶技能”键（以下简称技能键）更改为与攻击键不同的按键，以免无法蓄力！§r"));
        bookPages.add(StringTag.valueOf("棍花：原地按V（默认）可使用单手棍花，同时按W可变为双手棍花。棍花可抵挡射击伤害，可在配置文件添加可被格挡的物品。成功格挡可增加棍势。"));
        bookPages.add(StringTag.valueOf("跳跃攻击：跳跃时按下攻击键可进行跳跃攻击，即原版跳劈。攻击击中实体时将往回跳，跳跃时按技能键可使用跳跃重击，有棍势则消耗所有棍势并造成更大伤害。"));
        bookPages.add(StringTag.valueOf("完美闪避：无需学习技能书，使用悟空棍子时将自动把其他闪避替换为悟空专属闪避，可往四个方向进行三连翻滚，若触发完美闪避则可增加棍势。若在蓄力过程中触发完美闪避则可保留棍势。"));
        bookPages.add(StringTag.valueOf("劈棍式：棍子武器默认为劈棍式，长按技能键以蓄力（可在配置项调整蓄力速度），蓄力最多蓄到三层棍势。造成伤害也可增长棍势，有棍势时按技能键释放重击。第四层需靠造成伤害或完美闪避积攒。第四层棍势15s后将逐渐减少。"));
        bookPages.add(StringTag.valueOf("劈棍式：轻攻击后短时间内（可在配置项调整判定时长）若有棍势则可按技能键消耗1层棍势使用破棍式，若造成伤害则再次按下技能键可使用斩棍式，若成功识破敌人出招则轻攻击可从第三段继续。"));
        bookPages.add(StringTag.valueOf("立棍式和戳棍式和大圣模式：什么？尽力了尽力了，很快会和大家见面！"));

        book.addTagElement("pages", bookPages);//页数
        book.addTagElement("generation", IntTag.valueOf(3));//破损度
        book.addTagElement("author", StringTag.valueOf("P1nero"));
        book.addTagElement("title", StringTag.valueOf("史诗战斗：悟空附属游玩指南"));

        event.getPlayer().addItem(book);
        Config.GET_GUILD_BOOK.set(false);
    }

}
