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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;
import java.util.ArrayList;
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
        List<ItemStack> pageItems = new ArrayList<>();
        for (ItemStack stack : banList.subList(from, to)) {
            ItemStack copy = stack.copy();
            CompoundTag tag = copy.getTag();
            if (tag != null && !tag.isEmpty()) {
                List<Component> lore = new ArrayList<>();
                lore.add(Component.literal("NBT:").withStyle(ChatFormatting.GOLD));
                String nbtStr = tag.toString();
                int maxLine = 60;
                for (int i = 0; i < nbtStr.length(); i += maxLine) {
                    int end = Math.min(i + maxLine, nbtStr.length());
                    lore.add(Component.literal(nbtStr.substring(i, end)).withStyle(ChatFormatting.GRAY));
                }
                CompoundTag display = copy.getOrCreateTagElement("display");
                net.minecraft.nbt.ListTag loreTag = new net.minecraft.nbt.ListTag();
                for (Component line : lore) {
                    loreTag.add(net.minecraft.nbt.StringTag.valueOf(Component.Serializer.toJson(line)));
                }
                display.put("Lore", loreTag);
            }
            pageItems.add(copy);
        }

        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new BanListMenu(id, inv, pageItems, soft, page),
                Component.literal((soft ? "软" : "硬") + "封禁物品列表 - 第" + (page + 1) + "页")
        ));
    }
}

