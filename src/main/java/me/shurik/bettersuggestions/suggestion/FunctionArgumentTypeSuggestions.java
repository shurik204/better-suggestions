package me.shurik.bettersuggestions.suggestion;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import me.shurik.bettersuggestions.event.ServerEvents;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

import java.util.List;

import static me.shurik.bettersuggestions.ModConstants.CONFIG;

public class FunctionArgumentTypeSuggestions {
    public static boolean filteredFunctionListInitialized = false;
    public static final List<Identifier> filteredFunctionList = Lists.newArrayList();
    public static void init() {
        FunctionCommand.SUGGESTION_PROVIDER = (context, builder) -> {
            CommandFunctionManager commandFunctionManager = context.getSource().getServer().getCommandFunctionManager();
            CommandSource.suggestIdentifiers(commandFunctionManager.getFunctionTags(), builder, "#");
            if (CONFIG.functionSuggestions.hideUnderscoreFunctions) {
                if (!filteredFunctionListInitialized) {
                    initFilteredFunctionList(context);
                }

                return CommandSource.suggestIdentifiers(filteredFunctionList, builder);
            } else {
                return CommandSource.suggestIdentifiers(commandFunctionManager.getAllFunctions(), builder);
            }
        };

        // Mark the filtered function list to initialize when reloading datapacks
        ServerEvents.START_DATA_PACK_RELOAD.register((server) -> {
            filteredFunctionListInitialized = false;
        });
    }

    private static void initFilteredFunctionList(CommandContext<ServerCommandSource> context) {
        context.getSource().getServer().getCommandFunctionManager().getAllFunctions().forEach((s) -> {
            // Check if the function name starts with "_"
            if (!s.getPath().substring(s.getPath().lastIndexOf("/") + 1).startsWith("_")) {
                filteredFunctionList.add(s);
            }
        });
        filteredFunctionListInitialized = true;
    }
}