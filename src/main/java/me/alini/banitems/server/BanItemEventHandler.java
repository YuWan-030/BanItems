package me.alini.banitems.server;

import me.alini.banitems.Config;
import me.alini.banitems.item.BannedItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static me.alini.banitems.BanItemRegistry.BANNED_ITEM;

public class BanItemEventHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.level().isClientSide) return;
        if (player.hasPermissions(4)) return;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);

            boolean softBanned = false;
            try {
                softBanned = Config.isSoftBanned(stack);
            } catch (Throwable ignored) {}

            if (softBanned && stack.getItem() != BANNED_ITEM.get()) {
                CompoundTag data = stack.save(new CompoundTag());
                // 保存能力 NBT
                CompoundTag capNBT = null;
                try {
                    var capField = ItemStack.class.getDeclaredField("capNBT");
                    capField.setAccessible(true);
                    capNBT = (CompoundTag) capField.get(stack);
                } catch (Exception ignored) {}
                if (capNBT != null && !capNBT.isEmpty()) {
                    data.put("SavedCapNBT", capNBT.copy());
                }
                data.putInt("SavedCount", stack.getCount());

                ItemStack banned = new ItemStack(BANNED_ITEM.get());
                banned.setCount(1);
                banned.setTag(new CompoundTag());
                banned.getOrCreateTag().put(BannedItem.DATA_KEY, data);

                player.getInventory().setItem(i, banned);
            }
        }
    }
}