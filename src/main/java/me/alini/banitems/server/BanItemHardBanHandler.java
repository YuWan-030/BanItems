package me.alini.banitems.server;

import me.alini.banitems.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = "banitems", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class BanItemHardBanHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 只在服务端、END 阶段、每 20 tick 检查一次
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        // 仅每 20 tick 检查一次
        if (player.tickCount % 20 != 0) return;
        // 判断玩家是否是OP
        if (player.isCreative() || player.hasPermissions(4)) return;
        // 只提示一次
        boolean notified = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            for (me.alini.banitems.BanItemEntry ban : Config.hardBanItems.values()) {
                if (me.alini.banitems.BanItemEntry.matches(stack, ban)) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                    if (!notified && player instanceof net.minecraft.server.level.ServerPlayer sp) {
                        sp.sendSystemMessage(Component.literal("你携带的物品已被没收（硬封禁）").withStyle(ChatFormatting.RED));
                        notified = true;
                    }
                    break;
                }
            }
        }
    }
}

