package me.shurik.bettersuggestions.client.suggestion;

import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import me.shurik.bettersuggestions.client.utils.CompletionsContainer;
import me.shurik.bettersuggestions.client.utils.text.TextParser;
import me.shurik.bettersuggestions.client.utils.text.TextCompletions.TextCompletion;

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
        
        CompletionsContainer<TextCompletion> completions = TextParser.getCompletions(input, builder);
        if (completions != null) {
            builder = builder.createOffset(builder.getStart() + builder.getRemaining().length() - completions.getOffset());
            for (TextCompletion completion : completions) {
                suggest(builder, completion);
            }
        }
        
        return builder.buildFuture();
    }

    public static void parseJson(String input) {
        JsonElement element = JsonParser.parseString(input);

        if (element.isJsonArray()) {
            parseArray(element.getAsJsonArray());
        }

        if (element.isJsonObject()) {
            parseObject(element.getAsJsonObject());
        }
    }

    public static void parseArray(JsonArray array) {
        for (JsonElement e : array) {
            if (e.isJsonArray()) {
                parseArray(e.getAsJsonArray());
            } else if (e.isJsonObject()) {
                parseObject(e.getAsJsonObject());
            }


        }
    }

    private static void parseObject(JsonObject asJsonObject) {
    }
}
