package me.alini.banitems.client;

import me.alini.banitems.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BanItemTooltipHandler {
    private static boolean shouldDarken = false;

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (Config.isHardBanned(stack)) {
            event.getToolTip().add(1, Component.literal("该物品已被硬封禁").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
            shouldDarken = true;
        } else if (Config.isSoftBanned(stack)) {
            event.getToolTip().add(1, Component.literal("该物品已被软封禁").withStyle(ChatFormatting.RED));
            shouldDarken = true;
        } else {
            shouldDarken = false;
        }
    }
}