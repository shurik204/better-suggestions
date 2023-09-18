package me.shurik.bettersuggestions.client.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class SwizzleArgumentTypeSuggestions {
    private static String[] basicSuggestions = new String[] { "x", "xz", "xyz", "y", "z" };
    private static String[] allSuggestions = new String[] { "x", "y", "z", "xy", "xz", "yx", "yz", "zx", "zy", "xyz", "xzy", "yxz", "yzx", "zxy", "zyx" };
    public static <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();
        if (input.isEmpty()) {
            for (String suggestion : basicSuggestions) {
                builder.suggest(suggestion);
            }
        } else {
            for (String suggestion : allSuggestions) {
                if (suggestion.startsWith(input)) {
                    builder.suggest(suggestion);
                }
            }
        }
        return builder.buildFuture();
    }
}