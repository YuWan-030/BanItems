// src/main/java/me/alini/banitems/util/ItemHashUtils.java
package me.alini.banitems.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 工具类：生成物品唯一 SHA-256 哈希
 */
public class ItemHashUtils {
    /**
     * 生成指定物品的哈希字符串
     * @param stack 要处理的物品
     * @return SHA-256 哈希（小写 hex）
     */
    public static String getHash(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "empty";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 基础：物品注册名 + 数量
            String base = stack.getItem().builtInRegistryHolder().key().location().toString()
                    + "|" + stack.getCount();

            digest.update(base.getBytes(StandardCharsets.UTF_8));

            // 附加：NBT（如果存在）
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                String nbtStr = tag.toString();
                digest.update(nbtStr.getBytes(StandardCharsets.UTF_8));
            }

            // 转 hex
            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
