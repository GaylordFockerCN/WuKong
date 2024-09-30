package com.p1nero.wukong.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import com.p1nero.wukong.WukongMoveset;
import com.p1nero.wukong.epicfight.WukongSkillCategories;
import com.p1nero.wukong.epicfight.skill.WukongSkills;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.client.input.CombatKeyMapping;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.Collection;
import java.util.Set;

@Mod.EventBusSubscriber(value = {Dist.CLIENT},bus = Mod.EventBusSubscriber.Bus.MOD)
public class WukongKeyMappings {
    public static final MyKeyMapping W = new MyKeyMapping("key.wukong.w", GLFW.GLFW_KEY_W, "key.wukong.category");
    public static final MyKeyMapping CHOP_STYLE = new MyKeyMapping("key.wukong.smash_style", GLFW.GLFW_KEY_Z, "key.wukong.category");
    public static final MyKeyMapping STAND_STYLE = new MyKeyMapping("key.wukong.pillar_style", GLFW.GLFW_KEY_X, "key.wukong.category");
    public static final MyKeyMapping POKE_STYLE = new MyKeyMapping("key.wukong.thrust_style", GLFW.GLFW_KEY_C, "key.wukong.category");
    public static final KeyMapping STAFF_FLOWER = new CombatKeyMapping("key.wukong.staff_spin", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.wukong.category");

    @SubscribeEvent
    public static void registerKeys(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(CHOP_STYLE);
        ClientRegistry.registerKeyBinding(STAND_STYLE);
        ClientRegistry.registerKeyBinding(POKE_STYLE);
        ClientRegistry.registerKeyBinding(STAFF_FLOWER);
    }

    @Mod.EventBusSubscriber(modid = WukongMoveset.MOD_ID)
    public static class HandleClientTick{

        /**
         * 按键切换棍势，确保学过才可以用按键切换。
         */
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer == null) {
                return;
            }
            localPlayer.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
                Collection<Skill> styles = capabilitySkill.getLearnedSkills(WukongSkillCategories.STAFF_STYLE);
                Skill skill;
                if (CHOP_STYLE.isRelease() && styles.contains(WukongSkills.SMASH_STYLE)) {
                    skill = WukongSkills.SMASH_STYLE;
                } else if (POKE_STYLE.isRelease() && styles.contains(WukongSkills.THRUST_STYLE)) {
                    skill = WukongSkills.THRUST_STYLE;
                } else if (STAND_STYLE.isRelease() && styles.contains(WukongSkills.PILLAR_STYLE)) {
                    skill = WukongSkills.PILLAR_STYLE;
                } else {
                    return;
                }
                Set<SkillContainer> skillContainers = capabilitySkill.getSkillContainersFor(WukongSkillCategories.STAFF_STYLE);
                SkillContainer skillContainer = skillContainers.iterator().next();
                skillContainer.setSkill(skill);
                capabilitySkill.addLearnedSkill(skill);
                localPlayer.displayClientMessage(new TranslatableComponent("tips.wukong.style_change").append(skill.getDisplayName()), true);
                EpicFightNetworkManager.sendToServer(new CPChangeSkill(skillContainer.getSlot().universalOrdinal(), -1, skill.toString(), false));
            });
        }
    }

}
