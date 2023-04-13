package me.shurik.bettersuggestions.suggestion;

import java.util.Collection;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.ScoreHolderArgumentType;

/**
 * Replace the default ScoreHolderArgumentType suggestions with the one from entity selector.
 */
public class ScoreHolderArgumentTypeSuggestions {
    public static void init() {
        ScoreHolderArgumentType.SUGGESTION_PROVIDER = (context, builder) -> {
            Object var4 = context.getSource();
            if (var4 instanceof CommandSource commandSource) {
                StringReader stringReader = new StringReader(builder.getInput());
                stringReader.setCursor(builder.getStart());
                EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, commandSource.hasPermissionLevel(2));
                try {
                    entitySelectorReader.read();
                } catch (CommandSyntaxException var7) {
                }
    
                return entitySelectorReader.listSuggestions(builder, (builderx) -> {
                    Collection<String> collection = commandSource.getPlayerNames();
                    Iterable<String> iterable = Iterables.concat(collection, commandSource.getEntitySuggestions());
                    CommandSource.suggestMatching(iterable, builderx);
                });
            } else {
                return Suggestions.empty();
            }
        };
        // ScoreHolderArgumentType.SUGGESTION_PROVIDER = (context, builder2) -> {
        //     StringReader stringReader = new StringReader(builder2.getInput());
        //     stringReader.setCursor(builder2.getStart());
        //     EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader);
        //     try {
        //         entitySelectorReader.read();
        //     } catch (CommandSyntaxException commandSyntaxException) {
        //         // empty catch block
        //     }
        //     return entitySelectorReader.listSuggestions(builder2, (SuggestionsBuilder builder) -> CommandSource.suggestMatching(context.getSource().getEntitySuggestions(), builder));
        // };
    }

    // public static <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder2) {
    //     S s = context.getSource();
    //     if (s instanceof CommandSource) {
    //         CommandSource commandSource = (CommandSource)s;
    //         StringReader stringReader = new StringReader(builder2.getInput());
    //         stringReader.setCursor(builder2.getStart());
    //         EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, commandSource.hasPermissionLevel(2));
    //         try {
    //             entitySelectorReader.read();
    //         } catch (CommandSyntaxException commandSyntaxException) {
    //             // empty catch block
    //         }
    //         return entitySelectorReader.listSuggestions(builder2, (SuggestionsBuilder builder) -> {
    //             Collection<String> collection = commandSource.getPlayerNames();
    //             Iterable<String> iterable = Iterables.concat(collection, commandSource.getEntitySuggestions());
    //             CommandSource.suggestMatching(iterable, builder);
    //         });
    //     }
    //     return Suggestions.empty();
    // }
}