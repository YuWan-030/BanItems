// src/main/java/me/alini/banitems/server/BanListMenu.java
package me.alini.banitems.server;

import me.alini.banitems.BanItemEntry;
import me.alini.banitems.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BanListMenu extends ChestMenu {
    private final boolean soft;
    private final int page;
    private final List<ItemStack> pageItems;

    public BanListMenu(int id, Inventory inv, List<ItemStack> pageItems, boolean soft, int page) {
        super(MenuType.GENERIC_9x6, id, inv, new SimpleContainer(54), 6);
        this.soft = soft;
        this.page = page;
        this.pageItems = pageItems;
        for (int i = 0; i < pageItems.size(); i++) {
            this.getContainer().setItem(i, pageItems.get(i).copy());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < pageItems.size()) {
            ItemStack removed = this.getContainer().getItem(slotId);
            if (!removed.isEmpty()) {
                BanItemEntry entry = BanItemEntry.fromStack(removed);
                if (soft) {
                    Config.softBanItems.remove(entry.getKey());
                } else {
                    Config.hardBanItems.remove(entry.getKey());
                }
                Config.saveConfig();
                me.alini.banitems.server.BanItemSyncHandler.pushBanItemsToAllPlayers();

                // 刷新当前页面内容
                List<ItemStack> newPageItems = soft
                        ? Config.getSoftBanItemStacks()
                        : Config.getHardBanItemStacks();
                int from = page * 54;
                int to = Math.min(from + 54, newPageItems.size());
                List<ItemStack> updatedPageItems = newPageItems.subList(from, to);
                for (int i = 0; i < 54; i++) {
                    this.getContainer().setItem(i, i < updatedPageItems.size() ? updatedPageItems.get(i).copy() : ItemStack.EMPTY);
                }
                this.broadcastChanges(); // 通知客户端刷新
            }
        }
    }
}
