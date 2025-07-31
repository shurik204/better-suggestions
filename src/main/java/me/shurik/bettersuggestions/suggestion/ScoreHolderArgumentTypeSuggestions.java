package me.shurik.bettersuggestions.suggestion;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.ScoreHolderArgumentType;

import java.util.Collection;

/**
 * Replace the default ScoreHolderArgumentType suggestions with the one from entity selector.
 */
public class ScoreHolderArgumentTypeSuggestions {
    public static void init() {
        ScoreHolderArgumentType.SUGGESTION_PROVIDER = (context, builder) -> {
            Object source = context.getSource();
            if (source instanceof CommandSource commandSource) {
                StringReader reader = new StringReader(builder.getInput());
                reader.setCursor(builder.getStart());
                EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, EntitySelectorReader.shouldAllowAtSelectors(commandSource));
                try {
                    entitySelectorReader.read();
                } catch (CommandSyntaxException e) {
                    // Invalid entity selector
                }

                return entitySelectorReader.listSuggestions(builder, (builderx) -> {
                    // I tried :(
                    // try {
                    //     // Suggest score holder names
                    //     // for the given objective
                    //     ScoreboardObjective objective = ScoreboardObjectiveArgumentType.getObjective(context, "objective");
                    //     Collection<ScoreboardPlayerScore> collection = Scoreboards.getScores(objective);
                    //     CommandSource.suggestMatching(collection.stream().map(ScoreboardPlayerScore::getPlayerName), builderx);
                    // } catch (CommandSyntaxException e) {
                    //     // No objective specified
                    // }
                    Collection<String> collection = commandSource.getPlayerNames();
                    Iterable<String> iterable = Iterables.concat(collection, commandSource.getEntitySuggestions());
                    CommandSource.suggestMatching(iterable, builderx);
                });
            } else {
                return Suggestions.empty();
            }
        };
    }
}