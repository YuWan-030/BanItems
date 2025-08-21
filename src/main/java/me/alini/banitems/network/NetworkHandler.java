package me.alini.banitems.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkDirection;

import java.util.Optional;

public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation("banitems", "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        register();
    }

    private static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, S2CBanitemsList.class, S2CBanitemsList::encode, S2CBanitemsList::decode, S2CBanitemsList::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        // 继续注册其他自定义包
    }
}