package me.alini.banitems.server;

import me.alini.banitems.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "banitems", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class BanItemSoftBanHandler {

    // 阻止软封禁物品的右键使用（对空气）
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getEntity().isCreative() || event.getEntity().hasPermissions(4)) return;
        ItemStack stack = event.getItemStack();
        if (Config.isSoftBanned(stack)) {
            event.setCanceled(true);
            event.getEntity().sendSystemMessage(Component.literal("§c该物品已被软封禁，无法使用！"));
        }
    }

    // 阻止软封禁物品的右键使用（对方块）
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().isCreative() || event.getEntity().hasPermissions(4)) return;
        ItemStack stack = event.getItemStack();
        if (Config.isSoftBanned(stack)) {
            event.setCanceled(true);
            event.getEntity().sendSystemMessage(Component.literal("§c该物品已被软封禁，无法使用！"));
        }
    }

    // 阻止软封禁物品的长按/消耗（如食物、药水、弓等）
    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.hasPermissions(4)) return;
            ItemStack stack = event.getItem();
            if (Config.isSoftBanned(stack)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("§c该物品已被软封禁，无法使用！"));
            }
        }
    }

    // 阻止软封禁物品的放置
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.isCreative() || player.hasPermissions(4)) return;
            ItemStack stack = player.getMainHandItem();
            if (Config.isSoftBanned(stack)) {
                event.setCanceled(true);
                player.sendSystemMessage(Component.literal("§c该物品已被软封禁，无法放置！"));
            }
        }
    }

    // 阻止软封禁物品攻击实体（左键攻击）
    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().isCreative() || event.getEntity().hasPermissions(4)) return;
        ItemStack stack = event.getEntity().getMainHandItem();
        if (Config.isSoftBanned(stack)) {
            event.setCanceled(true);
            event.getEntity().sendSystemMessage(Component.literal("§c该物品已被软封禁，无法攻击！"));
        }
    }

    // 阻止软封禁物品破坏方块
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (player.isCreative() || player.hasPermissions(4)) return;
        ItemStack stack = player.getMainHandItem();
        if (Config.isSoftBanned(stack)) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("该物品已被软封禁，无法用于破坏方块！").withStyle(net.minecraft.ChatFormatting.RED));
            if (stack.isDamageableItem() && stack.getDamageValue() > 0) {
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
            player.swing(player.getUsedItemHand(), true);
        }
    }
}
