/*
package me.shurik.bettersuggestions.client.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.shurik.bettersuggestions.client.utils.CompletionsContainer;
import me.shurik.bettersuggestions.client.utils.text.TextCompletions.TextCompletion;
import me.shurik.bettersuggestions.client.utils.text.TextParser;

import java.util.concurrent.CompletableFuture;

public class TextArgumentSuggestions {
    public static void suggest(SuggestionsBuilder builder, String value) {
        builder.suggest(value);
    }

    public static void suggest(SuggestionsBuilder builder, TextCompletion completion) {
        suggest(builder, completion.value());
    }

    public static void suggestOpenNew(SuggestionsBuilder builder, String input) {
        suggest(builder, "["); // suggest a json array
        suggest(builder, "{"); // suggest a json object
        suggest(builder, "\""); // suggest a string
    }

    public static <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase();

        // if input is empty, then we are at the start of the argument
        // suggest an opening [, { or "
        if (input.isEmpty()) {
            suggestOpenNew(builder, input);
            return builder.buildFuture();
        }

        CompletionsContainer<TextCompletion> completions = TextParser.getCompletions(input, context, builder);
        if (completions != null) {
            builder = builder.createOffset(builder.getStart() + builder.getRemaining().length() - completions.getOffset());
            for (TextCompletion completion : completions) {
                suggest(builder, completion);
            }
        }

        return builder.buildFuture();
    }
}
*/