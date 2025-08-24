package me.alini.banitems;

import me.alini.banitems.item.BannedItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BanItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BanItem.MODID);

    public static final RegistryObject<Item> BANNED_ITEM = ITEMS.register(BannedItem.NAME, BannedItem::new);
}