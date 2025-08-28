package me.alini.banitems.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.alini.banitems.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import me.alini.banitems.server.BanItemMenuOpener;

public class BanItemCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("banitem")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("soft")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    BanItemMenuOpener.openBanBox(player, true);
                                    return 1;
                                })
                        )
                        .then(Commands.literal("hard")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    BanItemMenuOpener.openBanBox(player, false);
                                    return 1;
                                })
                        )
                        .then(Commands.literal("list")
                                .then(Commands.literal("soft")
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            BanItemMenuOpener.openBanList(player, true, 0);
                                            return 1;
                                        })
                                        .then(Commands.argument("page", IntegerArgumentType.integer(0))
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    int page = IntegerArgumentType.getInteger(ctx, "page");
                                                    BanItemMenuOpener.openBanList(player, true, page);
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("hard")
                                        .executes(ctx -> {
                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            BanItemMenuOpener.openBanList(player, false, 0);
                                            return 1;
                                        })
                                        .then(Commands.argument("page", IntegerArgumentType.integer(0))
                                                .executes(ctx -> {
                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    int page = IntegerArgumentType.getInteger(ctx, "page");
                                                    BanItemMenuOpener.openBanList(player, false, page);
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("reload")
                                .requires(src -> src.hasPermission(4))
                                .executes(ctx -> {
                                    Config.loadConfig();
                                    me.alini.banitems.server.BanItemSyncHandler.pushBanItemsToAllPlayers();
                                    ctx.getSource().sendSuccess(() -> Component.translatable("info.banitem.command.reload").withStyle(ChatFormatting.GREEN), true);
                                    return 1;
                                })
                        )
        );
    }
}

