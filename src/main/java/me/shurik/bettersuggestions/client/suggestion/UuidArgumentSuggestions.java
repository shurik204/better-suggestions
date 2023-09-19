package me.shurik.bettersuggestions.client.suggestion;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class UuidArgumentSuggestions {
    public static <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder.suggest(UUID.randomUUID().toString());
        return builder.buildFuture();
    }
}
