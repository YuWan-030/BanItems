package me.alini.banitems.server;


import me.alini.banitems.Config;
import me.alini.banitems.network.S2CBanitemsList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import me.alini.banitems.network.NetworkHandler;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = "banitems", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class BanItemSyncHandler {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        pushBanItemsToPlayer(player);
    }

    public static void pushBanItemsToPlayer(ServerPlayer player) {
        S2CBanitemsList packet = new S2CBanitemsList(
            Config.getSoftBanItemStacks().stream().collect(java.util.stream.Collectors.toSet()),
            Config.getHardBanItemStacks().stream().collect(java.util.stream.Collectors.toSet())
        );
        NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void pushBanItemsToAllPlayers() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            pushBanItemsToPlayer(player);
        }
    }
}

