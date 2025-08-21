package me.alini.banitems;

import com.google.gson.annotations.SerializedName;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;

public class BanItemEntry {
    public final String itemId;

    @SerializedName("nbt")
    private final String nbtString; // 用于Gson序列化
    private transient CompoundTag nbt; // 运行时用

    // 用于代码创建
    public BanItemEntry(String itemId, CompoundTag nbt) {
        this.itemId = itemId;
        this.nbt = nbt == null ? null : nbt.copy();
        this.nbtString = nbtToString(this.nbt);
    }

    // 用于Gson反序列化
    public BanItemEntry(String itemId, String nbtString) {
        this.itemId = itemId;
        this.nbtString = nbtString;
        this.nbt = stringToNbt(nbtString);
    }

    public static BanItemEntry fromStack(ItemStack stack) {
        String id = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
        CompoundTag tag = stack.hasTag() ? stack.getTag().copy() : null;
        return new BanItemEntry(id, tag);
    }

    public ItemStack toStack() {
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        if (item == null) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(item);
        if (nbt != null) stack.setTag(nbt.copy());
        return stack;
    }

    // 工具方法：去除 NBT 的 Damage 字段
    private static CompoundTag stripDamage(CompoundTag nbt) {
        if (nbt == null) return null;
        CompoundTag copy = nbt.copy();
        copy.remove("Damage");
        return copy;
    }

    // NBT <-> String
    private static String nbtToString(CompoundTag nbt) {
        if (nbt == null) return null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            NbtIo.writeCompressed(nbt, out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }

    private static CompoundTag stringToNbt(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            byte[] data = Base64.getDecoder().decode(str);
            return NbtIo.readCompressed(new ByteArrayInputStream(data));
        } catch (Exception e) {
            return null;
        }
    }

    // 忽略耐久的hash
    public static int hash(ItemStack stack) {
        String id = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        CompoundTag nbt = stack.hasTag() ? stripDamage(stack.getTag()) : null;
        int nbtHash = nbt != null ? nbt.hashCode() : 0;
        return Objects.hash(id, nbtHash);
    }

    // 忽略耐久的匹配
    public static boolean matches(ItemStack stack, BanItemEntry entry) {
        if (entry == null) return false;
        if (!ForgeRegistries.ITEMS.getKey(stack.getItem()).toString().equals(entry.itemId)) return false;
        CompoundTag stackNbt = stack.hasTag() ? stripDamage(stack.getTag()) : null;
        CompoundTag entryNbt = entry.nbt != null ? stripDamage(entry.nbt) : null;
        if (entryNbt == null && stackNbt == null) return true;
        if (entryNbt != null && stackNbt != null) return entryNbt.equals(stackNbt);
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BanItemEntry other)) return false;
        return Objects.equals(itemId, other.itemId) &&
                Objects.equals(stripDamage(nbt), stripDamage(other.nbt));
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, stripDamage(nbt));
    }
}