package me.alini.banitems;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Config {
    public static final Logger LOGGER = LogManager.getLogger("BanItems");

    public static final Map<Integer, BanItemEntry> softBanItems = new ConcurrentHashMap<>();
    public static final Map<Integer, BanItemEntry> hardBanItems = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    private static final File configFile = FMLPaths.CONFIGDIR.get().resolve("banitems.json").toFile();

    // 生成哈希key
    public static int getEntryHash(ItemStack stack) {
        return BanItemEntry.hash(stack);
    }

    public static void addSoftBanItem(ItemStack stack) {
        int hash = getEntryHash(stack);
        softBanItems.put(hash, BanItemEntry.fromStack(stack));
    }

    public static void addHardBanItem(ItemStack stack) {
        int hash = getEntryHash(stack);
        hardBanItems.put(hash, BanItemEntry.fromStack(stack));
    }

    public static void removeSoftBanItem(ItemStack stack) {
        int hash = getEntryHash(stack);
        softBanItems.remove(hash);
    }

    public static void removeHardBanItem(ItemStack stack) {
        int hash = getEntryHash(stack);
        hardBanItems.remove(hash);
    }

    public static boolean isSoftBanned(ItemStack stack) {
        int hash = getEntryHash(stack);
        BanItemEntry entry = softBanItems.get(hash);
        return entry != null && BanItemEntry.matches(stack, entry);
    }

    public static boolean isHardBanned(ItemStack stack) {
        int hash = getEntryHash(stack);
        BanItemEntry entry = hardBanItems.get(hash);
        return entry != null && BanItemEntry.matches(stack, entry);
    }

    // 解析物品ID，若无效则记录警告
    public static ItemStack parseItemStack(String id) {
        var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(new ResourceLocation(id));
        if (item.isEmpty() || item.get() == Items.AIR) {
            LOGGER.warn("配置中存在无效物品ID: {}，已忽略。", id);
            return ItemStack.EMPTY;
        }
        return new ItemStack(item.get());
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            BanItemsData data = new BanItemsData(
                    softBanItems.values().stream().collect(java.util.stream.Collectors.toSet()),
                    hardBanItems.values().stream().collect(java.util.stream.Collectors.toSet())
            );
            gson.toJson(data, writer);
        } catch (Exception e) {
            LOGGER.error("保存封禁列表时发生错误: {}", e.getMessage(), e);
        }
    }

    public static void loadConfig() {
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                Type type = new TypeToken<BanItemsData>() {}.getType();
                BanItemsData data = gson.fromJson(reader, type);
                softBanItems.clear();
                if (data != null && data.softBanItems != null) {
                    for (BanItemEntry entry : data.softBanItems) {
                        softBanItems.put(entry.hashCode(), entry);
                    }
                }
                hardBanItems.clear();
                if (data != null && data.hardBanItems != null) {
                    for (BanItemEntry entry : data.hardBanItems) {
                        hardBanItems.put(entry.hashCode(), entry);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("加载封禁列表时发生错误: {}", e.getMessage(), e);
            }
        } else {
            try {
                configFile.getParentFile().mkdirs();
                saveConfig();
                LOGGER.info("未检测到配置文件，已自动创建空的 banitems.json。");
            } catch (Exception e) {
                LOGGER.error("创建空配置文件时发生错误: {}", e.getMessage(), e);
            }
        }
    }

    // 获取ItemStack列表用于界面显示
    public static List<ItemStack> getSoftBanItemStacks() {
        return softBanItems.values().stream().map(BanItemEntry::toStack).collect(Collectors.toList());
    }
    public static List<ItemStack> getHardBanItemStacks() {
        return hardBanItems.values().stream().map(BanItemEntry::toStack).collect(Collectors.toList());
    }

    // 用于Gson序列化
    private static class BanItemsData {
        public java.util.Set<BanItemEntry> softBanItems;
        public java.util.Set<BanItemEntry> hardBanItems;

        public BanItemsData(java.util.Set<BanItemEntry> softBanItems, java.util.Set<BanItemEntry> hardBanItems) {
            this.softBanItems = softBanItems;
            this.hardBanItems = hardBanItems;
        }
    }
}