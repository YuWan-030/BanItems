// src/main/java/me/alini/banitems/server/BanItemHardBanHandler.java
package me.alini.banitems.server;

import me.alini.banitems.Config;
import me.alini.banitems.BanItemEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "banitems", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class BanItemHardBanHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        // 每 20 tick 检查一次
        if (player.tickCount % 20 != 0) return;

        // OP 或创造模式不检查
        if (player.isCreative() || player.hasPermissions(4)) return;

        boolean notified = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;

            String itemKey = BanItemEntry.fromStack(stack).getKey();

            if (Config.hardBanItems.containsKey(itemKey)) {
                player.getInventory().setItem(i, ItemStack.EMPTY);

                if (!notified && player instanceof ServerPlayer sp) {
                    sp.sendSystemMessage(Component.literal("你携带的物品已被没收（硬封禁）").withStyle(ChatFormatting.RED));
                    notified = true;
                }
            }
        }
    }
}
