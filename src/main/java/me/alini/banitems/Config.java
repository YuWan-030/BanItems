// src/main/java/me/alini/banitems/Config.java
package me.alini.banitems;

import com.google.gson.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Config {
    private static final File CONFIG_FILE = new File("config/banitems/banitems.json");

    public static final Map<String, BanItemEntry> softBanItems = new LinkedHashMap<>();
    public static final Map<String, BanItemEntry> hardBanItems = new LinkedHashMap<>();
    public static final Set<String> excludedItems = new HashSet<>(); //

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // 添加封禁物品
    public static void addSoftBanItem(ItemStack stack) {
        BanItemEntry entry = BanItemEntry.fromStack(stack);
        softBanItems.put(entry.getKey(), entry);
    }

    public static void addHardBanItem(ItemStack stack) {
        BanItemEntry entry = BanItemEntry.fromStack(stack);
        hardBanItems.put(entry.getKey(), entry);
    }

    // 添加/移除排除物品
    public static void addExcludedItem(String itemId) {
        excludedItems.add(itemId);
    }

    public static void removeExcludedItem(String itemId) {
        excludedItems.remove(itemId);
    }

    public static boolean isExcluded(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null) return false;
        return excludedItems.contains(id.toString());
    }

    // 移除封禁物品
    public static void removeSoftBanItem(ItemStack stack) {
        BanItemEntry entry = BanItemEntry.fromStack(stack);
        softBanItems.remove(entry.getKey());
    }

    public static void removeHardBanItem(ItemStack stack) {
        BanItemEntry entry = BanItemEntry.fromStack(stack);
        hardBanItems.remove(entry.getKey());
    }


    // 获取封禁物品
    public static Collection<BanItemEntry> getSoftBannedItems() {
        return softBanItems.values();
    }

    public static Collection<BanItemEntry> getHardBannedItems() {
        return hardBanItems.values();
    }

    // 获取所有软封禁物品的 ItemStack 列表
    public static java.util.List<ItemStack> getSoftBanItemStacks() {
        return softBanItems.values().stream().map(BanItemEntry::toStack).toList();
    }

    // 获取所有硬封禁物品的 ItemStack 列表
    public static java.util.List<ItemStack> getHardBanItemStacks() {
        return hardBanItems.values().stream().map(BanItemEntry::toStack).toList();
    }

    // 判断是否被封禁
    public static boolean isSoftBanned(ItemStack stack) {
        BanItemEntry entry = BanItemEntry.fromStack(stack);
        return softBanItems.containsKey(entry.getKey());
    }

    public static boolean isHardBanned(ItemStack stack) {
        BanItemEntry entry = BanItemEntry.fromStack(stack);
        return hardBanItems.containsKey(entry.getKey());
    }

    // 保存配置
    public static void saveConfig() {
        JsonObject root = new JsonObject();
        root.add("softBan", serializeBanItems(softBanItems.values()));
        root.add("hardBan", serializeBanItems(hardBanItems.values()));

        // 序列化排除列表
        JsonArray excludeArray = new JsonArray();
        for (String s : excludedItems) excludeArray.add(s);
        root.add("excluded", excludeArray);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //热重载
    public static void reloadConfig() {
        loadConfig();
    }

    // 加载配置
    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            try {
                CONFIG_FILE.getParentFile().mkdirs();
                saveConfig();
            } catch (Exception e) { e.printStackTrace(); }
            return;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            softBanItems.clear();
            hardBanItems.clear();
            excludedItems.clear();

            loadBanItems(root.getAsJsonArray("softBan"), softBanItems);
            loadBanItems(root.getAsJsonArray("hardBan"), hardBanItems);

            JsonArray excludeArray = root.getAsJsonArray("excluded");
            if (excludeArray != null) {
                for (JsonElement el : excludeArray) excludedItems.add(el.getAsString());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // 辅助方法：反序列化
    private static void loadBanItems(JsonArray array, Map<String, BanItemEntry> map) {
        if (array == null) return;
        for (JsonElement el : array) {
            BanItemEntry entry = BanItemEntry.fromJson(el.getAsJsonObject());
            map.put(entry.getKey(), entry);
        }
    }

    // 辅助方法：序列化
    private static JsonArray serializeBanItems(Collection<BanItemEntry> entries) {
        JsonArray array = new JsonArray();
        for (BanItemEntry entry : entries) {
            array.add(entry.toJson());
        }
        return array;
    }
}
