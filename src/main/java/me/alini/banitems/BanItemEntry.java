// BanItemEntry.java
package me.alini.banitems;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;

public class BanItemEntry {
    private final String itemId;
    private final String nbtHash;
    private final String nbtString; // 新增：完整 NBT 字符串

    public BanItemEntry(String itemId, String nbtHash, String nbtString) {
        this.itemId = itemId;
        this.nbtHash = nbtHash;
        this.nbtString = nbtString;
    }

    public static BanItemEntry fromStack(ItemStack stack) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String itemId = id != null ? id.toString() : "minecraft:air";
        CompoundTag tag = stack.getTag();
        String nbtHash = null;
        String nbtString = null;
        if (tag != null && !tag.isEmpty()) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(tag.toString().getBytes(StandardCharsets.UTF_8));
                nbtHash = Base64.getEncoder().encodeToString(hash);
                nbtString = tag.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new BanItemEntry(itemId, nbtHash, nbtString);
    }

    public String getKey() {
        return itemId + "#" + (nbtHash != null ? nbtHash : "");
    }

    public ItemStack toStack() {
        ResourceLocation id = ResourceLocation.tryParse(itemId);
        if (id == null) return ItemStack.EMPTY;
        var item = ForgeRegistries.ITEMS.getValue(id);
        if (item == null) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(item);
        if (nbtString != null && !nbtString.isEmpty()) {
            try {
                CompoundTag tag = TagParser.parseTag(nbtString);
                stack.setTag(tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stack;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("itemId", itemId);
        obj.addProperty("nbtHash", nbtHash);
        obj.addProperty("nbtString", nbtString);
        return obj;
    }

    public static BanItemEntry fromJson(JsonObject obj) {
        return new BanItemEntry(
                obj.get("itemId").getAsString(),
                obj.has("nbtHash") && !obj.get("nbtHash").isJsonNull() ? obj.get("nbtHash").getAsString() : null,
                obj.has("nbtString") && !obj.get("nbtString").isJsonNull() ? obj.get("nbtString").getAsString() : null
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BanItemEntry)) return false;
        BanItemEntry that = (BanItemEntry) o;
        return Objects.equals(itemId, that.itemId) &&
                Objects.equals(nbtHash, that.nbtHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, nbtHash);
    }
}