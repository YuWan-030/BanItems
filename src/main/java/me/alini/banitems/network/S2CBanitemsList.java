package me.alini.banitems.network;

import me.alini.banitems.Config;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.world.item.ItemStack;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public record S2CBanitemsList(Set<ItemStack> softBanItems, Set<ItemStack> hardBanItems) {
    public S2CBanitemsList(Set<ItemStack> softBanItems, Set<ItemStack> hardBanItems) {
        this.softBanItems = new HashSet<>(softBanItems);
        this.hardBanItems = new HashSet<>(hardBanItems);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(softBanItems.size());
        for (ItemStack stack : softBanItems) buf.writeItem(stack);
        buf.writeVarInt(hardBanItems.size());
        for (ItemStack stack : hardBanItems) buf.writeItem(stack);
    }

    public static S2CBanitemsList decode(FriendlyByteBuf buf) {
        int softSize = buf.readVarInt();
        Set<ItemStack> soft = new HashSet<>();
        for (int i = 0; i < softSize; i++) soft.add(buf.readItem());
        int hardSize = buf.readVarInt();
        Set<ItemStack> hard = new HashSet<>();
        for (int i = 0; i < hardSize; i++) hard.add(buf.readItem());
        return new S2CBanitemsList(soft, hard);
    }

    public static void handle(S2CBanitemsList message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Config.softBanItems.clear();
            message.softBanItems().forEach(stack ->
                    Config.softBanItems.put(stack.hashCode(), me.alini.banitems.BanItemEntry.fromStack(stack))
            );
            Config.hardBanItems.clear();
            message.hardBanItems().forEach(stack ->
                    Config.hardBanItems.put(stack.hashCode(), me.alini.banitems.BanItemEntry.fromStack(stack))
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
