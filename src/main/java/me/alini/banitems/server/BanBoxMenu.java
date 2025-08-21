// src/main/java/me/alini/banitems/server/BanBoxMenu.java
package me.alini.banitems.server;

import me.alini.banitems.Config;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;

public class BanBoxMenu extends ChestMenu {
    private final boolean soft;

    public BanBoxMenu(int id, Inventory inv, boolean soft) {
        super(MenuType.GENERIC_9x6, id, inv, new SimpleContainer(54), 6);
        this.soft = soft;
    }

    @Override
    public void removed(net.minecraft.world.entity.player.Player player) {
        super.removed(player);
        // 关闭时遍历箱子内容，加入封禁列表
        for (int i = 0; i < this.getContainer().getContainerSize(); i++) {
            ItemStack stack = this.getContainer().getItem(i);
            if (!stack.isEmpty()) {
                if (soft) {
                    Config.addSoftBanItem(stack.copy());
                } else {
                    Config.addHardBanItem(stack.copy());
                }
            }
        }
        Config.saveConfig(); // 持久化配置
        me.alini.banitems.server.BanItemSyncHandler.pushBanItemsToAllPlayers();
    }
}
