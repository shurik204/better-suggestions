package me.shurik.bettersuggestions.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class NoopCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(net.minecraft.server.command.CommandManager.literal("noop").executes((context) -> {
            return Integer.MAX_VALUE;
        }).then(CommandManager.argument("cmd", MessageArgumentType.message())).executes((context) -> {
            return Integer.MAX_VALUE;
        }));
    }
}