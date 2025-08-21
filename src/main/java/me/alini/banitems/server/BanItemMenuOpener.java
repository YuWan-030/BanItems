// src/main/java/me/alini/banitems/server/BanItemMenuOpener.java
package me.alini.banitems.server;

import me.alini.banitems.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BanItemMenuOpener {
    // 打开自定义箱子GUI，关闭时将物品加入soft/hardBanItems
    public static void openBanBox(ServerPlayer player, boolean soft) {
        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BanBoxMenu(id, inv, soft),
                Component.literal(soft ? "软封禁箱子" : "硬封禁箱子")
        ));
    }

    // 打开分页展示已封禁物品的GUI
    public static void openBanList(ServerPlayer player, boolean soft, int page) {
        List<ItemStack> banList = soft ? Config.getSoftBanItemStacks() : Config.getHardBanItemStacks();
        int pageSize = 54;
        int from = page * pageSize;
        int to = Math.min(from + pageSize, banList.size());
        List<ItemStack> pageItems = banList.subList(from, to);

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BanListMenu(id, inv, pageItems, soft, page),
                Component.literal((soft ? "软" : "硬") + "封禁物品列表 - 第" + (page + 1) + "页")
        ));
    }
}
